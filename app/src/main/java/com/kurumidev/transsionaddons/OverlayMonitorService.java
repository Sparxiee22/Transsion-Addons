package com.kurumidev.transsionaddons;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.Choreographer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;

public class OverlayMonitorService extends Service {
    private static final String CHANNEL_ID = "overlay_monitor";
    private static final int NOTIFICATION_ID = 1207;

    private WindowManager windowManager;
    private TextView overlayView;
    private WindowManager.LayoutParams overlayParams;
    private Handler handler;
    private SharedPreferences prefs;
    private ActivityManager activityManager;
    private int fps;
    private int frameCount;
    private long frameWindowStartNs;
    private boolean dragging;
    private float dragStartRawX;
    private float dragStartRawY;
    private int dragStartX;
    private int dragStartY;
    private int pendingDragX;
    private int pendingDragY;
    private boolean dragFrameScheduled;

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            if (!prefs.getBoolean(OverlayPrefs.KEY_ENABLED, false)) {
                stopSelf();
                return;
            }
            if (!canDrawOverlay()) {
                stopSelf();
                return;
            }
            ensureOverlay();
            if (dragging) {
                handler.postDelayed(this, 1000);
                return;
            }
            applyOverlayStyle();
            updateOverlayText();
            handler.postDelayed(this, 1000);
        }
    };

    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (frameWindowStartNs == 0) {
                frameWindowStartNs = frameTimeNanos;
                frameCount = 0;
            }
            frameCount++;
            long elapsed = frameTimeNanos - frameWindowStartNs;
            if (elapsed >= 1000000000L) {
                fps = Math.round(frameCount * 1000000000f / elapsed);
                frameCount = 0;
                frameWindowStartNs = frameTimeNanos;
            }
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    private final Choreographer.FrameCallback dragFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            dragFrameScheduled = false;
            applyPendingDragPosition();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        prefs = getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE);
        handler = new Handler(Looper.getMainLooper());
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!prefs.getBoolean(OverlayPrefs.KEY_ENABLED, false) || !canDrawOverlay()) {
            stopSelf();
            return START_NOT_STICKY;
        }
        startForegroundServiceNotification();
        ensureOverlay();
        handler.removeCallbacks(updater);
        handler.post(updater);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updater);
        Choreographer.getInstance().removeFrameCallback(frameCallback);
        Choreographer.getInstance().removeFrameCallback(dragFrameCallback);
        if (overlayView != null) {
            try {
                windowManager.removeView(overlayView);
            } catch (RuntimeException ignored) {
            }
            overlayView = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundServiceNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Overlay Monitor",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                openIntent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_IMMUTABLE
                        : 0);
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        Notification notification = builder
                .setSmallIcon(android.R.drawable.presence_online)
                .setContentTitle("Overlay Monitor")
                .setContentText("Touch passthrough monitor is active")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private boolean canDrawOverlay() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
    }

    private void ensureOverlay() {
        if (overlayView != null) {
            updateOverlayPosition();
            return;
        }
        overlayView = new TextView(this);
        overlayView.setIncludeFontPadding(false);
        overlayView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        overlayView.setTextColor(Color.WHITE);
        overlayView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        overlayView.setOnTouchListener((v, event) -> handleOverlayTouch(event));

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        overlayParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                overlayFlags(),
                PixelFormat.TRANSLUCENT);
        updateOverlayPosition();
        applyOverlayStyle();
        windowManager.addView(overlayView, overlayParams);
    }

    private void updateOverlayPosition() {
        if (overlayParams == null || dragging) {
            return;
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_CUSTOM_POSITION, false)) {
            overlayParams.gravity = Gravity.TOP | Gravity.LEFT;
            overlayParams.x = prefs.getInt(OverlayPrefs.KEY_X, dp(10));
            overlayParams.y = prefs.getInt(OverlayPrefs.KEY_Y, dp(10));
            if (overlayView != null) {
                try {
                    windowManager.updateViewLayout(overlayView, overlayParams);
                } catch (RuntimeException ignored) {
                }
            }
            return;
        }
        String position = prefs.getString(OverlayPrefs.KEY_POSITION, OverlayPrefs.DEFAULT_POSITION);
        if (OverlayPrefs.POSITION_TOP_RIGHT.equals(position)) {
            overlayParams.gravity = Gravity.TOP | Gravity.RIGHT;
        } else if (OverlayPrefs.POSITION_BOTTOM_LEFT.equals(position)) {
            overlayParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        } else if (OverlayPrefs.POSITION_BOTTOM_RIGHT.equals(position)) {
            overlayParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        } else {
            overlayParams.gravity = Gravity.TOP | Gravity.LEFT;
        }
        overlayParams.x = dp(10);
        overlayParams.y = dp(10);
        if (overlayView != null) {
            try {
                windowManager.updateViewLayout(overlayView, overlayParams);
            } catch (RuntimeException ignored) {
            }
        }
    }

    private int overlayFlags() {
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if (!prefs.getBoolean(OverlayPrefs.KEY_DRAG_MODE, false)) {
            flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        return flags;
    }

    private boolean handleOverlayTouch(MotionEvent event) {
        if (!prefs.getBoolean(OverlayPrefs.KEY_DRAG_MODE, false) || overlayParams == null) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dragging = true;
                int[] location = new int[2];
                overlayView.getLocationOnScreen(location);
                overlayParams.gravity = Gravity.TOP | Gravity.LEFT;
                overlayParams.x = location[0];
                overlayParams.y = location[1];
                dragStartX = overlayParams.x;
                dragStartY = overlayParams.y;
                pendingDragX = overlayParams.x;
                pendingDragY = overlayParams.y;
                dragStartRawX = event.getRawX();
                dragStartRawY = event.getRawY();
                applyPendingDragPosition();
                return true;
            case MotionEvent.ACTION_MOVE:
                pendingDragX = dragStartX + Math.round(event.getRawX() - dragStartRawX);
                pendingDragY = dragStartY + Math.round(event.getRawY() - dragStartRawY);
                scheduleDragFrame();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pendingDragX = dragStartX + Math.round(event.getRawX() - dragStartRawX);
                pendingDragY = dragStartY + Math.round(event.getRawY() - dragStartRawY);
                Choreographer.getInstance().removeFrameCallback(dragFrameCallback);
                dragFrameScheduled = false;
                applyPendingDragPosition();
                dragging = false;
                prefs.edit()
                        .putBoolean(OverlayPrefs.KEY_CUSTOM_POSITION, true)
                        .putInt(OverlayPrefs.KEY_X, overlayParams.x)
                        .putInt(OverlayPrefs.KEY_Y, overlayParams.y)
                        .apply();
                return true;
            default:
                return true;
        }
    }

    private void scheduleDragFrame() {
        if (dragFrameScheduled) {
            return;
        }
        dragFrameScheduled = true;
        Choreographer.getInstance().postFrameCallback(dragFrameCallback);
    }

    private void applyPendingDragPosition() {
        if (overlayView == null || overlayParams == null) {
            return;
        }
        overlayParams.x = pendingDragX;
        overlayParams.y = pendingDragY;
        try {
            windowManager.updateViewLayout(overlayView, overlayParams);
        } catch (RuntimeException ignored) {
        }
    }

    private void applyOverlayStyle() {
        if (overlayView == null) {
            return;
        }
        int textSize = clamp(prefs.getInt(OverlayPrefs.KEY_TEXT_SIZE, OverlayPrefs.DEFAULT_TEXT_SIZE), 8, 40);
        int opacity = clamp(prefs.getInt(OverlayPrefs.KEY_OPACITY, OverlayPrefs.DEFAULT_OPACITY), 10, 100);
        boolean shadow = prefs.getBoolean(OverlayPrefs.KEY_SHADOW, OverlayPrefs.DEFAULT_SHADOW);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.argb(255, 0, 0, 0));
        background.setCornerRadius(dp(8));
        overlayView.setBackground(background);
        if (overlayParams != null) {
            overlayParams.flags = overlayFlags();
            overlayParams.alpha = Math.min(0.80f, opacity / 100f);
        }
        overlayView.setPadding(dp(8), dp(6), dp(8), dp(6));
        overlayView.setTextSize(textSize);
        if (shadow) {
            overlayView.setShadowLayer(dp(2), 1f, 1f, Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                overlayView.setElevation(dp(6));
            }
        } else {
            overlayView.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                overlayView.setElevation(0f);
            }
        }
        updateOverlayPosition();
    }

    private void updateOverlayText() {
        ArrayList<String> lines = new ArrayList<>();
        if (prefs.getBoolean(OverlayPrefs.KEY_DRAG_MODE, false)) {
            lines.add("DRAG MODE");
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_CPU_FREQ, true)) {
            lines.add("CPU " + cpuFrequencyText());
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_GPU_FREQ, true)) {
            lines.add("GPU " + gpuFrequencyText());
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_RAM, true)) {
            lines.add("RAM " + ramText());
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_FPS, true)) {
            lines.add("FPS " + fps);
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_BATTERY_PERCENT, true)) {
            lines.add("BAT " + batteryPercentText());
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_BATTERY_TEMP, true)) {
            lines.add("BATT " + batteryTemperatureText());
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_CPU_TEMP, true)) {
            lines.add("CPUt " + thermalText(true));
        }
        if (prefs.getBoolean(OverlayPrefs.KEY_SHOW_GPU_TEMP, true)) {
            lines.add("GPUt " + thermalText(false));
        }
        if (lines.size() == 0) {
            lines.add("Overlay Monitor");
        }
        overlayView.setText(joinLines(lines));
    }

    private String cpuFrequencyText() {
        File base = new File("/sys/devices/system/cpu/cpufreq");
        File[] policies = base.listFiles();
        if (policies == null) {
            return "N/A";
        }
        ArrayList<String> values = new ArrayList<>();
        for (File policy : policies) {
            if (!policy.getName().startsWith("policy")) {
                continue;
            }
            long freq = readLongFile(new File(policy, "scaling_cur_freq"));
            if (freq <= 0) {
                freq = readLongFile(new File(policy, "cpuinfo_cur_freq"));
            }
            if (freq > 0) {
                values.add(formatFrequency(freq));
            }
        }
        LinkedHashSet<String> unique = new LinkedHashSet<>(values);
        values = new ArrayList<>(unique);
        Collections.sort(values);
        if (values.size() == 0) {
            return "N/A";
        }
        if (values.size() > 3) {
            values = new ArrayList<>(values.subList(0, 3));
        }
        return joinSlash(values);
    }

    private String gpuFrequencyText() {
        long freq = 0;
        File devfreq = new File("/sys/class/devfreq");
        File[] devices = devfreq.listFiles();
        if (devices != null) {
            for (File device : devices) {
                String name = device.getName().toLowerCase(Locale.US);
                if (name.contains("gpu") || name.contains("mali") || name.contains("adreno") ||
                        name.contains("kgsl") || name.contains("pvr")) {
                    freq = readLongFile(new File(device, "cur_freq"));
                    if (freq > 0) {
                        break;
                    }
                }
            }
        }
        if (freq <= 0) {
            freq = readLongFile(new File("/sys/class/kgsl/kgsl-3d0/gpuclk"));
        }
        return freq > 0 ? formatFrequency(freq) : "N/A";
    }

    private String ramText() {
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        long used = info.totalMem - info.availMem;
        return formatBytes(used) + "/" + formatBytes(info.totalMem);
    }

    private String batteryPercentText() {
        long value = readLongFile(new File("/sys/class/power_supply/battery/capacity"));
        return value >= 0 ? value + "%" : "N/A";
    }

    private String batteryTemperatureText() {
        long raw = readLongFile(new File("/sys/class/power_supply/battery/temp"));
        return raw >= 0 ? formatTemperature(raw) : "N/A";
    }

    private String thermalText(boolean cpu) {
        File[] zones = new File("/sys/class/thermal").listFiles();
        if (zones == null) {
            return "N/A";
        }
        double fallback = -999d;
        for (File zone : zones) {
            if (!zone.getName().startsWith("thermal_zone")) {
                continue;
            }
            String type = readTextFile(new File(zone, "type")).toLowerCase(Locale.US);
            long raw = readLongFile(new File(zone, "temp"));
            if (type.length() == 0 || raw < 0) {
                continue;
            }
            double temp = normalizeTemperature(raw);
            if (temp < -20d || temp > 140d) {
                continue;
            }
            boolean gpuZone = type.contains("gpu") || type.contains("mali") ||
                    type.contains("adreno") || type.contains("kgsl");
            boolean batteryZone = type.contains("battery") || type.contains("batt");
            boolean cpuZone = type.contains("cpu") || type.contains("soc") ||
                    type.equals("ap") || type.contains("ap-") || type.contains("board");
            if (!batteryZone && temp > fallback) {
                fallback = temp;
            }
            if (!cpu && gpuZone) {
                return formatTemperatureValue(temp);
            }
            if (cpu && cpuZone && !gpuZone && !batteryZone) {
                return formatTemperatureValue(temp);
            }
        }
        return cpu && fallback > -999d ? formatTemperatureValue(fallback) : "N/A";
    }

    private long readLongFile(File file) {
        String text = readTextFile(file);
        if (text.length() == 0) {
            return -1;
        }
        try {
            return Long.parseLong(text.trim().split("\\s+")[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String readTextFile(File file) {
        if (file == null || !file.exists()) {
            return "";
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            return line == null ? "" : line.trim();
        } catch (Exception e) {
            return "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private String formatFrequency(long value) {
        long mhz = value > 10000000L ? Math.round(value / 1000000f) : Math.round(value / 1000f);
        return mhz + "MHz";
    }

    private String formatBytes(long bytes) {
        if (bytes >= 1073741824L) {
            return String.format(Locale.US, "%.1fG", bytes / 1073741824f);
        }
        return Math.round(bytes / 1048576f) + "M";
    }

    private String formatTemperature(long raw) {
        return formatTemperatureValue(normalizeTemperature(raw));
    }

    private double normalizeTemperature(long raw) {
        long abs = Math.abs(raw);
        if (abs > 10000L) {
            return raw / 1000d;
        }
        if (abs > 100L) {
            return raw / 10d;
        }
        return raw;
    }

    private String formatTemperatureValue(double value) {
        return String.format(Locale.US, "%.1fC", value);
    }

    private String joinLines(ArrayList<String> lines) {
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line);
        }
        return builder.toString();
    }

    private String joinSlash(ArrayList<String> items) {
        StringBuilder builder = new StringBuilder();
        for (String item : items) {
            if (builder.length() > 0) {
                builder.append('/');
            }
            builder.append(item);
        }
        return builder.toString();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
