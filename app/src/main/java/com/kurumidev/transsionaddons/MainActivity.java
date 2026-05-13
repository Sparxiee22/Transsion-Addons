package com.kurumidev.transsionaddons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(245, 245, 245);
    private static final int SUMMARY = Color.rgb(150, 150, 150);
    private static final int DIVIDER = Color.rgb(232, 232, 232);
    private static final int SWITCH_OFF = Color.rgb(217, 217, 217);
    private static final int SWITCH_ON = Color.rgb(33, 132, 245);
    private static final String CHARGE_CURRENT_NODES =
            "/sys/class/power_supply/battery/constant_charge_current " +
            "/sys/class/power_supply/sc8950x_charger/constant_charge_current " +
            "/sys/class/power_supply/tran_charger/constant_charge_current " +
            "/sys/class/power_supply/*/constant_charge_current " +
            "/sys/devices/platform/*charger*/power_supply/*/constant_charge_current";
    private static final String INPUT_CURRENT_NODES =
            "/sys/class/power_supply/battery/input_current_limit " +
            "/sys/class/power_supply/sc8950x_charger/input_current_limit " +
            "/sys/class/power_supply/tran_charger/input_current_limit " +
            "/sys/class/power_supply/*/input_current_limit " +
            "/sys/devices/platform/*charger*/power_supply/*/input_current_limit";
    private static final String QUICK_CHARGE_NODE = "/sys/class/power_supply/tran-auto-test/quick_chg_back";
    private static final String CHARGE_SPEED_NODE = "/sys/devices/platform/tran_charger/chgspeed_ctl";
    private static final String BYPASS_CHARGE_NODE = "/sys/devices/platform/charger-manager/tran_bypass_disable_charger";
    private static final String AICHG_DISABLE_NODE = "/sys/devices/platform/charger-manager/tran_aichg_disable_charger";
    private static final String AI_CHARGE_STOP_NODE = "/sys/class/power_supply/battery/charger.0/stop_charge";
    private static final String BATTERY_CAPACITY_NODE = "/sys/class/power_supply/battery/capacity";
    private static final String AI_CHARGE_SCRIPT = "/data/adb/service.d/transsionaddons_aicharge.sh";
    private static final String AI_CHARGE_PID = "/data/local/tmp/transsionaddons_aicharge.pid";
    private static final String HWUI_RENDERER_PROP = "debug.hwui.renderer";
    private static final String RENDERENGINE_BACKEND_PROP = "debug.renderengine.backend";
    private static final String ANGLE_SUPPORTED_PROP = "ro.gfx.angle.supported";
    private static final String ANGLE_DEVELOPER_PROP = "debug.graphics.angle.developeroption.enable";
    private static final String PREF_AI_CHARGE_LIMIT = "ai_charge_limit";
    private static final String PREF_DISPLAY_RESOLUTION = "display_resolution";
    private static final String PREF_DISPLAY_REFRESH = "display_refresh";
    private static final String PREF_FORCE_PEAK_REFRESH = "display_force_peak_refresh";
    private static final String PREF_SHOW_REFRESH_RATE = "display_show_refresh_rate";
    private static final String PREF_GAME_FPS_OVERRIDE = "display_game_fps_override";
    private static final String PREF_DISABLE_HW_OVERLAYS = "display_disable_hw_overlays";
    private static final String PREF_FORCE_MSAA = "kernel_force_msaa";
    private static final String PREF_LED_CHANNEL = "led_channel";
    private static final String PREF_DEVFREQ_DEVICE = "devfreq_device";
    private static final String GAME_SPACE_PACKAGE = "com.transsion.gamespace.app";
    private static final String GAME_SPACE_ACTIVITY = "com.transsion.gamespace.activity.GameSpaceActivity";
    private static final String SMART_PANEL_PACKAGE = "com.transsion.smartpanel";
    private static final String SMART_PANEL_PQE_ACTION = "com.transsion.game.PQE_SETTINGS";
    private static final String SMART_PANEL_PQE_ACTIVITY = "com.transsion.gamemode.activity.PqeSettingsActivity";
    private static final String SMART_PANEL_FRAME_ACTIVITY = "com.transsion.gamemode.activity.FrameInterpolationSettingsActivity";
    private static final String SMART_PANEL_FEATURE_PREF = "/data/user/0/com.transsion.smartpanel/shared_prefs/feature_filter_list.xml";
    private static final String GAME_SPACE_PROVIDER_URI = "content://com.transsion.gamemode.provider/listapp";
    private static final String DISPLAY_SCRIPT = "/data/adb/service.d/transsionaddons_display.sh";
    private static final String TRAN_DEFAULT_REFRESH_MODE_PROP = "ro.tran_default_refresh_mode";
    private static final String GAME_FPS_OVERRIDE_PROP = "ro.surface_flinger.game_default_frame_rate_override";
    private static final String FORCE_MSAA_PROP = "debug.egl.force_msaa";
    private static final String FORCE_MSAA_SCRIPT = "/data/adb/service.d/transsionaddons_msaa.sh";
    private static final String SURFACE_FLINGER_SHOW_REFRESH_RATE = "1034";
    private static final String PREF_GAME_PACKAGE = "game_driver_package";
    private static final String PREF_GAME_LABEL = "game_driver_label";
    private static final String PREF_GAME_MODE = "game_driver_mode";
    private static final String PREF_SMART_PANEL_WHITELIST_PACKAGES = "smart_panel_whitelist_packages";
    private static final String PREF_SMART_PANEL_WHITELIST_STATUS = "smart_panel_whitelist_status";
    private static final String PREF_GAME_SPACE_PROVIDER_PACKAGES = "game_space_provider_packages";
    private static final String DRIVER_DEFAULT = "default";
    private static final String DRIVER_GAME = "game";
    private static final String DRIVER_SYSTEM = "system";
    private static final String DRIVER_DEVELOPER = "developer";
    private static final String GAME_DRIVER_OPT_IN = "game_driver_opt_in_apps";
    private static final String DRIVER_PRODUCTION_OPT_IN = "updatable_driver_production_opt_in_apps";
    private static final String DRIVER_PRODUCTION_OPT_OUT = "updatable_driver_production_opt_out_apps";
    private static final String DRIVER_PRERELEASE_OPT_IN = "updatable_driver_prerelease_opt_in_apps";
    private static final String UFS_LIFE_A_NODES =
            "/sys/devices/platform/soc/soc:ap-apb/20200000.ufs/health_descriptor/life_time_estimation_a " +
            "/sys/devices/platform/soc/1d84000.ufshc/health/lifetimeA " +
            "/sys/devices/platform/*ufs*/health_descriptor/life_time_estimation_a " +
            "/sys/devices/platform/*/*.ufs*/health_descriptor/life_time_estimation_a " +
            "/sys/devices/platform/soc/*.ufshc/health/lifetimeA " +
            "/sys/bus/platform/devices/*ufs*/health_descriptor/life_time_estimation_a " +
            "/sys/bus/platform/devices/*.ufshc/health/lifetimeA";
    private static final String UFS_LIFE_B_NODES =
            "/sys/devices/platform/soc/soc:ap-apb/20200000.ufs/health_descriptor/life_time_estimation_b " +
            "/sys/devices/platform/soc/1d84000.ufshc/health/lifetimeB " +
            "/sys/devices/platform/*ufs*/health_descriptor/life_time_estimation_b " +
            "/sys/devices/platform/*/*.ufs*/health_descriptor/life_time_estimation_b " +
            "/sys/devices/platform/soc/*.ufshc/health/lifetimeB " +
            "/sys/bus/platform/devices/*ufs*/health_descriptor/life_time_estimation_b " +
            "/sys/bus/platform/devices/*.ufshc/health/lifetimeB";
    private static final String EMMC_LIFE_A_NODES =
            "/sys/class/mmc_host/mmc0/mmc0:0001/life_time " +
            "/sys/block/mmcblk0/device/life_time " +
            "/sys/class/mmc_host/mmc*/mmc*:*/life_time " +
            "/sys/block/mmcblk*/device/life_time " +
            "/sys/bus/mmc/devices/mmc*:*/life_time";
    private static final String EMMC_LIFE_B_NODES =
            "/sys/class/mmc_host/mmc0/mmc0:0001/life_time_b " +
            "/sys/block/mmcblk0/device/life_time_b " +
            "/sys/class/mmc_host/mmc*/mmc*:*/life_time_b " +
            "/sys/block/mmcblk*/device/life_time_b " +
            "/sys/bus/mmc/devices/mmc*:*/life_time_b";
    private static final String[] RENDERER_VALUES = {
            "default", "opengl", "threaded", "skiagl", "skiaglthreaded", "skiavk", "skiavkthreaded"
    };
    private static final String[] RENDERER_LABELS = {
            "Default", "OpenGL", "Threaded", "SkiaGL", "SkiaGL Threaded", "SkiaVK", "SkiaVK Threaded"
    };
    private static final String[] GAME_DRIVER_VALUES = {
            DRIVER_DEFAULT, DRIVER_GAME, DRIVER_SYSTEM, DRIVER_DEVELOPER
    };
    private static final String[] GAME_DRIVER_LABELS = {
            "Default / Reset", "Game Driver", "System Driver", "Developer Driver"
    };
    private static final String[] AI_CHARGE_LIMITS = {"70", "80", "90", "100"};
    private static final String[] DISPLAY_RESOLUTION_VALUES = {"720", "1080", "1024", "1440", "custom"};
    private static final String[] DISPLAY_RESOLUTION_LABELS = {"720p", "1080p", "1K", "2K", "Custom"};
    private static final String[] DISPLAY_REFRESH_VALUES = {"60", "90", "120", "custom"};
    private static final String[] DISPLAY_REFRESH_LABELS = {"60Hz", "90Hz", "120Hz", "Custom"};
    private static final String[] GAME_FPS_VALUES = {"60", "90", "120", "custom"};
    private static final String[] GAME_FPS_LABELS = {"60Hz", "90Hz", "120Hz", "Custom"};
    private static final String IO_SCHEDULER_NODES =
            "/sys/block/sd*/queue/scheduler /sys/block/mmcblk*/queue/scheduler " +
            "/sys/block/nvme*n*/queue/scheduler /sys/block/vd*/queue/scheduler";
    private static final String BLOCK_QUEUE_PATHS =
            "/sys/block/sd*/queue /sys/block/mmcblk*/queue /sys/block/nvme*n*/queue /sys/block/vd*/queue";
    private static final String ZRAM_BLOCK_NODE = "/dev/block/zram0";
    private static final String ZRAM_ALT_BLOCK_NODE = "/dev/zram0";
    private static final String ZRAM_DISKSIZE_NODE = "/sys/block/zram0/disksize";
    private static final String ZRAM_COMP_ALGORITHM_NODE = "/sys/block/zram0/comp_algorithm";
    private static final String ZRAM_RESET_NODE = "/sys/block/zram0/reset";
    private static final String ZRAM_MAX_COMP_STREAMS_NODE = "/sys/block/zram0/max_comp_streams";
    private static final String ZRAM_USE_DEDUP_NODE = "/sys/block/zram0/use_dedup";
    private static final String ZRAM_COMPACT_NODE = "/sys/block/zram0/compact";
    private static final String VM_SWAPPINESS_NODE = "/proc/sys/vm/swappiness";
    private static final String VM_DIRTY_RATIO_NODE = "/proc/sys/vm/dirty_ratio";
    private static final String VM_DIRTY_BACKGROUND_RATIO_NODE = "/proc/sys/vm/dirty_background_ratio";
    private static final String VM_DIRTY_WRITEBACK_NODE = "/proc/sys/vm/dirty_writeback_centisecs";
    private static final String VM_MIN_FREE_KBYTES_NODE = "/proc/sys/vm/min_free_kbytes";
    private static final String VM_OOM_KILL_ALLOCATING_TASK_NODE = "/proc/sys/vm/oom_kill_allocating_task";
    private static final String VM_OVERCOMMIT_RATIO_NODE = "/proc/sys/vm/overcommit_ratio";
    private static final String VM_LAPTOP_MODE_NODE = "/proc/sys/vm/laptop_mode";
    private static final String VM_VFS_CACHE_PRESSURE_NODE = "/proc/sys/vm/vfs_cache_pressure";
    private static final String SCHED_CHILD_RUNS_FIRST_NODE = "/proc/sys/kernel/sched_child_runs_first";
    private static final String SCHED_DEADLINE_PERIOD_MAX_NODE = "/proc/sys/kernel/sched_deadline_period_max_us";
    private static final String SCHED_DEADLINE_PERIOD_MIN_NODE = "/proc/sys/kernel/sched_deadline_period_min_us";
    private static final String SCHED_ENERGY_AWARE_NODE = "/proc/sys/kernel/sched_energy_aware";
    private static final String SCHED_PELT_MULTIPLIER_NODE = "/proc/sys/kernel/sched_pelt_multiplier";
    private static final String SCHED_RR_TIMESLICE_NODE = "/proc/sys/kernel/sched_rr_timeslice_ms";
    private static final String SCHED_RT_PERIOD_NODE = "/proc/sys/kernel/sched_rt_period_us";
    private static final String SCHED_SCHEDSTATS_NODE = "/proc/sys/kernel/sched_schedstats";
    private static final String SCHED_UTIL_CLAMP_MAX_NODE = "/proc/sys/kernel/sched_util_clamp_max";
    private static final String SCHED_UTIL_CLAMP_MIN_NODE = "/proc/sys/kernel/sched_util_clamp_min";
    private static final String SCHED_UTIL_CLAMP_MIN_RT_DEFAULT_NODE = "/proc/sys/kernel/sched_util_clamp_min_rt_default";
    private static final String TCP_CONGESTION_CONTROL_NODE = "/proc/sys/net/ipv4/tcp_congestion_control";
    private static final String TCP_AVAILABLE_CONGESTION_CONTROL_NODE = "/proc/sys/net/ipv4/tcp_available_congestion_control";
    private static final String TCP_FASTOPEN_NODE = "/proc/sys/net/ipv4/tcp_fastopen";
    private static final String TCP_ECN_NODE = "/proc/sys/net/ipv4/tcp_ecn";
    private static final String TCP_TIMESTAMPS_NODE = "/proc/sys/net/ipv4/tcp_timestamps";
    private static final String TCP_LOW_LATENCY_NODE = "/proc/sys/net/ipv4/tcp_low_latency";
    private static final String TCP_SLOW_START_AFTER_IDLE_NODE = "/proc/sys/net/ipv4/tcp_slow_start_after_idle";
    private static final String TCP_TW_REUSE_NODE = "/proc/sys/net/ipv4/tcp_tw_reuse";
    private static final String TCP_SYNCOOKIES_NODE = "/proc/sys/net/ipv4/tcp_syncookies";
    private static final String TCP_MTU_PROBING_NODE = "/proc/sys/net/ipv4/tcp_mtu_probing";
    private static final String PSTORE_COMPRESS_NODE = "/sys/module/pstore/parameters/compress";
    private static final String ENTROPY_WRITE_WAKEUP_NODE = "/proc/sys/kernel/random/write_wakeup_threshold";
    private static final String DISPLAY_PANEL_FPS_NODES =
            "/sys/class/display/panel0/panel_fps /sys/class/graphics/fb0/panel_fps";
    private static final String DISPLAY_ACTUAL_FPS_NODES =
            "/sys/class/display/dispc0/actual_fps /sys/class/graphics/fb0/actual_fps";
    private static final String DISPLAY_MAX_FPS_NODES =
            "/sys/class/display/panel0/max_fps /sys/class/graphics/fb0/max_fps";
    private static final String DISPLAY_PIXEL_CLOCK_NODES =
            "/sys/class/display/panel0/pixel_clock /sys/class/graphics/fb0/pixel_clock";
    private static final String DISPLAY_SCREEN_SIZE_NODES =
            "/sys/class/display/panel0/screen_size /sys/class/graphics/fb0/screen_size";
    private static final String DISPLAY_BACKLIGHT_BRIGHTNESS_NODES =
            "/sys/class/backlight/*/brightness";
    private static final String DISPLAY_BACKLIGHT_MAX_NODES =
            "/sys/class/backlight/*/max_brightness";
    private static final String DISPLAY_ESD_CHECK_ENABLE_NODE = "/sys/class/display/panel0/esd_check_enable";
    private static final String DISPLAY_ESD_CHECK_PERIOD_NODE = "/sys/class/display/panel0/esd_check_period";
    private static final String GPU_POLLINGTIME_NODE = "/sys/module/mali_kbase/parameters/gpu_pollingtime";
    private static final String GPU_UPTHRESHOLD_NODE = "/sys/module/mali_kbase/parameters/gpu_upthreshold";
    private static final String GPU_DOWNDIFFERENTIAL_NODE = "/sys/module/mali_kbase/parameters/gpu_downdifferential";
    private static final String GPU_BOOST_LEVEL2_NODE = "/sys/module/mali_kbase/parameters/gpu_boost_level2";
    private static final String[] GAME_MODE_VALUES = {"0", "1", "2"};
    private static final String[] GAME_MODE_LABELS = {"Power Save", "Balanced", "Performance"};
    private static final String[] PQE_MODE_VALUES = {"0", "1", "2", "3"};
    private static final String[] PQE_MODE_LABELS = {"Classic", "Colorful", "Soft", "Realistic"};
    private static final String[] SMART_PANEL_SUPPORT_KEYS = {
            "com_transsion_smartpanel_game_packages",
            "com_transsion_smartpanel_optimization_packages_name",
            "com_transsion_smartpanel_pqe",
            "com_transsion_smartpanel_def_video",
            "com_transsion_smartpanel_virtual_control_list",
            "com_transsion_smartpanel_network_control_list",
            "com_transsion_smartpanel_sound_effects_list",
            "com_transsion_smartpanel_sound_effects_list2",
            "com_transsion_smartpanel_esport_mode_list"
    };
    private static final String[] KNOWN_GAME_SUPPORT_PACKAGES = {
            "com.mobile.legends",
            "com.tencent.ig",
            "com.tencent.tmgp.pubgmhd",
            "vn.vng.pubgmobile",
            "com.vng.pubgmobile",
            "com.pubg.krmobile",
            "com.dts.freefireth",
            "com.activision.callofduty.shooter",
            "com.garena.game.codm",
            "com.HoYoverse.hkrpgoversea",
            "com.miHoYo.GenshinImpact",
            "com.miHoYo.Yuanshen",
            "com.kurogame.wutheringwaves.global",
            "com.kurogame.wutheringwaves",
            "com.kurogame.mingchao",
            "com.levelinfinite.sgameGlobal",
            "com.proximabeta.nikke",
            "com.seasun.snowbreak.google",
            "com.YoStarEN.Arknights",
            "com.HoYoverse.Nap",
            "com.miHoYo.bh3global"
    };
    private static final String[] ZRAM_SIZE_VALUES = {
            "4GB", "5GB", "6GB", "7GB", "8GB", "9GB", "10GB", "11GB", "12GB"
    };
    private static final String[] ZRAM_ALGORITHM_PREFERENCE = {"lz4", "zstd", "lzo-rle", "lzo"};
    private static final String[] PSTORE_COMPRESS_VALUES = {"deflate", "lzo", "lz4", "zstd", "none"};
    private static final String[] SCHED_PELT_MULTIPLIER_VALUES = {"1", "2", "4"};
    private static final String LED_CLASS_PATH = "/sys/class/leds";
    private TextView rendererStatusView;
    private TextView gameDriverValueView;
    private TextView storageTypeView;
    private TextView storageLifeAView;
    private TextView storageLifeBView;
    private TextView aiChargeStatusView;
    private TextView displayResolutionView;
    private TextView displayRefreshView;
    private TextView gameFpsView;

    private LinearLayout root;
    private PopupWindow activePopup;
    private TextView activeArrow;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private Runnable autoRefreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMainPage();
        if (overlayMonitorEnabled() && canDrawOverlay()) {
            startOverlayMonitorService();
        }
    }

    @Override
    protected void onDestroy() {
        stopAutoRefresh();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (activePopup != null && activePopup.isShowing()) {
            activePopup.dismiss();
            return;
        }
        Object page = root == null ? null : root.getTag();
        if (page != null && !"main".equals(page)) {
            showMainPage();
            return;
        }
        super.onBackPressed();
    }

    private void showMainPage() {
        if (useMenuHome()) {
            showHomePage();
            return;
        }
        startPage("Transsion Addons", false);
        root.setTag("main");

        addSectionTitle("Rendering Change");
        LinearLayout rendering = card();
        rendering.addView(settingRendererRow());
        rendering.addView(dividerView());
        rendering.addView(settingSwitchRow(
                "ANGLE Developer Option",
                "Show ANGLE driver option in Developer Options",
                true,
                true,
                angleDeveloperOptionEnabled(),
                checked -> setAngleDeveloperOption(checked)));
        rendering.addView(settingActionRow(
                "Reboot To Apply",
                "Apply renderer safely after reboot",
                v -> rebootDevice()));
        rendering.addView(dividerView());
        rendering.addView(settingRendererStatusRow());
        root.addView(rendering);

        addSectionTitle("Game Driver Preference");
        LinearLayout gameDriver = card();
        gameDriver.addView(settingGameAppRow());
        gameDriver.addView(dividerView());
        gameDriver.addView(settingGameDriverRow());
        gameDriver.addView(dividerView());
        gameDriver.addView(settingActionRow(
                "Apply Driver",
                "Set selected app driver preference",
                v -> applyGameDriverPreference()));
        gameDriver.addView(dividerView());
        gameDriver.addView(settingActionRow(
                "Run App",
                "Open selected app after applying driver",
                v -> runSelectedGameApp()));
        root.addView(gameDriver);

        StorageHealth storageHealth = readStorageHealth();
        addSectionTitle("Storage Health");
        LinearLayout storage = card();
        storage.addView(settingStorageStatusRow(
                "Storage Type",
                storageHealth.type,
                0,
                true));
        storage.addView(settingStorageStatusRow(
                "Life A",
                storageHealthDisplay(storageHealth.lifeA),
                1,
                true));
        storage.addView(settingStorageStatusRow(
                "Life B",
                storageHealthDisplay(storageHealth.lifeB),
                2,
                true));
        storage.addView(settingActionRow(
                "Refresh Health",
                "Re-read UFS/eMMC lifetime value",
                v -> refreshStorageHealth()));
        root.addView(storage);

        addSectionTitle("Kernel Manager");
        LinearLayout kernel = card();
        kernel.addView(settingPickerRow(
                "CPU Governor",
                currentCpuGovernor(),
                getCpuGovernors(),
                value -> applyCpuGovernor(value),
                true));
        kernel.addView(settingPickerRow(
                "CPU Little Frequency",
                currentCpuLittleFrequency(),
                getCpuLittleFrequencies(),
                value -> applyCpuLittleFrequency(value),
                true));
        kernel.addView(settingPickerRow(
                "CPU Big Frequency",
                currentCpuBigFrequency(),
                getCpuBigFrequencies(),
                value -> applyCpuBigFrequency(value),
                true));
        kernel.addView(settingPickerRow(
                "GPU Governor",
                currentGpuGovernor(),
                getGpuGovernors(),
                value -> applyGpuGovernor(value),
                true));
        kernel.addView(settingPickerRow(
                "GPU Max Frequency",
                currentGpuFrequency(),
                getGpuFrequencies(),
                value -> applyGpuFrequency(value),
                true));
        kernel.addView(settingSwitchRow(
                "Force 4x MSAA",
                forceMsaaSummary(),
                true,
                true,
                isForceMsaaEnabled(),
                checked -> setForceMsaa(checked)));
        kernel.addView(settingSwitchRow(
                "Disable Thermal Throttling",
                thermalDisableSummary(),
                true,
                false,
                isThermalDisabled(),
                isChecked -> setThermalDisabled(isChecked)));
        root.addView(kernel);

        addSectionTitle("Kernel Settings");
        LinearLayout kernelSettings = card();
        kernelSettings.addView(settingPickerRow(
                "I/O Scheduler",
                currentIoScheduler(),
                getIoSchedulers(),
                value -> applyIoScheduler(value),
                true));
        kernelSettings.addView(settingCategoryRow("Memory"));
        kernelSettings.addView(settingSwitchRow(
                "Zram State",
                zramSummary(),
                true,
                true,
                isZramEnabled(),
                checked -> setZramEnabled(checked)));
        kernelSettings.addView(settingPickerRow(
                "Zram Size",
                currentZramSize(),
                getZramSizes(),
                value -> applyZramSize(value),
                true));
        kernelSettings.addView(settingPickerRow(
                "Compression Algorithm",
                currentZramAlgorithm(),
                getZramAlgorithms(),
                value -> applyZramAlgorithm(value),
                true));
        kernelSettings.addView(settingNumberRow(
                "Swappiness",
                kernelNodeValue(VM_SWAPPINESS_NODE),
                "150",
                value -> applyKernelNodeValue(VM_SWAPPINESS_NODE, value, "Swappiness"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Dirty Ratio",
                kernelNodeValue(VM_DIRTY_RATIO_NODE),
                "20",
                value -> applyKernelNodeValue(VM_DIRTY_RATIO_NODE, value, "Dirty Ratio"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Dirty Background Ratio",
                kernelNodeValue(VM_DIRTY_BACKGROUND_RATIO_NODE),
                "10",
                value -> applyKernelNodeValue(VM_DIRTY_BACKGROUND_RATIO_NODE, value, "Dirty Background Ratio"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Dirty Writeback Centisecs",
                kernelNodeValue(VM_DIRTY_WRITEBACK_NODE),
                "500",
                value -> applyKernelNodeValue(VM_DIRTY_WRITEBACK_NODE, value, "Dirty Writeback Centisecs"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Minimum Amount Of Free Memory",
                kernelNodeValue(VM_MIN_FREE_KBYTES_NODE),
                "11110",
                value -> applyKernelNodeValue(VM_MIN_FREE_KBYTES_NODE, value, "Minimum Free Memory"),
                true));
        kernelSettings.addView(settingSwitchRow(
                "OOM Kill Allocating Task",
                kernelSwitchSummary(VM_OOM_KILL_ALLOCATING_TASK_NODE),
                true,
                true,
                kernelSwitchEnabled(VM_OOM_KILL_ALLOCATING_TASK_NODE),
                checked -> applyKernelSwitchNode(VM_OOM_KILL_ALLOCATING_TASK_NODE, checked, "OOM Kill Allocating Task")));
        kernelSettings.addView(settingNumberRow(
                "Overcommit Ratio",
                kernelNodeValue(VM_OVERCOMMIT_RATIO_NODE),
                "50",
                value -> applyKernelNodeValue(VM_OVERCOMMIT_RATIO_NODE, value, "Overcommit Ratio"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Laptop Mode",
                kernelNodeValue(VM_LAPTOP_MODE_NODE),
                "0",
                value -> applyKernelNodeValue(VM_LAPTOP_MODE_NODE, value, "Laptop Mode"),
                true));
        kernelSettings.addView(settingNumberRow(
                "VFS Cache Pressure",
                kernelNodeValue(VM_VFS_CACHE_PRESSURE_NODE),
                "100",
                value -> applyKernelNodeValue(VM_VFS_CACHE_PRESSURE_NODE, value, "VFS Cache Pressure"),
                true));
        kernelSettings.addView(settingCategoryRow("Scheduler"));
        kernelSettings.addView(settingSwitchRow(
                "Sched Child Runs First",
                kernelSwitchSummary(SCHED_CHILD_RUNS_FIRST_NODE),
                true,
                true,
                kernelSwitchEnabled(SCHED_CHILD_RUNS_FIRST_NODE),
                checked -> applyKernelSwitchNode(SCHED_CHILD_RUNS_FIRST_NODE, checked, "Sched Child Runs First")));
        kernelSettings.addView(settingNumberRow(
                "Sched Deadline Period Max Us",
                kernelNodeValue(SCHED_DEADLINE_PERIOD_MAX_NODE),
                "4194304",
                value -> applyKernelNodeValue(SCHED_DEADLINE_PERIOD_MAX_NODE, value, "Sched Deadline Period Max"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Sched Deadline Period Min Us",
                kernelNodeValue(SCHED_DEADLINE_PERIOD_MIN_NODE),
                "100",
                value -> applyKernelNodeValue(SCHED_DEADLINE_PERIOD_MIN_NODE, value, "Sched Deadline Period Min"),
                true));
        kernelSettings.addView(settingSwitchRow(
                "Sched Energy Aware",
                kernelSwitchSummary(SCHED_ENERGY_AWARE_NODE),
                true,
                true,
                kernelSwitchEnabled(SCHED_ENERGY_AWARE_NODE),
                checked -> applyKernelSwitchNode(SCHED_ENERGY_AWARE_NODE, checked, "Sched Energy Aware")));
        kernelSettings.addView(settingPickerRow(
                "Sched Pelt Multiplier",
                kernelNodeValue(SCHED_PELT_MULTIPLIER_NODE),
                getSchedPeltMultiplierValues(),
                value -> applyKernelNodeValue(SCHED_PELT_MULTIPLIER_NODE, value, "Sched Pelt Multiplier"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Sched Rr Timeslice Ms",
                kernelNodeValue(SCHED_RR_TIMESLICE_NODE),
                "100",
                value -> applyKernelNodeValue(SCHED_RR_TIMESLICE_NODE, value, "Sched Rr Timeslice"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Sched Rt Period Us",
                kernelNodeValue(SCHED_RT_PERIOD_NODE),
                "1000000",
                value -> applyKernelNodeValue(SCHED_RT_PERIOD_NODE, value, "Sched Rt Period"),
                true));
        kernelSettings.addView(settingSwitchRow(
                "Sched Schedstats",
                kernelSwitchSummary(SCHED_SCHEDSTATS_NODE),
                true,
                true,
                kernelSwitchEnabled(SCHED_SCHEDSTATS_NODE),
                checked -> applyKernelSwitchNode(SCHED_SCHEDSTATS_NODE, checked, "Sched Schedstats")));
        kernelSettings.addView(settingNumberRow(
                "Sched Util Clamp Max",
                kernelNodeValue(SCHED_UTIL_CLAMP_MAX_NODE),
                "1024",
                value -> applyKernelNodeValue(SCHED_UTIL_CLAMP_MAX_NODE, value, "Sched Util Clamp Max"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Sched Util Clamp Min",
                kernelNodeValue(SCHED_UTIL_CLAMP_MIN_NODE),
                "1024",
                value -> applyKernelNodeValue(SCHED_UTIL_CLAMP_MIN_NODE, value, "Sched Util Clamp Min"),
                true));
        kernelSettings.addView(settingSwitchRow(
                "Sched Util Clamp Min Rt Default",
                kernelSwitchSummary(SCHED_UTIL_CLAMP_MIN_RT_DEFAULT_NODE),
                true,
                true,
                kernelSwitchEnabled(SCHED_UTIL_CLAMP_MIN_RT_DEFAULT_NODE),
                checked -> applyKernelSwitchNode(SCHED_UTIL_CLAMP_MIN_RT_DEFAULT_NODE, checked, "Sched Util Clamp Min Rt Default")));
        kernelSettings.addView(settingCategoryRow("Miscellaneous"));
        kernelSettings.addView(settingPickerRow(
                "TCP Congestion Algorithm",
                kernelNodeValue(TCP_CONGESTION_CONTROL_NODE),
                getTcpCongestionAlgorithms(),
                value -> applyKernelNodeValue(TCP_CONGESTION_CONTROL_NODE, value, "TCP Congestion Algorithm"),
                true));
        kernelSettings.addView(settingPickerRow(
                "Pstore Compress",
                kernelNodeValue(PSTORE_COMPRESS_NODE),
                getPstoreCompressValues(),
                value -> applyKernelNodeValue(PSTORE_COMPRESS_NODE, value, "Pstore Compress"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Entropy Write Wakeup Threshold",
                kernelNodeValue(ENTROPY_WRITE_WAKEUP_NODE),
                "256",
                value -> applyKernelNodeValue(ENTROPY_WRITE_WAKEUP_NODE, value, "Entropy Write Wakeup Threshold"),
                true));
        kernelSettings.addView(settingNumberRow(
                "Keyboard Polling Interval",
                currentKeyboardPollingInterval(),
                "8",
                value -> applyKeyboardPollingInterval(value),
                false));
        root.addView(kernelSettings);

        addSectionTitle("LED Backlight");
        LinearLayout led = card();
        led.addView(settingPickerRow(
                "LED Channel",
                currentLedChannel(),
                getLedChannels(),
                value -> {
                    setLedChannel(value);
                    showLedBacklightPage();
                },
                true));
        led.addView(settingSwitchRow(
                "LED State",
                ledStateSummary(),
                true,
                true,
                isLedEnabled(),
                checked -> setLedEnabled(checked)));
        led.addView(settingNumberRow(
                "LED Brightness",
                currentLedBrightness(),
                currentLedMaxBrightness(),
                value -> setLedBrightness(value),
                true));
        led.addView(settingPickerRow(
                "LED Trigger",
                currentLedTrigger(),
                getLedTriggers(),
                value -> applyLedTrigger(value),
                true));
        led.addView(settingSwitchRow(
                "Blink Timer",
                "Use kernel timer trigger",
                true,
                true,
                isLedTimerEnabled(),
                checked -> setLedTimer(checked)));
        led.addView(settingNumberRow(
                "Blink Delay On",
                currentLedDelay("delay_on"),
                "500",
                value -> applyLedDelay("delay_on", value),
                true));
        led.addView(settingNumberRow(
                "Blink Delay Off",
                currentLedDelay("delay_off"),
                "500",
                value -> applyLedDelay("delay_off", value),
                false));
        root.addView(led);

        addSectionTitle("Overlay Monitor");
        LinearLayout overlay = card();
        overlay.addView(settingSwitchRow(
                "Overlay Monitor",
                overlayMonitorSummary(),
                true,
                true,
                overlayMonitorEnabled(),
                checked -> setOverlayMonitorEnabled(checked)));
        overlay.addView(settingPickerRow(
                "Position",
                overlayPositionValue(),
                new ArrayList<>(Arrays.asList(OverlayPrefs.POSITION_VALUES)),
                value -> setOverlayPrefString(OverlayPrefs.KEY_POSITION, value),
                true));
        overlay.addView(settingSwitchRow(
                "Drag Overlay",
                "Temporarily allow moving overlay",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_DRAG_MODE, false),
                checked -> setOverlayDragMode(checked)));
        overlay.addView(settingActionRow(
                "Reset Overlay Position",
                "Return overlay to selected preset",
                v -> resetOverlayPosition()));
        overlay.addView(dividerView());
        overlay.addView(settingNumberRow(
                "Text Size",
                overlayPrefIntText(OverlayPrefs.KEY_TEXT_SIZE, OverlayPrefs.DEFAULT_TEXT_SIZE),
                String.valueOf(OverlayPrefs.DEFAULT_TEXT_SIZE),
                value -> setOverlayPrefInt(OverlayPrefs.KEY_TEXT_SIZE, value, 8, 40, "Text Size"),
                true));
        overlay.addView(settingNumberRow(
                "Opacity",
                overlayPrefIntText(OverlayPrefs.KEY_OPACITY, OverlayPrefs.DEFAULT_OPACITY),
                String.valueOf(OverlayPrefs.DEFAULT_OPACITY),
                value -> setOverlayPrefInt(OverlayPrefs.KEY_OPACITY, value, 10, 100, "Opacity"),
                true));
        overlay.addView(settingSwitchRow(
                "Shadow",
                "Text and panel shadow",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHADOW, OverlayPrefs.DEFAULT_SHADOW),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHADOW, checked)));
        overlay.addView(settingCategoryRow("Visible Items"));
        overlay.addView(settingSwitchRow(
                "CPU Frequency",
                "Show current CPU frequency",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_FREQ, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_FREQ, checked)));
        overlay.addView(settingSwitchRow(
                "GPU Frequency",
                "Show current GPU frequency",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_FREQ, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_FREQ, checked)));
        overlay.addView(settingSwitchRow(
                "RAM Usage",
                "Show used and total memory",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_RAM, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_RAM, checked)));
        overlay.addView(settingSwitchRow(
                "Frame Per Second",
                "Show overlay frame pacing",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_FPS, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_FPS, checked)));
        overlay.addView(settingSwitchRow(
                "Battery Temperature",
                "Show battery temperature",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_TEMP, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_TEMP, checked)));
        overlay.addView(settingSwitchRow(
                "CPU Temperature",
                "Show CPU or SoC temperature",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_TEMP, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_TEMP, checked)));
        overlay.addView(settingSwitchRow(
                "GPU Temperature",
                "Show GPU temperature when available",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_TEMP, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_TEMP, checked)));
        overlay.addView(settingSwitchRow(
                "Battery Percent",
                "Show battery percentage",
                true,
                false,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_PERCENT, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_PERCENT, checked)));
        root.addView(overlay);

        addSectionTitle("Battery");
        LinearLayout battery = card();
        battery.addView(settingSwitchRow(
                "Fast Charge",
                "Enable/Disable Fast Charging",
                true,
                true,
                isFastChargeEnabled(),
                isChecked -> setFastCharge(isChecked)));
        battery.addView(settingSwitchRow(
                "Bypass Charge",
                "Enabling/disabling bypass charging",
                true,
                false,
                isBypassChargeEnabled(),
                isChecked -> setBypassCharge(isChecked)));
        root.addView(battery);

        addSectionTitle("AI Charge");
        LinearLayout aiCharge = card();
        aiCharge.addView(settingAiChargeLimitRow());
        aiCharge.addView(dividerView());
        aiCharge.addView(settingAiChargeStatusRow());
        aiCharge.addView(dividerView());
        aiCharge.addView(settingActionRow(
                "Disable AI Charge",
                "Remove auto cut and allow charging",
                v -> disableAiCharge()));
        root.addView(aiCharge);

        addSectionTitle("Display");
        LinearLayout display = card();
        display.addView(settingDisplayResolutionRow());
        display.addView(dividerView());
        display.addView(settingDisplayRefreshRow());
        display.addView(dividerView());
        display.addView(settingSwitchRow(
                "Show Refresh Rate",
                refreshRateOverlaySummary(),
                true,
                true,
                isRefreshRateOverlayEnabled(),
                checked -> setRefreshRateOverlay(checked)));
        display.addView(settingSwitchRow(
                "Force Peak Refresh Rate",
                forcePeakRefreshSummary(),
                true,
                true,
                isForcePeakRefreshEnabled(),
                checked -> setForcePeakRefreshRate(checked)));
        display.addView(settingGameFpsRow());
        display.addView(dividerView());
        display.addView(settingSwitchRow(
                "Disable HW Overlay",
                "Force GPU composition through SurfaceFlinger",
                true,
                true,
                isHwOverlayDisabled(),
                checked -> setHwOverlayDisabled(checked)));
        display.addView(settingActionRow(
                "Reset Display Tweaks",
                "Restore size, refresh and overlay defaults",
                v -> resetDisplayTweaks()));
        root.addView(display);
    }

    private boolean useMenuHome() {
        return true;
    }

    private void showHomePage() {
        startPage("Transsion Addons", false);
        root.setTag("main");

        addSectionTitle("Graphics & Display");
        LinearLayout graphics = card();
        graphics.addView(settingActionRow(
                "Rendering Change",
                "Renderer, ANGLE option and current rendering status",
                v -> showRenderingPage()));
        graphics.addView(dividerView());
        graphics.addView(settingActionRow(
                "Game Driver Preference",
                "Choose per-app graphics driver and launch the selected app",
                v -> showGameDriverPage()));
        graphics.addView(dividerView());
        graphics.addView(settingActionRow(
                "Display",
                "Resolution, refresh rate, FPS override and HW overlay toggles",
                v -> showDisplayPage()));
        graphics.addView(dividerView());
        graphics.addView(settingActionRow(
                "Display Debug",
                "Read panel FPS, brightness, pixel clock and ESD status nodes",
                v -> showDisplayDebugPage()));
        root.addView(graphics);

        addSectionTitle("Gaming");
        LinearLayout gaming = card();
        gaming.addView(settingActionRow(
                "Game Space Controls",
                "Simple controls for built-in Game Space, frame boost and game mode settings",
                v -> showGameSpacePage()));
        root.addView(gaming);

        addSectionTitle("Performance");
        LinearLayout performance = card();
        performance.addView(settingActionRow(
                "Kernel Manager",
                "CPU, GPU, MSAA and thermal service controls",
                v -> showKernelManagerPage()));
        performance.addView(dividerView());
        performance.addView(settingActionRow(
                "Kernel Settings",
                "Memory, scheduler, I/O, ZRAM and misc kernel tunables",
                v -> showKernelSettingsPage()));
        performance.addView(dividerView());
        performance.addView(settingActionRow(
                "Device Frequency",
                "Auto-detect devfreq nodes for GPU, DPU, memory and media blocks",
                v -> showDevfreqPage()));
        performance.addView(dividerView());
        performance.addView(settingActionRow(
                "Thermal Monitor",
                "Show CPU, GPU, board, charger and cooling-device states",
                v -> showThermalMonitorPage()));
        root.addView(performance);

        addSectionTitle("Device");
        LinearLayout device = card();
        device.addView(settingActionRow(
                "Storage Health",
                "Read UFS or eMMC lifetime health from detected storage nodes",
                v -> showStorageHealthPage()));
        device.addView(dividerView());
        device.addView(settingActionRow(
                "Battery & Charging",
                "Battery status, fast charge, bypass charge and AI charge limit",
                v -> showBatteryPage()));
        device.addView(dividerView());
        device.addView(settingActionRow(
                "LED Backlight",
                "Select LED channel, trigger, brightness and blink timing",
                v -> showLedBacklightPage()));
        device.addView(dividerView());
        device.addView(settingActionRow(
                "Touch Settings",
                "Auto-detect touch nodes for glove, grip, gesture and noise options",
                v -> showTouchSettingsPage()));
        root.addView(device);

        addSectionTitle("Monitoring & Network");
        LinearLayout monitoring = card();
        monitoring.addView(settingActionRow(
                "Overlay Monitor",
                "Floating monitor with custom position, size, opacity and visible items",
                v -> showOverlayMonitorPage()));
        monitoring.addView(dividerView());
        monitoring.addView(settingActionRow(
                "Network Tweaks",
                "TCP congestion and common IPv4 networking tunables",
                v -> showNetworkPage()));
        root.addView(monitoring);
    }

    private void showRenderingPage() {
        startPage("Rendering", true);
        root.setTag("rendering");

        addSectionTitle("Rendering Change");
        LinearLayout rendering = card();
        rendering.addView(settingRendererRow());
        rendering.addView(dividerView());
        rendering.addView(settingSwitchRow(
                "ANGLE Developer Option",
                "Shows Android's ANGLE driver picker in Developer Options.",
                true,
                true,
                angleDeveloperOptionEnabled(),
                checked -> setAngleDeveloperOption(checked)));
        rendering.addView(settingActionRow(
                "Reboot To Apply",
                "Restart Android so renderer properties are loaded cleanly.",
                v -> rebootDevice()));
        rendering.addView(dividerView());
        rendering.addView(settingRendererStatusRow());
        root.addView(rendering);
    }

    private void showGameDriverPage() {
        startPage("Game Driver", true);
        root.setTag("game_driver");

        addSectionTitle("Game Driver Preference");
        LinearLayout gameDriver = card();
        gameDriver.addView(settingGameAppRow());
        gameDriver.addView(dividerView());
        gameDriver.addView(settingGameDriverRow());
        gameDriver.addView(dividerView());
        gameDriver.addView(settingActionRow(
                "Apply Driver",
                "Write the selected driver mode to Android global settings.",
                v -> applyGameDriverPreference()));
        gameDriver.addView(dividerView());
        gameDriver.addView(settingActionRow(
                "Run App",
                "Open the selected app after applying its driver preference.",
                v -> runSelectedGameApp()));
        root.addView(gameDriver);
    }

    private void showGameSpacePage() {
        startPage("Game Space Controls", true);
        root.setTag("game_space");

        addSectionTitle("Built-in Game Space");
        LinearLayout launcher = card();
        launcher.addView(settingInfoRow(
                "Game Space Package",
                gameSpaceVersion(),
                "Detected Transsion Game Space package and installed version.",
                true));
        launcher.addView(settingActionRow(
                "Open Game Space",
                "Launch the stock Game Space activity without changing it.",
                v -> openGameSpace()));
        root.addView(launcher);

        addSectionTitle("Compatibility Whitelist");
        LinearLayout compatibility = card();
        compatibility.addView(settingGameAppRow());
        compatibility.addView(dividerView());
        compatibility.addView(settingInfoRow(
                "SmartPanel Local List",
                smartPanelWhitelistStatus(),
                "Forces SmartPanel's fallback supported-app list when the ROM returns an empty vendor list.",
                true));
        compatibility.addView(settingInfoRow(
                "GameSpace Provider",
                gameSpaceProviderStatus(selectedGamePackage()),
                "Checks whether the selected app is present in the stock GameSpace database.",
                true));
        compatibility.addView(settingActionRow(
                "Force Selected App Support",
                "Add selected app to SmartPanel feature lists, GameSpace provider and game driver whitelist.",
                v -> forceSelectedGameSupport()));
        compatibility.addView(dividerView());
        compatibility.addView(settingActionRow(
                "Clear Forced SmartPanel List",
                "Remove only the local SmartPanel fallback whitelist written by this app.",
                v -> clearSmartPanelWhitelist()));
        root.addView(compatibility);

        addSectionTitle("Frame & Image");
        LinearLayout frame = card();
        frame.addView(settingSwitchRow(
                "Graphic Enhancement",
                gameSettingSwitchSummary("transsion_game_picture_optimization"),
                true,
                true,
                gameSettingSwitchEnabled("transsion_game_picture_optimization"),
                checked -> applyGameSettingSwitch("transsion_game_picture_optimization", checked, "Graphic Enhancement")));
        frame.addView(settingPickerRow(
                "Enhancement Mode",
                pqeModeLabel(gameSettingValue("pqe_mode_values")),
                pqeModeOptions(),
                value -> applyPqeMode(value),
                true));
        frame.addView(dividerView());
        frame.addView(settingActionRow(
                "Force Runtime Effects",
                "Push current image, audio and countdown flags to the runtime keys used by SmartPanel.",
                v -> forceGameSpaceRuntimeEffects()));
        root.addView(frame);
        addSectionTitle("Performance");
        LinearLayout performance = card();
        performance.addView(settingPickerRow(
                "Game Performance Mode",
                gamePerformanceModeLabel(gameSettingValue("settings_game_performance_mode")),
                gamePerformanceModeOptions(),
                value -> applyGamePerformanceMode(value),
                true));
        performance.addView(settingSwitchRow(
                "Game Acceleration",
                gameSettingSwitchSummary("transsion_game_acceleration"),
                true,
                true,
                gameSettingSwitchEnabled("transsion_game_acceleration"),
                checked -> applyGameSettingSwitch("transsion_game_acceleration", checked, "Game Acceleration")));
        performance.addView(settingSwitchRow(
                "Game Bypass Charging",
                gameSettingSwitchSummary("game_bypass_charging"),
                true,
                true,
                gameSettingSwitchEnabled("game_bypass_charging"),
                checked -> applyGameSettingSwitch("game_bypass_charging", checked, "Game Bypass Charging")));
        performance.addView(settingSwitchRow(
                "Charge Cooling",
                gameSettingSwitchSummary("transsion_game_charge_cooling"),
                true,
                false,
                gameSettingSwitchEnabled("transsion_game_charge_cooling"),
                checked -> applyGameSettingSwitch("transsion_game_charge_cooling", checked, "Charge Cooling")));
        root.addView(performance);

        addSectionTitle("Gameplay Extras");
        LinearLayout extras = card();
        extras.addView(settingSwitchRow(
                "Immersive Audio",
                gameSettingSwitchSummary("game_space_pace"),
                true,
                true,
                gameSettingSwitchEnabled("game_space_pace"),
                checked -> applyGameSettingSwitch("game_space_pace", checked, "Immersive Audio")));
        extras.addView(dividerView());
        extras.addView(settingSwitchRow(
                "Game Sound Effects",
                gameSettingSwitchSummary("game_sound_effects"),
                true,
                true,
                gameSettingSwitchEnabled("game_sound_effects"),
                checked -> applyGameSettingSwitch("game_sound_effects", checked, "Game Sound Effects")));
        extras.addView(dividerView());
        extras.addView(settingSwitchRow(
                "Respawn Countdown",
                gameSettingSwitchSummary("game_space_resurrection_status"),
                true,
                true,
                gameSettingSwitchEnabled("game_space_resurrection_status"),
                checked -> applyGameSettingSwitch("game_space_resurrection_status", checked, "Respawn Countdown")));
        root.addView(extras);

        addSectionTitle("Anti Disturb");
        LinearLayout anti = card();
        anti.addView(settingSwitchRow(
                "Reject Calls",
                gameSettingSwitchSummary("transsion_game_mode_refuse_call"),
                true,
                true,
                gameSettingSwitchEnabled("transsion_game_mode_refuse_call"),
                checked -> applyGameSettingSwitch("transsion_game_mode_refuse_call", checked, "Reject Calls")));
        anti.addView(settingSwitchRow(
                "Do Not Interrupt",
                gameSettingSwitchSummary("transsion_game_mode_not_interrupt"),
                true,
                true,
                gameSettingSwitchEnabled("transsion_game_mode_not_interrupt"),
                checked -> applyGameSettingSwitch("transsion_game_mode_not_interrupt", checked, "Do Not Interrupt")));
        anti.addView(settingSwitchRow(
                "Block Gestures",
                gameSettingSwitchSummary("transsion_os_game_block_gestures"),
                true,
                true,
                gameSettingSwitchEnabled("transsion_os_game_block_gestures"),
                checked -> applyGameSettingSwitch("transsion_os_game_block_gestures", checked, "Block Gestures")));
        anti.addView(settingSwitchRow(
                "Block Control Center",
                gameSettingSwitchSummary("transsion_os_game_block_control_center"),
                true,
                true,
                gameSettingSwitchEnabled("transsion_os_game_block_control_center"),
                checked -> applyGameSettingSwitch("transsion_os_game_block_control_center", checked, "Block Control Center")));
        anti.addView(settingSwitchRow(
                "Screen Lock",
                gameSettingSwitchSummary("game_screen_lock_status"),
                true,
                false,
                gameSettingSwitchEnabled("game_screen_lock_status"),
                checked -> applyGameSettingSwitch("game_screen_lock_status", checked, "Screen Lock")));
        root.addView(anti);
    }

    private void showStorageHealthPage() {
        startPage("Storage Health", true);
        root.setTag("storage");

        StorageHealth storageHealth = readStorageHealth();
        addSectionTitle("Storage Health");
        LinearLayout storage = card();
        storage.addView(settingStorageStatusRow(
                "Storage Type",
                storageHealth.type,
                0,
                true));
        storage.addView(settingStorageStatusRow(
                "Life A",
                storageHealthDisplay(storageHealth.lifeA),
                1,
                true));
        storage.addView(settingStorageStatusRow(
                "Life B",
                storageHealthDisplay(storageHealth.lifeB),
                2,
                true));
        storage.addView(settingActionRow(
                "Refresh Health",
                "Re-read UFS/eMMC lifetime values from detected nodes.",
                v -> refreshStorageHealth()));
        root.addView(storage);
    }

    private void showKernelManagerPage() {
        startPage("Kernel Manager", true);
        root.setTag("kernel_manager");

        addSectionTitle("CPU & GPU");
        LinearLayout kernel = card();
        kernel.addView(settingPickerRow(
                "CPU Governor",
                currentCpuGovernor(),
                getCpuGovernors(),
                value -> applyCpuGovernor(value),
                true));
        kernel.addView(settingPickerRow(
                "CPU Little Frequency",
                currentCpuLittleFrequency(),
                getCpuLittleFrequencies(),
                value -> applyCpuLittleFrequency(value),
                true));
        kernel.addView(settingPickerRow(
                "CPU Big Frequency",
                currentCpuBigFrequency(),
                getCpuBigFrequencies(),
                value -> applyCpuBigFrequency(value),
                true));
        kernel.addView(settingPickerRow(
                "GPU Governor",
                currentGpuGovernor(),
                getGpuGovernors(),
                value -> applyGpuGovernor(value),
                true));
        kernel.addView(settingPickerRow(
                "GPU Max Frequency",
                currentGpuFrequency(),
                getGpuFrequencies(),
                value -> applyGpuFrequency(value),
                true));
        kernel.addView(settingSwitchRow(
                "Force 4x MSAA",
                "Force OpenGL ES MSAA flag for apps that respect Android's debug property.",
                true,
                true,
                isForceMsaaEnabled(),
                checked -> setForceMsaa(checked)));
        kernel.addView(settingSwitchRow(
                "Disable Thermal Throttling",
                thermalDisableSummary(),
                true,
                false,
                isThermalDisabled(),
                isChecked -> setThermalDisabled(isChecked)));
        root.addView(kernel);

        addSectionTitle("Advanced");
        LinearLayout advanced = card();
        advanced.addView(settingActionRow(
                "Device Frequency",
                "Open detected devfreq devices and GPU driver tunables.",
                v -> showDevfreqPage()));
        advanced.addView(dividerView());
        advanced.addView(settingActionRow(
                "Thermal Monitor",
                "Check current thermal zones before changing throttling behavior.",
                v -> showThermalMonitorPage()));
        root.addView(advanced);
    }

    private void showKernelSettingsPage() {
        startPage("Kernel Settings", true);
        root.setTag("kernel_settings");

        addSectionTitle("I/O");
        LinearLayout io = card();
        io.addView(settingPickerRow(
                "I/O Scheduler",
                currentIoScheduler(),
                getIoSchedulers(),
                value -> applyIoScheduler(value),
                true));
        io.addView(settingNumberRow(
                "Read Ahead KB",
                currentBlockQueueValue("read_ahead_kb"),
                "128",
                value -> applyBlockQueueValue("read_ahead_kb", value, "Read Ahead KB"),
                true));
        io.addView(settingNumberRow(
                "NR Requests",
                currentBlockQueueValue("nr_requests"),
                "64",
                value -> applyBlockQueueValue("nr_requests", value, "NR Requests"),
                true));
        io.addView(settingNumberRow(
                "RQ Affinity",
                currentBlockQueueValue("rq_affinity"),
                "2",
                value -> applyBlockQueueValue("rq_affinity", value, "RQ Affinity"),
                true));
        io.addView(settingSwitchRow(
                "I/O Stats",
                blockQueueSwitchSummary("iostats"),
                true,
                true,
                blockQueueSwitchEnabled("iostats"),
                checked -> applyBlockQueueSwitch("iostats", checked, "I/O Stats")));
        io.addView(settingSwitchRow(
                "Add Random",
                blockQueueSwitchSummary("add_random"),
                true,
                true,
                blockQueueSwitchEnabled("add_random"),
                checked -> applyBlockQueueSwitch("add_random", checked, "Add Random")));
        io.addView(settingNumberRow(
                "No Merges",
                currentBlockQueueValue("nomerges"),
                "0",
                value -> applyBlockQueueValue("nomerges", value, "No Merges"),
                true));
        io.addView(settingSwitchRow(
                "I/O Poll",
                blockQueueSwitchSummary("io_poll"),
                true,
                false,
                blockQueueSwitchEnabled("io_poll"),
                checked -> applyBlockQueueSwitch("io_poll", checked, "I/O Poll")));
        root.addView(io);

        addSectionTitle("Memory");
        LinearLayout memory = card();
        memory.addView(settingSwitchRow(
                "Zram State",
                zramSummary(),
                true,
                true,
                isZramEnabled(),
                checked -> setZramEnabled(checked)));
        memory.addView(settingPickerRow(
                "Zram Size",
                currentZramSize(),
                getZramSizes(),
                value -> applyZramSize(value),
                true));
        memory.addView(settingPickerRow(
                "Compression Algorithm",
                currentZramAlgorithm(),
                getZramAlgorithms(),
                value -> applyZramAlgorithm(value),
                true));
        memory.addView(settingNumberRow(
                "Zram Max Comp Streams",
                kernelNodeValue(ZRAM_MAX_COMP_STREAMS_NODE),
                "8",
                value -> applyKernelNodeValue(ZRAM_MAX_COMP_STREAMS_NODE, value, "Zram Max Comp Streams"),
                true));
        memory.addView(settingSwitchRow(
                "Zram Dedup",
                kernelSwitchSummary(ZRAM_USE_DEDUP_NODE),
                true,
                true,
                kernelSwitchEnabled(ZRAM_USE_DEDUP_NODE),
                checked -> applyKernelSwitchNode(ZRAM_USE_DEDUP_NODE, checked, "Zram Dedup")));
        memory.addView(settingActionRow(
                "Compact Zram",
                "Ask zram to compact memory pages when the kernel supports it.",
                v -> compactZram()));
        memory.addView(dividerView());
        memory.addView(settingNumberRow(
                "Swappiness",
                kernelNodeValue(VM_SWAPPINESS_NODE),
                "150",
                value -> applyKernelNodeValue(VM_SWAPPINESS_NODE, value, "Swappiness"),
                true));
        memory.addView(settingNumberRow(
                "Dirty Ratio",
                kernelNodeValue(VM_DIRTY_RATIO_NODE),
                "20",
                value -> applyKernelNodeValue(VM_DIRTY_RATIO_NODE, value, "Dirty Ratio"),
                true));
        memory.addView(settingNumberRow(
                "Dirty Background Ratio",
                kernelNodeValue(VM_DIRTY_BACKGROUND_RATIO_NODE),
                "10",
                value -> applyKernelNodeValue(VM_DIRTY_BACKGROUND_RATIO_NODE, value, "Dirty Background Ratio"),
                true));
        memory.addView(settingNumberRow(
                "Dirty Writeback Centisecs",
                kernelNodeValue(VM_DIRTY_WRITEBACK_NODE),
                "500",
                value -> applyKernelNodeValue(VM_DIRTY_WRITEBACK_NODE, value, "Dirty Writeback Centisecs"),
                true));
        memory.addView(settingNumberRow(
                "Minimum Amount Of Free Memory",
                kernelNodeValue(VM_MIN_FREE_KBYTES_NODE),
                "11110",
                value -> applyKernelNodeValue(VM_MIN_FREE_KBYTES_NODE, value, "Minimum Free Memory"),
                true));
        memory.addView(settingSwitchRow(
                "OOM Kill Allocating Task",
                kernelSwitchSummary(VM_OOM_KILL_ALLOCATING_TASK_NODE),
                true,
                true,
                kernelSwitchEnabled(VM_OOM_KILL_ALLOCATING_TASK_NODE),
                checked -> applyKernelSwitchNode(VM_OOM_KILL_ALLOCATING_TASK_NODE, checked, "OOM Kill Allocating Task")));
        memory.addView(settingNumberRow(
                "Overcommit Ratio",
                kernelNodeValue(VM_OVERCOMMIT_RATIO_NODE),
                "50",
                value -> applyKernelNodeValue(VM_OVERCOMMIT_RATIO_NODE, value, "Overcommit Ratio"),
                true));
        memory.addView(settingNumberRow(
                "Laptop Mode",
                kernelNodeValue(VM_LAPTOP_MODE_NODE),
                "0",
                value -> applyKernelNodeValue(VM_LAPTOP_MODE_NODE, value, "Laptop Mode"),
                true));
        memory.addView(settingNumberRow(
                "VFS Cache Pressure",
                kernelNodeValue(VM_VFS_CACHE_PRESSURE_NODE),
                "100",
                value -> applyKernelNodeValue(VM_VFS_CACHE_PRESSURE_NODE, value, "VFS Cache Pressure"),
                false));
        root.addView(memory);

        addSectionTitle("Scheduler");
        LinearLayout scheduler = card();
        scheduler.addView(settingSwitchRow(
                "Sched Child Runs First",
                kernelSwitchSummary(SCHED_CHILD_RUNS_FIRST_NODE),
                true,
                true,
                kernelSwitchEnabled(SCHED_CHILD_RUNS_FIRST_NODE),
                checked -> applyKernelSwitchNode(SCHED_CHILD_RUNS_FIRST_NODE, checked, "Sched Child Runs First")));
        scheduler.addView(settingNumberRow(
                "Sched Deadline Period Max Us",
                kernelNodeValue(SCHED_DEADLINE_PERIOD_MAX_NODE),
                "4194304",
                value -> applyKernelNodeValue(SCHED_DEADLINE_PERIOD_MAX_NODE, value, "Sched Deadline Period Max"),
                true));
        scheduler.addView(settingNumberRow(
                "Sched Deadline Period Min Us",
                kernelNodeValue(SCHED_DEADLINE_PERIOD_MIN_NODE),
                "100",
                value -> applyKernelNodeValue(SCHED_DEADLINE_PERIOD_MIN_NODE, value, "Sched Deadline Period Min"),
                true));
        scheduler.addView(settingSwitchRow(
                "Sched Energy Aware",
                kernelSwitchSummary(SCHED_ENERGY_AWARE_NODE),
                true,
                true,
                kernelSwitchEnabled(SCHED_ENERGY_AWARE_NODE),
                checked -> applyKernelSwitchNode(SCHED_ENERGY_AWARE_NODE, checked, "Sched Energy Aware")));
        scheduler.addView(settingPickerRow(
                "Sched Pelt Multiplier",
                kernelNodeValue(SCHED_PELT_MULTIPLIER_NODE),
                getSchedPeltMultiplierValues(),
                value -> applyKernelNodeValue(SCHED_PELT_MULTIPLIER_NODE, value, "Sched Pelt Multiplier"),
                true));
        scheduler.addView(settingNumberRow(
                "Sched Rr Timeslice Ms",
                kernelNodeValue(SCHED_RR_TIMESLICE_NODE),
                "100",
                value -> applyKernelNodeValue(SCHED_RR_TIMESLICE_NODE, value, "Sched Rr Timeslice"),
                true));
        scheduler.addView(settingNumberRow(
                "Sched Rt Period Us",
                kernelNodeValue(SCHED_RT_PERIOD_NODE),
                "1000000",
                value -> applyKernelNodeValue(SCHED_RT_PERIOD_NODE, value, "Sched Rt Period"),
                true));
        scheduler.addView(settingSwitchRow(
                "Sched Schedstats",
                kernelSwitchSummary(SCHED_SCHEDSTATS_NODE),
                true,
                true,
                kernelSwitchEnabled(SCHED_SCHEDSTATS_NODE),
                checked -> applyKernelSwitchNode(SCHED_SCHEDSTATS_NODE, checked, "Sched Schedstats")));
        scheduler.addView(settingNumberRow(
                "Sched Util Clamp Max",
                kernelNodeValue(SCHED_UTIL_CLAMP_MAX_NODE),
                "1024",
                value -> applyKernelNodeValue(SCHED_UTIL_CLAMP_MAX_NODE, value, "Sched Util Clamp Max"),
                true));
        scheduler.addView(settingNumberRow(
                "Sched Util Clamp Min",
                kernelNodeValue(SCHED_UTIL_CLAMP_MIN_NODE),
                "1024",
                value -> applyKernelNodeValue(SCHED_UTIL_CLAMP_MIN_NODE, value, "Sched Util Clamp Min"),
                true));
        scheduler.addView(settingSwitchRow(
                "Sched Util Clamp Min Rt Default",
                kernelSwitchSummary(SCHED_UTIL_CLAMP_MIN_RT_DEFAULT_NODE),
                true,
                false,
                kernelSwitchEnabled(SCHED_UTIL_CLAMP_MIN_RT_DEFAULT_NODE),
                checked -> applyKernelSwitchNode(SCHED_UTIL_CLAMP_MIN_RT_DEFAULT_NODE, checked, "Sched Util Clamp Min Rt Default")));
        root.addView(scheduler);

        addSectionTitle("Miscellaneous");
        LinearLayout misc = card();
        misc.addView(settingPickerRow(
                "TCP Congestion Algorithm",
                kernelNodeValue(TCP_CONGESTION_CONTROL_NODE),
                getTcpCongestionAlgorithms(),
                value -> applyKernelNodeValue(TCP_CONGESTION_CONTROL_NODE, value, "TCP Congestion Algorithm"),
                true));
        misc.addView(settingPickerRow(
                "Pstore Compress",
                kernelNodeValue(PSTORE_COMPRESS_NODE),
                getPstoreCompressValues(),
                value -> applyKernelNodeValue(PSTORE_COMPRESS_NODE, value, "Pstore Compress"),
                true));
        misc.addView(settingNumberRow(
                "Entropy Write Wakeup Threshold",
                kernelNodeValue(ENTROPY_WRITE_WAKEUP_NODE),
                "256",
                value -> applyKernelNodeValue(ENTROPY_WRITE_WAKEUP_NODE, value, "Entropy Write Wakeup Threshold"),
                true));
        misc.addView(settingNumberRow(
                "Keyboard Polling Interval",
                currentKeyboardPollingInterval(),
                "8",
                value -> applyKeyboardPollingInterval(value),
                false));
        root.addView(misc);
    }

    private void showLedBacklightPage() {
        startPage("LED Backlight", true);
        root.setTag("led");

        addSectionTitle("LED Backlight");
        LinearLayout led = card();
        led.addView(settingPickerRow(
                "LED Channel",
                currentLedChannel(),
                getLedChannels(),
                value -> {
                    setLedChannel(value);
                    showLedBacklightPage();
                },
                true));
        led.addView(settingSwitchRow(
                "LED State",
                ledStateSummary(),
                true,
                true,
                isLedEnabled(),
                checked -> setLedEnabled(checked)));
        led.addView(settingNumberRow(
                "LED Brightness",
                currentLedBrightness(),
                currentLedMaxBrightness(),
                value -> setLedBrightness(value),
                true));
        led.addView(settingPickerRow(
                "LED Trigger",
                currentLedTrigger(),
                getLedTriggers(),
                value -> applyLedTrigger(value),
                true));
        led.addView(settingSwitchRow(
                "Blink Timer",
                "Switch the selected LED trigger to the kernel timer mode.",
                true,
                true,
                isLedTimerEnabled(),
                checked -> setLedTimer(checked)));
        led.addView(settingNumberRow(
                "Blink Delay On",
                currentLedDelay("delay_on"),
                "500",
                value -> applyLedDelay("delay_on", value),
                true));
        led.addView(settingNumberRow(
                "Blink Delay Off",
                currentLedDelay("delay_off"),
                "500",
                value -> applyLedDelay("delay_off", value),
                false));
        root.addView(led);
    }

    private void showOverlayMonitorPage() {
        startPage("Overlay Monitor", true);
        root.setTag("overlay");

        addSectionTitle("Overlay Monitor");
        LinearLayout overlay = card();
        overlay.addView(settingSwitchRow(
                "Overlay Monitor",
                overlayMonitorSummary(),
                true,
                true,
                overlayMonitorEnabled(),
                checked -> setOverlayMonitorEnabled(checked)));
        overlay.addView(settingPickerRow(
                "Position",
                overlayPositionValue(),
                new ArrayList<>(Arrays.asList(OverlayPrefs.POSITION_VALUES)),
                value -> setOverlayPrefString(OverlayPrefs.KEY_POSITION, value),
                true));
        overlay.addView(settingSwitchRow(
                "Drag Overlay",
                "Allow moving the overlay temporarily; touch passthrough returns when off.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_DRAG_MODE, false),
                checked -> setOverlayDragMode(checked)));
        overlay.addView(settingActionRow(
                "Reset Overlay Position",
                "Return overlay to the selected preset and disable drag mode.",
                v -> resetOverlayPosition()));
        root.addView(overlay);

        addSectionTitle("Style");
        LinearLayout style = card();
        style.addView(settingNumberRow(
                "Text Size",
                overlayPrefIntText(OverlayPrefs.KEY_TEXT_SIZE, OverlayPrefs.DEFAULT_TEXT_SIZE),
                String.valueOf(OverlayPrefs.DEFAULT_TEXT_SIZE),
                value -> setOverlayPrefInt(OverlayPrefs.KEY_TEXT_SIZE, value, 8, 40, "Text Size"),
                true));
        style.addView(settingNumberRow(
                "Opacity",
                overlayPrefIntText(OverlayPrefs.KEY_OPACITY, OverlayPrefs.DEFAULT_OPACITY),
                String.valueOf(OverlayPrefs.DEFAULT_OPACITY),
                value -> setOverlayPrefInt(OverlayPrefs.KEY_OPACITY, value, 10, 100, "Opacity"),
                true));
        style.addView(settingSwitchRow(
                "Shadow",
                "Toggle text and panel shadow for readability.",
                true,
                false,
                overlayPrefBoolean(OverlayPrefs.KEY_SHADOW, OverlayPrefs.DEFAULT_SHADOW),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHADOW, checked)));
        root.addView(style);

        addSectionTitle("Visible Items");
        LinearLayout visible = card();
        visible.addView(settingSwitchRow(
                "CPU Frequency",
                "Show current CPU frequency.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_FREQ, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_FREQ, checked)));
        visible.addView(settingSwitchRow(
                "GPU Frequency",
                "Show current GPU frequency when a devfreq GPU node exists.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_FREQ, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_FREQ, checked)));
        visible.addView(settingSwitchRow(
                "RAM Usage",
                "Show used and total memory.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_RAM, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_RAM, checked)));
        visible.addView(settingSwitchRow(
                "Frame Per Second",
                "Show overlay frame pacing.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_FPS, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_FPS, checked)));
        visible.addView(settingSwitchRow(
                "Battery Temperature",
                "Show battery temperature.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_TEMP, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_TEMP, checked)));
        visible.addView(settingSwitchRow(
                "CPU Temperature",
                "Show CPU or SoC thermal-zone temperature.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_TEMP, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_CPU_TEMP, checked)));
        visible.addView(settingSwitchRow(
                "GPU Temperature",
                "Show GPU thermal-zone temperature when available.",
                true,
                true,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_TEMP, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_GPU_TEMP, checked)));
        visible.addView(settingSwitchRow(
                "Battery Percent",
                "Show battery percentage.",
                true,
                false,
                overlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_PERCENT, true),
                checked -> setOverlayPrefBoolean(OverlayPrefs.KEY_SHOW_BATTERY_PERCENT, checked)));
        root.addView(visible);
    }

    private void showBatteryPage() {
        startPage("Battery & Charging", true);
        root.setTag("battery");

        addSectionTitle("Battery Status");
        LinearLayout status = card();
        status.addView(settingInfoRow("Battery Percent", batteryValue("capacity"), "Current battery level from power_supply.", true));
        status.addView(settingInfoRow("Battery Status", batteryValue("status"), "Charging, discharging, full or unknown state.", true));
        status.addView(settingInfoRow("Battery Health", batteryValue("health"), "Health reported by the battery driver.", true));
        status.addView(settingInfoRow("Battery Temperature", batteryValue("temp"), "Battery sensor temperature.", true));
        status.addView(settingInfoRow("Battery Current", batteryValue("current_now"), "Instant battery current; negative often means charging.", true));
        status.addView(settingInfoRow("Battery Voltage", batteryValue("voltage_now"), "Current battery voltage.", true));
        status.addView(settingInfoRow("USB Type", powerSupplyValue("usb_type"), "USB/charger type exposed by power_supply.", true));
        status.addView(settingInfoRow("Input Current Limit", readFirstNode(INPUT_CURRENT_NODES), "Current input limit exposed by charger nodes.", true));
        status.addView(settingInfoRow("Charge Current", readFirstNode(CHARGE_CURRENT_NODES), "Constant charge current exposed by charger nodes.", true));
        status.addView(settingActionRow(
                "Refresh Battery Info",
                "Re-open this page and read the latest power_supply values.",
                v -> showBatteryPage()));
        root.addView(status);

        addSectionTitle("Charging Control");
        LinearLayout battery = card();
        battery.addView(settingSwitchRow(
                "Fast Charge",
                "Raise charger current limits when supported by the kernel.",
                true,
                true,
                isFastChargeEnabled(),
                isChecked -> setFastCharge(isChecked)));
        battery.addView(settingSwitchRow(
                "Bypass Charge",
                "Try to stop battery charge current while keeping external power active.",
                true,
                false,
                isBypassChargeEnabled(),
                isChecked -> setBypassCharge(isChecked)));
        root.addView(battery);

        addSectionTitle("AI Charge");
        LinearLayout aiCharge = card();
        aiCharge.addView(settingAiChargeLimitRow());
        aiCharge.addView(dividerView());
        aiCharge.addView(settingAiChargeStatusRow());
        aiCharge.addView(dividerView());
        aiCharge.addView(settingActionRow(
                "Disable AI Charge",
                "Remove auto-cut script and allow charging again.",
                v -> disableAiCharge()));
        root.addView(aiCharge);
        scheduleAutoRefresh("battery", () -> showBatteryPage(), 5000);
    }

    private void showDisplayPage() {
        startPage("Display", true);
        root.setTag("display");

        addSectionTitle("Display");
        LinearLayout display = card();
        display.addView(settingDisplayResolutionRow());
        display.addView(dividerView());
        display.addView(settingDisplayRefreshRow());
        display.addView(dividerView());
        display.addView(settingSwitchRow(
                "Show Refresh Rate",
                refreshRateOverlaySummary(),
                true,
                true,
                isRefreshRateOverlayEnabled(),
                checked -> setRefreshRateOverlay(checked)));
        display.addView(settingSwitchRow(
                "Force Peak Refresh Rate",
                forcePeakRefreshSummary(),
                true,
                true,
                isForcePeakRefreshEnabled(),
                checked -> setForcePeakRefreshRate(checked)));
        display.addView(settingGameFpsRow());
        display.addView(dividerView());
        display.addView(settingSwitchRow(
                "Disable HW Overlay",
                "Force GPU composition through SurfaceFlinger.",
                true,
                true,
                isHwOverlayDisabled(),
                checked -> setHwOverlayDisabled(checked)));
        display.addView(settingActionRow(
                "Reset Display Tweaks",
                "Restore size, refresh and overlay defaults.",
                v -> resetDisplayTweaks()));
        root.addView(display);

        addSectionTitle("Debug");
        LinearLayout debug = card();
        debug.addView(settingActionRow(
                "Display Debug",
                "Open panel and backlight node readings.",
                v -> showDisplayDebugPage()));
        root.addView(debug);
    }

    private void showDisplayDebugPage() {
        startPage("Display Debug", true);
        root.setTag("display_debug");

        addSectionTitle("Panel Info");
        LinearLayout panel = card();
        panel.addView(settingInfoRow("Panel FPS", readFirstExistingNodeLine(DISPLAY_PANEL_FPS_NODES), "FPS reported by the panel driver.", true));
        panel.addView(settingInfoRow("Actual FPS", readFirstExistingNodeLine(DISPLAY_ACTUAL_FPS_NODES), "Current display controller FPS when exposed.", true));
        panel.addView(settingInfoRow("Max FPS", readFirstExistingNodeLine(DISPLAY_MAX_FPS_NODES), "Highest panel FPS node value.", true));
        panel.addView(settingInfoRow("Pixel Clock", readFirstExistingNodeLine(DISPLAY_PIXEL_CLOCK_NODES), "Panel pixel clock from display sysfs.", true));
        panel.addView(settingInfoRow("Screen Size", readFirstExistingNodeLine(DISPLAY_SCREEN_SIZE_NODES), "Physical panel size from display sysfs.", true));
        panel.addView(settingInfoRow("Raw Brightness", readFirstExistingNodeLine(DISPLAY_BACKLIGHT_BRIGHTNESS_NODES), "Current backlight raw brightness.", true));
        panel.addView(settingInfoRow("Max Brightness", readFirstExistingNodeLine(DISPLAY_BACKLIGHT_MAX_NODES), "Maximum backlight raw brightness.", true));
        panel.addView(settingActionRow(
                "Refresh Panel Info",
                "Re-open this page and read display nodes again.",
                v -> showDisplayDebugPage()));
        root.addView(panel);

        addSectionTitle("Panel Safety");
        LinearLayout safety = card();
        safety.addView(settingSwitchRow(
                "ESD Check",
                kernelSwitchSummary(DISPLAY_ESD_CHECK_ENABLE_NODE),
                true,
                true,
                kernelSwitchEnabled(DISPLAY_ESD_CHECK_ENABLE_NODE),
                checked -> applyKernelSwitchNode(DISPLAY_ESD_CHECK_ENABLE_NODE, checked, "ESD Check")));
        safety.addView(settingNumberRow(
                "ESD Check Period",
                kernelNodeValue(DISPLAY_ESD_CHECK_PERIOD_NODE),
                "2000",
                value -> applyKernelNodeValue(DISPLAY_ESD_CHECK_PERIOD_NODE, value, "ESD Check Period"),
                false));
        root.addView(safety);
    }

    private void showTouchSettingsPage() {
        startPage("Touch Settings", true);
        root.setTag("touch");

        addSectionTitle("Touch Info");
        LinearLayout info = card();
        info.addView(settingInfoRow("Touch Device", touchInputName(), "Detected touchscreen input device.", true));
        info.addView(settingInfoRow("Touch Vendor", readSysNode("/sys/transsion/hwinfo/touch_vendor"), "Vendor info when exposed by the ROM.", true));
        info.addView(settingInfoRow("Touch IC", readSysNode("/sys/transsion/hwinfo/touch_ic"), "Touch controller IC name when available.", true));
        info.addView(settingInfoRow("Touch Firmware", readSysNode("/sys/transsion/hwinfo/touch_firmware_version"), "Touch firmware version when available.", false));
        root.addView(info);

        addSectionTitle("Touch Controls");
        LinearLayout touch = card();
        touch.addView(settingSwitchRow(
                "Glove Mode",
                touchSwitchSummary("enable_glove"),
                true,
                true,
                touchSwitchEnabled("enable_glove"),
                checked -> applyTouchSwitch("enable_glove", checked, "Glove Mode")));
        touch.addView(settingSwitchRow(
                "Thick Glove Mode",
                touchSwitchSummary("enable_thick_glove"),
                true,
                true,
                touchSwitchEnabled("enable_thick_glove"),
                checked -> applyTouchSwitch("enable_thick_glove", checked, "Thick Glove Mode")));
        touch.addView(settingSwitchRow(
                "Grip Suppression",
                touchSwitchSummary("grip_suppression_enabled"),
                true,
                true,
                touchSwitchEnabled("grip_suppression_enabled"),
                checked -> applyTouchSwitch("grip_suppression_enabled", checked, "Grip Suppression")));
        touch.addView(settingSwitchRow(
                "Wakeup Gesture Mode",
                touchSwitchSummary("in_wakeup_gesture_mode"),
                true,
                true,
                touchSwitchEnabled("in_wakeup_gesture_mode"),
                checked -> applyTouchSwitch("in_wakeup_gesture_mode", checked, "Wakeup Gesture Mode")));
        touch.addView(settingSwitchRow(
                "No Doze",
                touchSwitchSummary("no_doze"),
                true,
                true,
                touchSwitchEnabled("no_doze"),
                checked -> applyTouchSwitch("no_doze", checked, "No Doze")));
        touch.addView(settingSwitchRow(
                "Rezero On Exit Deep Sleep",
                touchSwitchSummary("rezero_on_exit_deep_sleep"),
                true,
                true,
                touchSwitchEnabled("rezero_on_exit_deep_sleep"),
                checked -> applyTouchSwitch("rezero_on_exit_deep_sleep", checked, "Rezero On Exit Deep Sleep")));
        touch.addView(settingSwitchRow(
                "Disable Noise Mitigation",
                touchSwitchSummary("disable_noise_mitigation"),
                true,
                true,
                touchSwitchEnabled("disable_noise_mitigation"),
                checked -> applyTouchSwitch("disable_noise_mitigation", checked, "Disable Noise Mitigation")));
        touch.addView(settingNumberRow(
                "Touch Requested Frequency",
                touchNodeValue("requested_frequency"),
                "0",
                value -> applyTouchValue("requested_frequency", value, "Touch Requested Frequency"),
                false));
        root.addView(touch);
    }

    private void showThermalMonitorPage() {
        startPage("Thermal Monitor", true);
        root.setTag("thermal");

        addSectionTitle("Thermal Zones");
        LinearLayout thermal = card();
        thermal.addView(settingInfoRow("CPU Temperature", thermalTemp("cpu"), "CPU cluster or SoC thermal-zone reading.", true));
        thermal.addView(settingInfoRow("GPU Temperature", thermalTemp("gpu"), "GPU thermal-zone reading when available.", true));
        thermal.addView(settingInfoRow("Board Temperature", thermalTemp("board"), "Board or PCB thermal-zone reading.", true));
        thermal.addView(settingInfoRow("Battery Temperature", thermalTemp("battery"), "Battery thermal-zone reading.", true));
        thermal.addView(settingInfoRow("Charger Temperature", thermalTemp("charger"), "Charger thermal-zone reading.", true));
        thermal.addView(settingActionRow(
                "Refresh Thermal Info",
                "Re-open this page and read thermal zones again.",
                v -> showThermalMonitorPage()));
        root.addView(thermal);

        addSectionTitle("Cooling Devices");
        LinearLayout cooling = card();
        cooling.addView(settingInfoRow("CPU Cooling State", coolingState("cpu"), "Current CPU cooling throttle state.", true));
        cooling.addView(settingInfoRow("GPU Cooling State", coolingState("gpu"), "Current GPU cooling throttle state.", true));
        cooling.addView(settingInfoRow("Thermal Zone Count", thermalZoneCount(), "Number of thermal zones exposed by the kernel.", false));
        root.addView(cooling);
        scheduleAutoRefresh("thermal", () -> showThermalMonitorPage(), 5000);
    }

    private void showDevfreqPage() {
        startPage("Device Frequency", true);
        root.setTag("devfreq");

        addSectionTitle("Devfreq Device");
        LinearLayout devfreq = card();
        devfreq.addView(settingPickerRow(
                "Devfreq Device",
                currentDevfreqDevice(),
                getDevfreqDevices(),
                value -> {
                    setDevfreqDevice(value);
                    showDevfreqPage();
                },
                true));
        devfreq.addView(settingInfoRow("Current Frequency", devfreqValue("cur_freq"), "Current clock for the selected device.", true));
        devfreq.addView(settingInfoRow("Target Frequency", devfreqValue("target_freq"), "Target clock requested by the governor.", true));
        devfreq.addView(settingPickerRow(
                "Devfreq Governor",
                devfreqValue("governor"),
                getDevfreqGovernors(),
                value -> applyDevfreqNode("governor", value, "Devfreq Governor"),
                true));
        devfreq.addView(settingPickerRow(
                "Devfreq Min Frequency",
                devfreqValue("min_freq"),
                getDevfreqFrequencies(),
                value -> applyDevfreqNode("min_freq", value, "Devfreq Min Frequency"),
                true));
        devfreq.addView(settingPickerRow(
                "Devfreq Max Frequency",
                devfreqValue("max_freq"),
                getDevfreqFrequencies(),
                value -> applyDevfreqNode("max_freq", value, "Devfreq Max Frequency"),
                false));
        root.addView(devfreq);

        addSectionTitle("GPU Driver Advanced");
        LinearLayout gpu = card();
        gpu.addView(settingNumberRow(
                "GPU Polling Time",
                kernelNodeValue(GPU_POLLINGTIME_NODE),
                "50",
                value -> applyKernelNodeValue(GPU_POLLINGTIME_NODE, value, "GPU Polling Time"),
                true));
        gpu.addView(settingNumberRow(
                "GPU Up Threshold",
                kernelNodeValue(GPU_UPTHRESHOLD_NODE),
                "40",
                value -> applyKernelNodeValue(GPU_UPTHRESHOLD_NODE, value, "GPU Up Threshold"),
                true));
        gpu.addView(settingNumberRow(
                "GPU Down Differential",
                kernelNodeValue(GPU_DOWNDIFFERENTIAL_NODE),
                "5",
                value -> applyKernelNodeValue(GPU_DOWNDIFFERENTIAL_NODE, value, "GPU Down Differential"),
                true));
        gpu.addView(settingNumberRow(
                "GPU Boost Level",
                kernelNodeValue(GPU_BOOST_LEVEL2_NODE),
                "0",
                value -> applyGpuBoostLevel(value),
                false));
        root.addView(gpu);
        scheduleAutoRefresh("devfreq", () -> showDevfreqPage(), 5000);
    }

    private void showNetworkPage() {
        startPage("Network Tweaks", true);
        root.setTag("network");

        addSectionTitle("TCP");
        LinearLayout tcp = card();
        tcp.addView(settingPickerRow(
                "TCP Congestion Algorithm",
                kernelNodeValue(TCP_CONGESTION_CONTROL_NODE),
                getTcpCongestionAlgorithms(),
                value -> applyKernelNodeValue(TCP_CONGESTION_CONTROL_NODE, value, "TCP Congestion Algorithm"),
                true));
        tcp.addView(settingNumberRow(
                "TCP Fast Open",
                kernelNodeValue(TCP_FASTOPEN_NODE),
                "3",
                value -> applyKernelNodeValue(TCP_FASTOPEN_NODE, value, "TCP Fast Open"),
                true));
        tcp.addView(settingNumberRow(
                "TCP ECN",
                kernelNodeValue(TCP_ECN_NODE),
                "0",
                value -> applyKernelNodeValue(TCP_ECN_NODE, value, "TCP ECN"),
                true));
        tcp.addView(settingSwitchRow(
                "TCP Timestamps",
                kernelSwitchSummary(TCP_TIMESTAMPS_NODE),
                true,
                true,
                kernelSwitchEnabled(TCP_TIMESTAMPS_NODE),
                checked -> applyKernelSwitchNode(TCP_TIMESTAMPS_NODE, checked, "TCP Timestamps")));
        tcp.addView(settingSwitchRow(
                "TCP Low Latency",
                kernelSwitchSummary(TCP_LOW_LATENCY_NODE),
                true,
                true,
                kernelSwitchEnabled(TCP_LOW_LATENCY_NODE),
                checked -> applyKernelSwitchNode(TCP_LOW_LATENCY_NODE, checked, "TCP Low Latency")));
        tcp.addView(settingSwitchRow(
                "TCP Slow Start After Idle",
                kernelSwitchSummary(TCP_SLOW_START_AFTER_IDLE_NODE),
                true,
                true,
                kernelSwitchEnabled(TCP_SLOW_START_AFTER_IDLE_NODE),
                checked -> applyKernelSwitchNode(TCP_SLOW_START_AFTER_IDLE_NODE, checked, "TCP Slow Start After Idle")));
        tcp.addView(settingSwitchRow(
                "TCP TW Reuse",
                kernelSwitchSummary(TCP_TW_REUSE_NODE),
                true,
                true,
                kernelSwitchEnabled(TCP_TW_REUSE_NODE),
                checked -> applyKernelSwitchNode(TCP_TW_REUSE_NODE, checked, "TCP TW Reuse")));
        tcp.addView(settingSwitchRow(
                "TCP Syncookies",
                kernelSwitchSummary(TCP_SYNCOOKIES_NODE),
                true,
                true,
                kernelSwitchEnabled(TCP_SYNCOOKIES_NODE),
                checked -> applyKernelSwitchNode(TCP_SYNCOOKIES_NODE, checked, "TCP Syncookies")));
        tcp.addView(settingNumberRow(
                "TCP MTU Probing",
                kernelNodeValue(TCP_MTU_PROBING_NODE),
                "0",
                value -> applyKernelNodeValue(TCP_MTU_PROBING_NODE, value, "TCP MTU Probing"),
                false));
        root.addView(tcp);
    }

    private void showGovernorPage() {
        startPage("Cpu & Gpu Governor", true);
        root.setTag("governor");

        addSectionTitle("Cpu");
        root.addView(pickerRow(
                "Governor",
                currentCpuGovernor(),
                getCpuGovernors(),
                value -> applyCpuGovernor(value)));

        addGap(10);
        addSectionTitle("Gpu");
        root.addView(pickerRow(
                "Governor",
                currentGpuGovernor(),
                getGpuGovernors(),
                value -> applyGpuGovernor(value)));
    }

    private void showFrequencyPage() {
        startPage("Cpu Frequency", true);
        root.setTag("frequency");

        addSectionTitle("Cpu");
        root.addView(pickerRow(
                "Little Frequency",
                currentCpuLittleFrequency(),
                getCpuLittleFrequencies(),
                value -> applyCpuLittleFrequency(value)));

        addGap(10);
        root.addView(pickerRow(
                "Big Frequency",
                currentCpuBigFrequency(),
                getCpuBigFrequencies(),
                value -> applyCpuBigFrequency(value)));

        addGap(10);
        addSectionTitle("Gpu");
        root.addView(pickerRow(
                "Max Frequency",
                currentGpuFrequency(),
                getGpuFrequencies(),
                value -> applyGpuFrequency(value)));
    }

    private void startPage(String title, boolean showBack) {
        dismissPopup();
        stopAutoRefresh();

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BG);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), 0, dp(14), dp(18));
        root.setBackgroundColor(BG);
        scrollView.setOnApplyWindowInsetsListener((v, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            root.setPadding(dp(14), top + dp(2), dp(14), Math.max(dp(18), bottom + dp(6)));
            return insets;
        });
        scrollView.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));
        setContentView(scrollView);
        scrollView.requestApplyInsets();

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setClipChildren(false);
        header.setClipToPadding(false);
        header.setPadding(0, 0, 0, dp(6));
        root.addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(42)));

        TextView back = new TextView(this);
        back.setText(showBack ? "\u2039" : "\u00d7");
        back.setTextColor(Color.rgb(45, 45, 45));
        back.setTextSize(20);
        back.setGravity(Gravity.CENTER);
        back.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        back.setIncludeFontPadding(false);
        back.setBackground(oval(Color.WHITE, 0, 0));
        back.setElevation(dp(1));
        back.setOnClickListener(v -> {
            if (showBack) {
                showMainPage();
            } else {
                finish();
            }
        });
        header.addView(back, new LinearLayout.LayoutParams(dp(34), dp(34)));

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(16);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setIncludeFontPadding(false);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f);
        titleLp.leftMargin = dp(10);
        header.addView(titleView, titleLp);
    }

    private void scheduleAutoRefresh(String pageTag, Runnable refreshAction, long delayMs) {
        stopAutoRefresh();
        autoRefreshRunnable = () -> {
            if (root == null || !pageTag.equals(String.valueOf(root.getTag()))) {
                return;
            }
            if (activePopup != null && activePopup.isShowing()) {
                scheduleAutoRefresh(pageTag, refreshAction, delayMs);
                return;
            }
            refreshAction.run();
        };
        uiHandler.postDelayed(autoRefreshRunnable, delayMs);
    }

    private void stopAutoRefresh() {
        if (autoRefreshRunnable != null) {
            uiHandler.removeCallbacks(autoRefreshRunnable);
            autoRefreshRunnable = null;
        }
    }

    private void addSectionTitle(String text) {
        TextView section = new TextView(this);
        section.setText(text);
        section.setTextColor(Color.rgb(128, 128, 128));
        section.setTextSize(14);
        section.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        section.setIncludeFontPadding(false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = dp(12);
        lp.rightMargin = dp(12);
        lp.topMargin = dp(2);
        lp.bottomMargin = dp(7);
        root.addView(section, lp);
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(7), dp(12), dp(7));
        card.setBackground(round(Color.WHITE, dp(8), 0, 0));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 0;
        lp.rightMargin = 0;
        lp.bottomMargin = dp(16);
        card.setLayoutParams(lp);
        return card;
    }

    private View settingPickerRow(String title, String selected, ArrayList<String> values, PickerAction action, boolean divider) {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, title, featureDescription(title));

        TextView value = new TextView(this);
        value.setText(displayRawValue(selected));
        value.setTextColor(SUMMARY);
        value.setTextSize(13);
        value.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(true);
        row.addView(value, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showPicker(value, arrow, values, selectedValue -> {
            value.setText(displayRawValue(selectedValue));
            action.onPick(selectedValue);
        }));
        value.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());

        if (divider) {
            holder.addView(dividerView());
        }
        return holder;
    }

    private View settingNumberRow(String title, String selected, String hint, PickerAction action, boolean divider) {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, title, featureDescription(title));

        TextView value = new TextView(this);
        value.setText(displayRawValue(selected));
        value.setTextColor(SUMMARY);
        value.setTextSize(13);
        value.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(true);
        row.addView(value, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> {
            if ("Not detected".equals(value.getText().toString().trim())) {
                toast(title + " not detected");
                return;
            }
            showKernelNumberDialog(title, value, hint, action);
        });
        value.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());

        if (divider) {
            holder.addView(dividerView());
        }
        return holder;
    }

    private View settingCategoryRow(String title) {
        LinearLayout holder = rowHolder();
        TextView label = new TextView(this);
        label.setText(title);
        label.setTextColor(SWITCH_ON);
        label.setTextSize(13);
        label.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setIncludeFontPadding(false);
        label.setPadding(0, dp(10), 0, dp(4));
        holder.addView(label, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(34)));
        return holder;
    }

    private View settingRendererRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Renderer", featureDescription("Renderer"));

        TextView value = new TextView(this);
        value.setText(rendererLabel(currentRenderer()));
        value.setTextColor(SUMMARY);
        value.setTextSize(13);
        value.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(true);
        row.addView(value, new LinearLayout.LayoutParams(dp(112), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showRendererPicker(value, arrow));
        value.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingRendererStatusRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Current Rendering", featureDescription("Current Rendering"));

        rendererStatusView = new TextView(this);
        rendererStatusView.setText(rendererLabel(currentRenderer()));
        rendererStatusView.setTextColor(SUMMARY);
        rendererStatusView.setTextSize(13);
        rendererStatusView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        rendererStatusView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        rendererStatusView.setSingleLine(true);
        row.addView(rendererStatusView, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        row.setOnClickListener(v -> {
            rendererStatusView.setText(rendererLabel(currentRenderer()));
            toast("Rendering status refreshed");
        });
        rendererStatusView.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingGameAppRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "App", featureDescription("App"));

        TextView value = new TextView(this);
        value.setText(selectedGameLabel());
        value.setTextColor(SUMMARY);
        value.setTextSize(13);
        value.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(true);
        row.addView(value, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showGameAppPicker(value, arrow));
        value.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingGameDriverRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Driver", featureDescription("Driver"));

        gameDriverValueView = new TextView(this);
        gameDriverValueView.setText(gameDriverLabel(selectedGameDriverMode()));
        gameDriverValueView.setTextColor(SUMMARY);
        gameDriverValueView.setTextSize(13);
        gameDriverValueView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        gameDriverValueView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        gameDriverValueView.setSingleLine(true);
        row.addView(gameDriverValueView, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showGameDriverPicker(gameDriverValueView, arrow));
        gameDriverValueView.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingStorageStatusRow(String title, String valueText, int target, boolean divider) {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, title, featureDescription(title));

        TextView value = new TextView(this);
        value.setText(valueText);
        value.setTextColor(SUMMARY);
        value.setTextSize(13);
        value.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(true);
        row.addView(value, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        if (target == 0) {
            storageTypeView = value;
        } else if (target == 1) {
            storageLifeAView = value;
        } else if (target == 2) {
            storageLifeBView = value;
        }

        row.setOnClickListener(v -> refreshStorageHealth());
        value.setOnClickListener(v -> row.performClick());

        if (divider) {
            holder.addView(dividerView());
        }
        return holder;
    }

    private View settingAiChargeLimitRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Stop Charge At", featureDescription("Stop Charge At"));

        TextView value = new TextView(this);
        value.setText(aiChargeLimitLabel(currentAiChargeLimit()));
        value.setTextColor(SUMMARY);
        value.setTextSize(13);
        value.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(true);
        row.addView(value, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showAiChargeLimitPicker(value, arrow));
        value.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingAiChargeStatusRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Current Status", featureDescription("Current Status"));

        aiChargeStatusView = new TextView(this);
        aiChargeStatusView.setText(aiChargeStatus());
        aiChargeStatusView.setTextColor(SUMMARY);
        aiChargeStatusView.setTextSize(13);
        aiChargeStatusView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        aiChargeStatusView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        aiChargeStatusView.setSingleLine(true);
        row.addView(aiChargeStatusView, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        row.setOnClickListener(v -> refreshAiChargeStatus());
        aiChargeStatusView.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingDisplayResolutionRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Resolution", featureDescription("Resolution"));

        displayResolutionView = new TextView(this);
        displayResolutionView.setText(currentDisplaySize());
        displayResolutionView.setTextColor(SUMMARY);
        displayResolutionView.setTextSize(13);
        displayResolutionView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        displayResolutionView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        displayResolutionView.setSingleLine(true);
        row.addView(displayResolutionView, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showResolutionPicker(displayResolutionView, arrow));
        displayResolutionView.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingDisplayRefreshRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Force Refresh Rate", featureDescription("Force Refresh Rate"));

        displayRefreshView = new TextView(this);
        displayRefreshView.setText(refreshRateLabel(currentRefreshRate()));
        displayRefreshView.setTextColor(SUMMARY);
        displayRefreshView.setTextSize(13);
        displayRefreshView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        displayRefreshView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        displayRefreshView.setSingleLine(true);
        row.addView(displayRefreshView, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showRefreshPicker(displayRefreshView, arrow));
        displayRefreshView.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingGameFpsRow() {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, "Game Default FPS", featureDescription("Game Default FPS"));

        gameFpsView = new TextView(this);
        gameFpsView.setText(refreshRateLabel(currentGameFpsOverride()));
        gameFpsView.setTextColor(SUMMARY);
        gameFpsView.setTextSize(13);
        gameFpsView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        gameFpsView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        gameFpsView.setSingleLine(true);
        row.addView(gameFpsView, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(18), dp(44)));

        row.setOnClickListener(v -> showGameFpsPicker(gameFpsView, arrow));
        gameFpsView.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return holder;
    }

    private View settingActionRow(String title, String summary, View.OnClickListener listener) {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(6), 0, dp(6));
        row.setOnClickListener(listener);
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(14);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        titleView.setIncludeFontPadding(false);
        titleView.setPadding(0, 0, 0, dp(3));
        texts.addView(titleView);

        TextView summaryView = new TextView(this);
        summaryView.setText(summary);
        summaryView.setTextColor(SUMMARY);
        summaryView.setTextSize(13);
        summaryView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        summaryView.setIncludeFontPadding(false);
        texts.addView(summaryView);

        row.addView(arrowText(">"), new LinearLayout.LayoutParams(dp(18), LinearLayout.LayoutParams.MATCH_PARENT));
        return holder;
    }

    private View settingInfoRow(String title, String valueText, String summary, boolean divider) {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(7), 0, dp(7));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        addSettingTexts(texts, title, summary);

        TextView value = new TextView(this);
        value.setText(displayRawValue(valueText));
        value.setTextColor(SUMMARY);
        value.setTextSize(13);
        value.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(false);
        value.setMaxLines(2);
        value.setIncludeFontPadding(false);
        row.addView(value, new LinearLayout.LayoutParams(dp(124), LinearLayout.LayoutParams.WRAP_CONTENT));

        if (divider) {
            holder.addView(dividerView());
        }
        return holder;
    }

    private void addSettingTexts(LinearLayout texts, String title, String summary) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(14);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        titleView.setIncludeFontPadding(false);
        titleView.setPadding(0, 0, 0, dp(3));
        texts.addView(titleView);

        TextView summaryView = new TextView(this);
        summaryView.setText(summary == null || summary.length() == 0 ? "Uses a detected kernel or Android setting when available." : summary);
        summaryView.setTextColor(SUMMARY);
        summaryView.setTextSize(12);
        summaryView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        summaryView.setLineSpacing(0, 1.0f);
        summaryView.setIncludeFontPadding(false);
        texts.addView(summaryView);
    }

    private String featureDescription(String title) {
        if (title == null) {
            return "";
        }
        switch (title) {
            case "Renderer":
                return "Selects Android HWUI and RenderEngine backend properties.";
            case "Current Rendering":
                return "Tap to refresh renderer property state.";
            case "App":
                return "Chooses an installed user app for per-app graphics driver settings.";
            case "Driver":
                return "Selects default, game, system or developer graphics driver mode.";
            case "Storage Type":
                return "Detected storage family from UFS or eMMC health nodes.";
            case "Life A":
                return "Storage lifetime estimation A; lower wear usually means higher percent.";
            case "Life B":
                return "Storage lifetime estimation B when the device exposes it.";
            case "Stop Charge At":
                return "Battery percentage where AI Charge will cut charging.";
            case "Current Status":
                return "Shows AI Charge script state and current cut/charge status.";
            case "Resolution":
                return "Changes Android logical display size with wm size.";
            case "Force Refresh Rate":
                return "Writes Android and vendor refresh-rate settings for the selected target.";
            case "Game Default FPS":
                return "Sets SurfaceFlinger game default frame-rate override property.";
            case "Game Performance Mode":
                return "Chooses the stock Game Space power, balanced or performance profile.";
            case "Graphic Enhancement":
                return "Toggles the stock Game Space PQ flag; real effect still depends on vendor-supported game list.";
            case "Enhancement Mode":
                return "Selects the stock PQ style; this ROM uses 0-2 unless screen mode exposes Realistic.";
            case "Stock PQ Panel":
                return "Shows whether the ROM image-enhancement settings page can be opened.";
            case "Frame Interpolation Panel":
                return "Shows whether the hidden SmartPanel frame-interpolation page is disabled by the ROM.";
            case "Open Stock Image Enhancement":
                return "Opens the ROM PQ page to check which games the vendor exposes.";
            case "Open Hidden Frame Interpolation":
                return "Enables and opens the hidden SmartPanel frame-interpolation page for game mode.";
            case "Open Video HDR Panel":
                return "Opens the hidden SmartPanel page in video HDR mode.";
            case "CPU Governor":
                return "Controls how CPU frequency scales between power saving and performance.";
            case "CPU Little Frequency":
                return "Sets max frequency for the first detected CPU cluster.";
            case "CPU Big Frequency":
                return "Sets max frequency for the last detected CPU cluster.";
            case "GPU Governor":
                return "Controls GPU devfreq scaling behavior.";
            case "GPU Max Frequency":
                return "Locks the detected GPU devfreq max and min frequency.";
            case "I/O Scheduler":
                return "Selects block scheduler for detected storage queues.";
            case "Read Ahead KB":
                return "Controls storage read-ahead cache size.";
            case "NR Requests":
                return "Controls max queued block requests.";
            case "RQ Affinity":
                return "Controls CPU affinity behavior for block I/O completions.";
            case "No Merges":
                return "Controls whether the block layer merges adjacent I/O requests.";
            case "Zram Size":
                return "Sets compressed swap disk size for zram0.";
            case "Compression Algorithm":
                return "Chooses zram compression algorithm supported by the kernel.";
            case "Zram Max Comp Streams":
                return "Sets zram compression worker stream count.";
            case "Swappiness":
                return "Controls how aggressively memory is moved to swap.";
            case "Dirty Ratio":
                return "Max dirty page percentage before writeback becomes forced.";
            case "Dirty Background Ratio":
                return "Dirty page percentage where background writeback begins.";
            case "Dirty Writeback Centisecs":
                return "Interval for kernel dirty-page writeback.";
            case "Minimum Amount Of Free Memory":
                return "Minimum free RAM watermark in KB.";
            case "Overcommit Ratio":
                return "Memory overcommit percentage used by the VM.";
            case "Laptop Mode":
                return "Delays disk writeback to reduce I/O wakeups.";
            case "VFS Cache Pressure":
                return "Controls how quickly inode/dentry cache is reclaimed.";
            case "Sched Deadline Period Max Us":
                return "Maximum scheduler deadline period in microseconds.";
            case "Sched Deadline Period Min Us":
                return "Minimum scheduler deadline period in microseconds.";
            case "Sched Pelt Multiplier":
                return "Controls PELT utilization tracking scale when exposed.";
            case "Sched Rr Timeslice Ms":
                return "Round-robin scheduler time slice in milliseconds.";
            case "Sched Rt Period Us":
                return "Real-time scheduler accounting period.";
            case "Sched Util Clamp Max":
                return "Upper utilization clamp for scheduler tasks.";
            case "Sched Util Clamp Min":
                return "Lower utilization clamp for scheduler tasks.";
            case "TCP Congestion Algorithm":
                return "Chooses TCP congestion-control algorithm supported by the kernel.";
            case "Pstore Compress":
                return "Chooses compression for persistent kernel crash logs.";
            case "Entropy Write Wakeup Threshold":
                return "Random pool threshold that wakes writers.";
            case "Keyboard Polling Interval":
                return "Detected keyboard/key input polling interval.";
            case "LED Channel":
                return "Selects one LED class device exposed by the kernel.";
            case "LED Brightness":
                return "Writes raw brightness to the selected LED channel.";
            case "LED Trigger":
                return "Chooses the kernel LED trigger source.";
            case "Blink Delay On":
                return "Timer trigger on-duration in milliseconds.";
            case "Blink Delay Off":
                return "Timer trigger off-duration in milliseconds.";
            case "Position":
                return "Chooses overlay preset position unless custom drag is active.";
            case "Text Size":
                return "Overlay text size in scaled pixels.";
            case "Opacity":
                return "Overlay background opacity percentage.";
            case "Devfreq Device":
                return "Selects an auto-detected devfreq device.";
            case "Devfreq Governor":
                return "Sets scaling governor for the selected devfreq device.";
            case "Devfreq Min Frequency":
                return "Sets minimum frequency for the selected devfreq device.";
            case "Devfreq Max Frequency":
                return "Sets maximum frequency for the selected devfreq device.";
            case "GPU Polling Time":
                return "Mali governor polling interval when the driver exposes it.";
            case "GPU Up Threshold":
                return "Mali utilization threshold for ramping frequency up.";
            case "GPU Down Differential":
                return "Mali downscale hysteresis value.";
            case "GPU Boost Level":
                return "Mali boost parameter; the app verifies the value after writing.";
            case "ESD Check Period":
                return "Panel ESD check interval in milliseconds.";
            case "Touch Requested Frequency":
                return "Requested touchscreen scan/report frequency node.";
            case "TCP Fast Open":
                return "Controls TCP Fast Open client/server mode.";
            case "TCP ECN":
                return "Controls Explicit Congestion Notification behavior.";
            case "TCP MTU Probing":
                return "Controls TCP path MTU probing mode.";
            default:
                return "Uses the detected Android setting or kernel node when available.";
        }
    }

    private View settingSwitchRow(String title, String summary, boolean interactive, boolean divider, ToggleAction listener) {
        return settingSwitchRow(title, summary, interactive, divider, false, listener);
    }

    private View settingSwitchRow(String title, String summary, boolean interactive, boolean divider, boolean checked, ToggleAction listener) {
        LinearLayout holder = rowHolder();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(6), 0, dp(6));
        holder.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(14);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        titleView.setIncludeFontPadding(false);
        titleView.setPadding(0, 0, 0, dp(3));
        texts.addView(titleView);

        TextView summaryView = new TextView(this);
        summaryView.setText(summary);
        summaryView.setTextColor(SUMMARY);
        summaryView.setTextSize(13);
        summaryView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        summaryView.setLineSpacing(0, 1.0f);
        summaryView.setIncludeFontPadding(false);
        texts.addView(summaryView);

        MiniSwitch sw = new MiniSwitch(interactive, checked);
        sw.setOnToggleChangedListener(listener);
        LinearLayout.LayoutParams swLp = new LinearLayout.LayoutParams(dp(42), dp(26));
        swLp.leftMargin = dp(8);
        row.addView(sw, swLp);

        if (divider) {
            holder.addView(dividerView());
        }
        return holder;
    }

    private LinearLayout rowHolder() {
        LinearLayout holder = new LinearLayout(this);
        holder.setOrientation(LinearLayout.VERTICAL);
        holder.setPadding(0, 0, 0, 0);
        return holder;
    }

    private View dividerView() {
        View divider = new View(this);
        divider.setBackgroundColor(DIVIDER);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1));
        return divider;
    }

    private View navRow(String title, String summary, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 0, 0, dp(16));
        row.setOnClickListener(listener);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        texts.addView(titleText(title));
        texts.addView(summaryText(summary));
        row.addView(arrowText(">"), new LinearLayout.LayoutParams(dp(24), dp(48)));
        return row;
    }

    private View switchRow(String title, String summary, boolean interactive, ToggleAction listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 0, 0, dp(2));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        texts.addView(titleText(title));
        texts.addView(summaryText(summary));

        MiniSwitch sw = new MiniSwitch(interactive);
        sw.setOnToggleChangedListener(listener);
        LinearLayout.LayoutParams swLp = new LinearLayout.LayoutParams(dp(42), dp(26));
        swLp.leftMargin = dp(8);
        row.addView(sw, swLp);
        return row;
    }

    private View pickerRow(String title, String selected, ArrayList<String> values, PickerAction action) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), 0, dp(6), 0);
        row.setBackground(round(Color.WHITE, dp(18), 0, 0));
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(40));
        row.setLayoutParams(rowLp);

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        label.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setIncludeFontPadding(false);
        row.addView(label, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

        TextView value = new TextView(this);
        value.setText(displayValue(selected));
        value.setTextColor(Color.BLACK);
        value.setTextSize(14);
        value.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        value.setSingleLine(false);
        row.addView(value, new LinearLayout.LayoutParams(dp(112), LinearLayout.LayoutParams.MATCH_PARENT));

        TextView arrow = arrowText(">");
        row.addView(arrow, new LinearLayout.LayoutParams(dp(22), LinearLayout.LayoutParams.MATCH_PARENT));

        row.setOnClickListener(v -> showPicker(value, arrow, values, selectedValue -> {
            value.setText(displayValue(selectedValue));
            action.onPick(selectedValue);
        }));
        value.setOnClickListener(v -> row.performClick());
        arrow.setOnClickListener(v -> row.performClick());
        return row;
    }

    private void showPicker(TextView anchor, TextView arrow, ArrayList<String> values, PickerAction action) {
        if (values.size() == 0 || "Not detected".equals(values.get(0))) {
            toast("Option not detected");
            return;
        }

        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = popupMenu();
        for (String item : values) {
            TextView option = popupOption(displayValue(item));
            menu.addView(option);
            option.setOnClickListener(v -> {
                action.onPick(item);
                dismissPopup();
            });
        }

        showPopupMenu(anchor, menu, values.size(), 240, 92, -6);
    }

    private void showRendererPicker(TextView anchor, TextView arrow) {
        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = popupMenu();

        for (int i = 0; i < RENDERER_VALUES.length; i++) {
            final String renderer = RENDERER_VALUES[i];
            TextView option = popupOption(RENDERER_LABELS[i]);
            menu.addView(option);
            option.setOnClickListener(v -> {
                anchor.setText(rendererLabel(renderer));
                applyRenderer(renderer);
                dismissPopup();
            });
        }

        showPopupMenu(anchor, menu, RENDERER_VALUES.length, 220, 92, -6);
    }

    private void showGameDriverPicker(TextView anchor, TextView arrow) {
        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = popupMenu();

        for (int i = 0; i < GAME_DRIVER_VALUES.length; i++) {
            final String mode = GAME_DRIVER_VALUES[i];
            TextView option = popupOption(GAME_DRIVER_LABELS[i]);
            menu.addView(option);
            option.setOnClickListener(v -> {
                getPreferences(MODE_PRIVATE).edit().putString(PREF_GAME_MODE, mode).apply();
                anchor.setText(gameDriverLabel(mode));
                dismissPopup();
            });
        }

        showPopupMenu(anchor, menu, GAME_DRIVER_VALUES.length, 220, 92, -18);
    }

    private void showAiChargeLimitPicker(TextView anchor, TextView arrow) {
        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = popupMenu();

        for (String limit : AI_CHARGE_LIMITS) {
            TextView option = popupOption(aiChargeLimitLabel(limit));
            menu.addView(option);
            option.setOnClickListener(v -> {
                anchor.setText(aiChargeLimitLabel(limit));
                setAiChargeLimit(limit);
                dismissPopup();
            });
        }

        showPopupMenu(anchor, menu, AI_CHARGE_LIMITS.length, 180, 92, -6);
    }

    private void showResolutionPicker(TextView anchor, TextView arrow) {
        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = popupMenu();

        for (int i = 0; i < DISPLAY_RESOLUTION_VALUES.length; i++) {
            final String value = DISPLAY_RESOLUTION_VALUES[i];
            TextView option = popupOption(DISPLAY_RESOLUTION_LABELS[i]);
            menu.addView(option);
            option.setOnClickListener(v -> {
                dismissPopup();
                if ("custom".equals(value)) {
                    showCustomResolutionDialog(anchor);
                } else {
                    applyDisplayResolution(resolutionForWidth(value));
                    anchor.setText(currentDisplaySize());
                }
            });
        }

        showPopupMenu(anchor, menu, DISPLAY_RESOLUTION_VALUES.length, 180, 92, -6);
    }

    private void showRefreshPicker(TextView anchor, TextView arrow) {
        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = popupMenu();

        for (int i = 0; i < DISPLAY_REFRESH_VALUES.length; i++) {
            final String value = DISPLAY_REFRESH_VALUES[i];
            TextView option = popupOption(DISPLAY_REFRESH_LABELS[i]);
            menu.addView(option);
            option.setOnClickListener(v -> {
                dismissPopup();
                if ("custom".equals(value)) {
                    showCustomRefreshDialog(anchor);
                } else {
                    applyRefreshRate(value);
                    anchor.setText(refreshRateLabel(value));
                }
            });
        }

        showPopupMenu(anchor, menu, DISPLAY_REFRESH_VALUES.length, 180, 92, -6);
    }

    private void showGameFpsPicker(TextView anchor, TextView arrow) {
        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = popupMenu();

        for (int i = 0; i < GAME_FPS_VALUES.length; i++) {
            final String value = GAME_FPS_VALUES[i];
            TextView option = popupOption(GAME_FPS_LABELS[i]);
            menu.addView(option);
            option.setOnClickListener(v -> {
                dismissPopup();
                if ("custom".equals(value)) {
                    showCustomGameFpsDialog(anchor);
                } else {
                    applyGameFpsOverride(value);
                    anchor.setText(refreshRateLabel(value));
                }
            });
        }

        showPopupMenu(anchor, menu, GAME_FPS_VALUES.length, 180, 92, -6);
    }

    private void showGameAppPicker(TextView anchor, TextView arrow) {
        ArrayList<UserApp> apps = getUserLaunchableApps();
        if (apps.size() == 0) {
            toast("No user app detected");
            return;
        }

        dismissPopup();
        activeArrow = arrow;
        arrow.setText("v");

        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(dp(8), dp(4), dp(8), dp(4));
        menu.setBackground(round(Color.WHITE, dp(14), Color.rgb(185, 185, 185), 1));

        for (UserApp app : apps) {
            LinearLayout option = new LinearLayout(this);
            option.setOrientation(LinearLayout.VERTICAL);
            option.setGravity(Gravity.CENTER_VERTICAL);
            option.setPadding(0, 0, 0, 0);

            TextView title = new TextView(this);
            title.setText(app.label);
            title.setTextColor(Color.BLACK);
            title.setTextSize(13);
            title.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            title.setSingleLine(true);
            title.setIncludeFontPadding(false);
            option.addView(title);

            TextView pkg = new TextView(this);
            pkg.setText(app.packageName);
            pkg.setTextColor(SUMMARY);
            pkg.setTextSize(10);
            pkg.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            pkg.setSingleLine(true);
            pkg.setIncludeFontPadding(false);
            option.addView(pkg);

            menu.addView(option, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(42)));
            option.setOnClickListener(v -> {
                getPreferences(MODE_PRIVATE).edit()
                        .putString(PREF_GAME_PACKAGE, app.packageName)
                        .putString(PREF_GAME_LABEL, app.label)
                        .putString(PREF_GAME_MODE, currentGameDriverMode(app.packageName))
                        .apply();
                anchor.setText(app.label);
                if (gameDriverValueView != null) {
                    gameDriverValueView.setText(gameDriverLabel(selectedGameDriverMode()));
                }
                dismissPopup();
            });
        }

        ScrollView scroll = new ScrollView(this);
        scroll.addView(menu);
        int width = Math.max(dp(260), anchor.getWidth() + dp(128));
        int height = Math.min(dp(360), dp(8) + apps.size() * dp(42));
        activePopup = new PopupWindow(
                scroll,
                width,
                height,
                true);
        activePopup.setOutsideTouchable(true);
        activePopup.setBackgroundDrawable(round(Color.TRANSPARENT, dp(14), 0, 0));
        activePopup.setOnDismissListener(() -> {
            if (activeArrow != null) {
                activeArrow.setText(">");
            }
            activePopup = null;
            activeArrow = null;
        });
        activePopup.showAsDropDown(anchor, -dp(126), -dp(40));
    }

    private LinearLayout popupMenu() {
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(dp(10), dp(6), dp(10), dp(6));
        return menu;
    }

    private TextView popupOption(String text) {
        TextView option = new TextView(this);
        option.setText(text);
        option.setTextColor(Color.BLACK);
        option.setTextSize(13);
        option.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        option.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        option.setIncludeFontPadding(true);
        option.setSingleLine(false);
        option.setMaxLines(2);
        option.setLineSpacing(0, 1.0f);
        option.setPadding(0, dp(6), 0, dp(6));
        option.setMinHeight(dp(36));
        option.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return option;
    }

    private void showPopupMenu(TextView anchor, LinearLayout menu, int itemCount,
                               int minWidthDp, int extraWidthDp, int xOffsetDp) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setVerticalScrollBarEnabled(true);
        scroll.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        scroll.setBackground(round(Color.WHITE, dp(14), Color.rgb(185, 185, 185), 1));
        scroll.addView(menu, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int width = Math.max(dp(minWidthDp), anchor.getWidth() + dp(extraWidthDp));
        width = Math.min(width, screenWidth - dp(48));
        int maxHeight = Math.min(dp(420), screenHeight - dp(180));
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        menu.measure(widthSpec, heightSpec);
        int contentHeight = menu.getMeasuredHeight();
        int height = Math.max(dp(56), Math.min(contentHeight, maxHeight));

        activePopup = new PopupWindow(
                scroll,
                width,
                height,
                true);
        activePopup.setOutsideTouchable(true);
        activePopup.setBackgroundDrawable(round(Color.TRANSPARENT, dp(14), 0, 0));
        activePopup.setOnDismissListener(() -> {
            if (activeArrow != null) {
                activeArrow.setText(">");
            }
            activePopup = null;
            activeArrow = null;
        });
        activePopup.showAsDropDown(anchor, signedDp(xOffsetDp), -dp(40));
    }

    private int signedDp(int value) {
        return value < 0 ? -dp(-value) : dp(value);
    }

    private void dismissPopup() {
        if (activePopup != null && activePopup.isShowing()) {
            activePopup.dismiss();
        }
        activePopup = null;
        activeArrow = null;
    }

    private TextView titleText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tv.setIncludeFontPadding(false);
        tv.setPadding(0, 0, 0, dp(4));
        return tv;
    }

    private TextView summaryText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(SUMMARY);
        tv.setTextSize(13);
        tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tv.setLineSpacing(0, 0.98f);
        tv.setIncludeFontPadding(false);
        return tv;
    }

    private TextView arrowText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.rgb(120, 120, 120));
        tv.setTextSize(18);
        tv.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        tv.setGravity(Gravity.CENTER);
        tv.setIncludeFontPadding(false);
        return tv;
    }

    private GradientDrawable round(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(dp(strokeWidth), strokeColor);
        }
        return drawable;
    }

    private GradientDrawable oval(int color, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        if (strokeWidth > 0) {
            drawable.setStroke(dp(strokeWidth), strokeColor);
        }
        return drawable;
    }

    private void addGap(int height) {
        View gap = new View(this);
        root.addView(gap, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(height)));
    }

    private void setThermalDisabled(boolean disabled) {
        if (disabled) {
            RootShell.run("cmd thermalservice override-status 0 >/dev/null 2>&1; " +
                    "setprop debug.thermal.throttle.support no 2>/dev/null; " +
                    "setprop persist.vendor.thermal.goven 0 2>/dev/null; " +
                    "setprop persist.vendor.thermal.limen 0 2>/dev/null; " +
                    "for zone in /sys/class/thermal/thermal_zone*/mode; do " +
                    "[ -e \"$zone\" ] || continue; echo disabled > \"$zone\" 2>/dev/null; done; " +
                    "for cooling in /sys/class/thermal/cooling_device*/cur_state; do " +
                    "[ -e \"$cooling\" ] || continue; echo 0 > \"$cooling\" 2>/dev/null; done; " +
                    "for svc in $(getprop | grep 'init.svc.*therm' | cut -d '[' -f2 | cut -d ']' -f1 | sed 's/^init\\.svc\\.//'); do " +
                    "stop \"$svc\" 2>/dev/null; done; " +
                    "stop vendor.thermal-hal-2-0 2>/dev/null; stop vendor.thermald 2>/dev/null");
            toast("Thermal fully disabled");
        } else {
            RootShell.run("cmd thermalservice reset >/dev/null 2>&1; " +
                    "setprop debug.thermal.throttle.support yes 2>/dev/null; " +
                    "setprop persist.vendor.thermal.goven 1 2>/dev/null; " +
                    "setprop persist.vendor.thermal.limen 1 2>/dev/null; " +
                    "for zone in /sys/class/thermal/thermal_zone*/mode; do " +
                    "[ -e \"$zone\" ] || continue; echo enabled > \"$zone\" 2>/dev/null; done; " +
                    "for svc in $(getprop | grep 'init.svc.*therm' | cut -d '[' -f2 | cut -d ']' -f1 | sed 's/^init\\.svc\\.//'); do " +
                    "start \"$svc\" 2>/dev/null; done; " +
                    "start vendor.thermal-hal-2-0 2>/dev/null; start vendor.thermald 2>/dev/null");
            toast("Thermal enabled");
        }
    }

    private String thermalDisableSummary() {
        if (isThermalDisabled()) {
            return "Current: Disabled. Thermal services, zones, cooling states and runtime props are forced off.";
        }
        return "Current: Active. Stops thermal services, disables zones and forces thermal props off.";
    }

    private boolean isThermalDisabled() {
        String out = RootShell.run("running=$(getprop | grep 'init.svc.*therm' | grep -c 'running'); " +
                "mode=$(for zone in /sys/class/thermal/thermal_zone*/mode; do " +
                "[ -e \"$zone\" ] && cat \"$zone\" 2>/dev/null && break; done); " +
                "goven=$(getprop persist.vendor.thermal.goven); " +
                "limen=$(getprop persist.vendor.thermal.limen); " +
                "[ \"$running\" = 0 ] && [ \"$mode\" = disabled ] && [ \"$goven\" = 0 ] && [ \"$limen\" = 0 ] && echo 1 || echo 0");
        return "1".equals(firstLine(out));
    }

    private String forceMsaaSummary() {
        return isForceMsaaEnabled() ? "Current: Enabled" : "Current: Disabled";
    }

    private boolean isForceMsaaEnabled() {
        String value = firstLine(RootShell.run("getprop " + FORCE_MSAA_PROP));
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    private void setForceMsaa(boolean enabled) {
        RootShell.run(setPropCommand(FORCE_MSAA_PROP, enabled ? "1" : "0") + "; " +
                forceMsaaBootScriptCommand(enabled));
        getPreferences(MODE_PRIVATE).edit().putBoolean(PREF_FORCE_MSAA, enabled).apply();
        toast(enabled ? "Force 4x MSAA enabled" : "Force 4x MSAA disabled");
    }

    private String forceMsaaBootScriptCommand(boolean enabled) {
        if (!enabled) {
            return "rm -f " + FORCE_MSAA_SCRIPT;
        }
        return "mkdir -p /data/adb/service.d; " +
                "printf '%s\\n' '#!/system/bin/sh' 'sleep 3' " +
                shellSingleQuote(setPropCommand(FORCE_MSAA_PROP, "1")) +
                " > " + FORCE_MSAA_SCRIPT + "; chmod 755 " + FORCE_MSAA_SCRIPT;
    }

    private void applyRenderer(String renderer) {
        RootShell.run(rendererCommand(renderer) + "; " + rendererBootScriptCommand(renderer));
        if (rendererStatusView != null) {
            rendererStatusView.setText(rendererLabel(currentRenderer()));
        }
        toast("Renderer saved");
    }

    private void rebootDevice() {
        toast("Rebooting");
        RootShell.run("reboot");
    }

    private String rendererCommand(String renderer) {
        if ("default".equals(renderer)) {
            return deletePropCommand(HWUI_RENDERER_PROP) + "; " +
                    deletePropCommand(RENDERENGINE_BACKEND_PROP);
        }
        return setPropCommand(HWUI_RENDERER_PROP, hwuiRendererValue(renderer)) + "; " +
                setPropCommand(RENDERENGINE_BACKEND_PROP, renderEngineBackendValue(renderer));
    }

    private String rendererBootScriptCommand(String renderer) {
        String script = "/data/adb/service.d/transsionaddons_renderer_once.sh";
        String apply = rendererCommand(renderer)
                .replace("\\", "\\\\")
                .replace("'", "'\"'\"'");
        return "mkdir -p /data/adb/service.d; " +
                "printf '%s\\n' '#!/system/bin/sh' " +
                "'rm -f " + script + "' " +
                "'sleep 3' " +
                "'" + apply + "' > " + script + "; " +
                "chmod 755 " + script;
    }

    private String currentRenderer() {
        String renderEngine = RootShell.run("getprop " + RENDERENGINE_BACKEND_PROP).trim();
        String renderer = normalizeRenderer(renderEngine);
        if (!"default".equals(renderer)) {
            return renderer;
        }

        String hwui = RootShell.run("getprop " + HWUI_RENDERER_PROP).trim();
        renderer = normalizeRenderer(hwui);
        if (!"default".equals(renderer)) {
            return renderer;
        }
        return "default";
    }

    private String normalizeRenderer(String renderer) {
        if (renderer == null || renderer.length() == 0 || "none".equals(renderer) || renderer.startsWith("ERROR:")) {
            return "default";
        }
        if ("gles".equals(renderer)) {
            return "opengl";
        }
        for (String value : RENDERER_VALUES) {
            if (value.equals(renderer)) {
                return renderer;
            }
        }
        return renderer;
    }

    private String hwuiRendererValue(String renderer) {
        if ("skiavk".equals(renderer) || "skiavkthreaded".equals(renderer)) {
            return "skiavk";
        }
        if ("skiagl".equals(renderer) || "skiaglthreaded".equals(renderer)) {
            return "skiagl";
        }
        return "opengl";
    }

    private String renderEngineBackendValue(String renderer) {
        if ("opengl".equals(renderer)) {
            return "gles";
        }
        return renderer;
    }

    private void setAngleDeveloperOption(boolean enabled) {
        String value = enabled ? "true" : "false";
        RootShell.run(setPropCommand(ANGLE_SUPPORTED_PROP, value) + "; " +
                setPropCommand(ANGLE_DEVELOPER_PROP, value) + "; " +
                angleBootScriptCommand(enabled));
        toast(enabled ? "ANGLE option enabled" : "ANGLE option disabled");
    }

    private boolean angleDeveloperOptionEnabled() {
        return "true".equals(RootShell.run("getprop " + ANGLE_DEVELOPER_PROP).trim()) ||
                "true".equals(RootShell.run("getprop " + ANGLE_SUPPORTED_PROP).trim());
    }

    private String angleBootScriptCommand(boolean enabled) {
        String script = "/data/adb/service.d/transsionaddons_angle.sh";
        if (!enabled) {
            return "rm -f " + script;
        }
        String apply = (setPropCommand(ANGLE_SUPPORTED_PROP, "true") + "; " +
                setPropCommand(ANGLE_DEVELOPER_PROP, "true"))
                .replace("\\", "\\\\")
                .replace("'", "'\"'\"'");
        return "mkdir -p /data/adb/service.d; " +
                "printf '%s\\n' '#!/system/bin/sh' " +
                "'sleep 3' " +
                "'" + apply + "' > " + script + "; " +
                "chmod 755 " + script;
    }

    private String setPropCommand(String prop, String value) {
        return "if command -v resetprop >/dev/null 2>&1 && resetprop " + prop + " " + value + " 2>/dev/null; then :; " +
                "elif [ -x /data/adb/ksu/bin/resetprop ] && /data/adb/ksu/bin/resetprop " + prop + " " + value + " 2>/dev/null; then :; " +
                "else setprop " + prop + " " + value + "; fi";
    }

    private String deletePropCommand(String prop) {
        return "if command -v resetprop >/dev/null 2>&1 && resetprop -d " + prop + " 2>/dev/null; then :; " +
                "elif [ -x /data/adb/ksu/bin/resetprop ] && /data/adb/ksu/bin/resetprop -d " + prop + " 2>/dev/null; then :; " +
                "else setprop " + prop + " ''; fi";
    }

    private String rendererLabel(String renderer) {
        for (int i = 0; i < RENDERER_VALUES.length; i++) {
            if (RENDERER_VALUES[i].equals(renderer)) {
                return RENDERER_LABELS[i];
            }
        }
        return renderer == null || renderer.length() == 0 ? "Default" : renderer;
    }

    private String selectedGamePackage() {
        return getPreferences(MODE_PRIVATE).getString(PREF_GAME_PACKAGE, "").trim();
    }

    private String selectedGameLabel() {
        String label = getPreferences(MODE_PRIVATE).getString(PREF_GAME_LABEL, "").trim();
        String pkg = selectedGamePackage();
        if (pkg.length() == 0) {
            return "Select app";
        }
        if (label.length() > 0) {
            return label;
        }
        return pkg;
    }

    private String selectedGameDriverMode() {
        String mode = getPreferences(MODE_PRIVATE).getString(PREF_GAME_MODE, DRIVER_DEFAULT);
        for (String value : GAME_DRIVER_VALUES) {
            if (value.equals(mode)) {
                return mode;
            }
        }
        return DRIVER_DEFAULT;
    }

    private String gameDriverLabel(String mode) {
        for (int i = 0; i < GAME_DRIVER_VALUES.length; i++) {
            if (GAME_DRIVER_VALUES[i].equals(mode)) {
                return GAME_DRIVER_LABELS[i];
            }
        }
        return GAME_DRIVER_LABELS[0];
    }

    private String currentGameDriverMode(String packageName) {
        if (containsGlobalPackage(DRIVER_PRERELEASE_OPT_IN, packageName)) {
            return DRIVER_DEVELOPER;
        }
        if (containsGlobalPackage(DRIVER_PRODUCTION_OPT_OUT, packageName)) {
            return DRIVER_SYSTEM;
        }
        if (containsGlobalPackage(DRIVER_PRODUCTION_OPT_IN, packageName) ||
                containsGlobalPackage(GAME_DRIVER_OPT_IN, packageName)) {
            return DRIVER_GAME;
        }
        return DRIVER_DEFAULT;
    }

    private void applyGameDriverPreference() {
        String packageName = selectedGamePackage();
        if (!isValidPackageName(packageName)) {
            toast("Select app first");
            return;
        }

        String mode = selectedGameDriverMode();
        setPackageInGlobalList(GAME_DRIVER_OPT_IN, packageName, DRIVER_GAME.equals(mode));
        setPackageInGlobalList(DRIVER_PRODUCTION_OPT_IN, packageName, DRIVER_GAME.equals(mode));
        setPackageInGlobalList(DRIVER_PRODUCTION_OPT_OUT, packageName, DRIVER_SYSTEM.equals(mode));
        setPackageInGlobalList(DRIVER_PRERELEASE_OPT_IN, packageName, DRIVER_DEVELOPER.equals(mode));
        toast(gameDriverLabel(mode) + " applied");
    }

    private void runSelectedGameApp() {
        String packageName = selectedGamePackage();
        if (!isValidPackageName(packageName)) {
            toast("Select app first");
            return;
        }

        Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch == null) {
            toast("App cannot be opened");
            return;
        }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launch);
    }

    private void openGameSpace() {
        Intent intent = new Intent();
        intent.setClassName(GAME_SPACE_PACKAGE, GAME_SPACE_ACTIVITY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Intent launch = getPackageManager().getLaunchIntentForPackage(GAME_SPACE_PACKAGE);
            if (launch == null) {
                toast("Game Space not detected");
                return;
            }
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
        }
    }

    private void openSmartPanelPqe() {
        Intent intent = new Intent(SMART_PANEL_PQE_ACTION);
        intent.setClassName(SMART_PANEL_PACKAGE, SMART_PANEL_PQE_ACTIVITY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            RootShell.run("am start -n " + SMART_PANEL_PACKAGE + "/" + SMART_PANEL_PQE_ACTIVITY + " >/dev/null 2>&1");
            toast("Stock PQ panel requested");
        }
    }

    private void openFrameInterpolationPanel(int type) {
        int cleanType = type == 1 ? 1 : 0;
        String component = SMART_PANEL_PACKAGE + "/" + SMART_PANEL_FRAME_ACTIVITY;
        RootShell.run("pm enable " + component + " >/dev/null 2>&1");

        Intent intent = new Intent();
        intent.setClassName(SMART_PANEL_PACKAGE, SMART_PANEL_FRAME_ACTIVITY);
        intent.putExtra("type", cleanType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            RootShell.run("am start -n " + component + " --ei type " + cleanType + " >/dev/null 2>&1");
            toast("Hidden panel requested");
        }
    }

    private String smartPanelActionStatus(String action) {
        return SMART_PANEL_PQE_ACTION.equals(action)
                ? packageActivityStatus(SMART_PANEL_PQE_ACTIVITY)
                : "Not detected";
    }

    private String smartPanelComponentStatus(String activity) {
        if (activity == null || !activity.matches("[a-zA-Z0-9_\\.]+")) {
            return "Not detected";
        }
        return packageActivityStatus(activity);
    }

    private String packageActivityStatus(String activity) {
        try {
            int flags = PackageManager.MATCH_DISABLED_COMPONENTS;
            if (Build.VERSION.SDK_INT >= 24) {
                flags |= PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS;
            }
            android.content.pm.ActivityInfo info = getPackageManager().getActivityInfo(
                    new ComponentName(SMART_PANEL_PACKAGE, activity),
                    flags);
            return info.enabled ? "Available" : "Hidden by ROM";
        } catch (Exception ignored) {
            return "Not detected";
        }
    }

    private String gameSpaceVersion() {
        try {
            String version = getPackageManager().getPackageInfo(GAME_SPACE_PACKAGE, 0).versionName;
            return version == null || version.length() == 0 ? "Installed" : version;
        } catch (Exception ignored) {
            return "Not detected";
        }
    }

    private String smartPanelWhitelistStatus() {
        String packageName = selectedGamePackage();
        if (!isValidPackageName(packageName)) {
            return "Select app first";
        }
        String packages = getPreferences(MODE_PRIVATE).getString(PREF_SMART_PANEL_WHITELIST_PACKAGES, "");
        if (csvContainsPackage(packages, packageName)) {
            return "Forced for app";
        }
        String status = getPreferences(MODE_PRIVATE).getString(PREF_SMART_PANEL_WHITELIST_STATUS, "").trim();
        if (status.length() > 0) {
            return status;
        }
        return "Not checked";
    }

    private String gameSpaceProviderStatus(String packageName) {
        if (!isValidPackageName(packageName)) {
            return "Select app first";
        }
        String packages = getPreferences(MODE_PRIVATE).getString(PREF_GAME_SPACE_PROVIDER_PACKAGES, "");
        if (csvContainsPackage(packages, packageName)) {
            return "Enabled";
        }
        return "Not checked";
    }

    private void forceSelectedGameSupport() {
        String packageName = selectedGamePackage();
        if (!isValidPackageName(packageName)) {
            toast("Select app first");
            return;
        }

        toast("Forcing GameSpace support...");
        new Thread(() -> {
            LinkedHashSet<String> packages = collectSmartPanelSupportPackages(packageName);
            boolean whitelistOk = writeSmartPanelFeatureWhitelist(packages);
            boolean providerOk = false;
            for (String item : packages) {
                boolean added = addGameSpaceProviderApp(item);
                if (packageName.equals(item)) {
                    providerOk = added;
                }
                setPackageInGlobalList(GAME_DRIVER_OPT_IN, item, true);
            }
            forceGameSpaceCompatibilitySettings();

            String joined = joinPackages(packages);
            getPreferences(MODE_PRIVATE).edit()
                    .putString(PREF_SMART_PANEL_WHITELIST_PACKAGES, whitelistOk ? joined : "")
                    .putString(PREF_SMART_PANEL_WHITELIST_STATUS, whitelistOk ? "Forced for app" : "Write failed")
                    .putString(PREF_GAME_SPACE_PROVIDER_PACKAGES, providerOk ? joined : "")
                    .apply();

            runOnUiThread(() -> {
                toast(whitelistOk ? "GameSpace support forced" : "SmartPanel whitelist write failed");
                if ("game_space".equals(String.valueOf(root.getTag()))) {
                    showGameSpacePage();
                }
            });
        }).start();
    }

    private LinkedHashSet<String> collectSmartPanelSupportPackages(String selectedPackage) {
        LinkedHashSet<String> packages = new LinkedHashSet<>();
        if (isValidPackageName(selectedPackage)) {
            packages.add(selectedPackage);
        }
        packages.addAll(readGlobalPackageList(GAME_DRIVER_OPT_IN));
        addPackagesFromCsv(packages, getPreferences(MODE_PRIVATE).getString(PREF_SMART_PANEL_WHITELIST_PACKAGES, ""));
        for (String packageName : KNOWN_GAME_SUPPORT_PACKAGES) {
            if (isPackageInstalled(packageName)) {
                packages.add(packageName);
            }
        }
        return packages;
    }

    private boolean writeSmartPanelFeatureWhitelist(LinkedHashSet<String> packages) {
        if (packages == null || packages.size() == 0) {
            return false;
        }
        String xml = buildSmartPanelFeatureXml(packages);
        String encoded = Base64.encodeToString(xml.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        String directory = SMART_PANEL_FEATURE_PREF.substring(0, SMART_PANEL_FEATURE_PREF.lastIndexOf('/'));
        String inner = "mkdir -p " + shellSingleQuote(directory) + "; " +
                "rm " + shellSingleQuote(SMART_PANEL_FEATURE_PREF) + " 2>/dev/null; " +
                "printf %s " + shellSingleQuote(encoded) + " | base64 -d > " +
                shellSingleQuote(SMART_PANEL_FEATURE_PREF) + "; " +
                "chmod 0644 " + shellSingleQuote(SMART_PANEL_FEATURE_PREF) + " 2>/dev/null; " +
                "[ -s " + shellSingleQuote(SMART_PANEL_FEATURE_PREF) + " ] && echo TA_OK || echo TA_FAILED";
        String out = runSmartPanelDataCommand(inner);
        RootShell.run("am force-stop " + SMART_PANEL_PACKAGE + " >/dev/null 2>&1");
        return out.contains("TA_OK");
    }

    private String buildSmartPanelFeatureXml(LinkedHashSet<String> packages) {
        String json = smartPanelPackageJson(packages);
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n");
        builder.append("<map>\n");
        for (String key : SMART_PANEL_SUPPORT_KEYS) {
            builder.append("    <string name=\"")
                    .append(key)
                    .append("\">")
                    .append(json)
                    .append("</string>\n");
        }
        builder.append("</map>\n");
        return builder.toString();
    }

    private String smartPanelPackageJson(LinkedHashSet<String> packages) {
        StringBuilder builder = new StringBuilder("[");
        for (String packageName : packages) {
            if (!isValidPackageName(packageName)) {
                continue;
            }
            if (builder.length() > 1) {
                builder.append(',');
            }
            builder.append("{\"packageName\":\"")
                    .append(packageName)
                    .append("\"}");
        }
        builder.append(']');
        return builder.toString();
    }

    private String readSmartPanelFeatureXml() {
        String inner = "cat " + shellSingleQuote(SMART_PANEL_FEATURE_PREF) + " 2>/dev/null";
        String out = runSmartPanelDataCommand(inner);
        return isBadShellOutput(out) ? "" : out;
    }

    private String runSmartPanelDataCommand(String inner) {
        String quoted = shellSingleQuote(inner);
        String out = RootShell.run("runcon u:r:system_app:s0 sh -c " + quoted);
        if (!looksBlockedShellOutput(out)) {
            return out;
        }
        out = RootShell.run("runcon u:r:system_app:s0 su 1000 -c " + quoted);
        return out;
    }

    private boolean looksBlockedShellOutput(String out) {
        if (out == null || out.length() == 0) {
            return false;
        }
        String lower = out.toLowerCase();
        return lower.contains("permission denied") ||
                lower.contains("operation not permitted") ||
                lower.contains("inaccessible") ||
                lower.contains("invalid context") ||
                lower.contains("could not set context") ||
                lower.startsWith("error:");
    }

    private boolean csvContainsPackage(String packages, String packageName) {
        if (!isValidPackageName(packageName) || packages == null || packages.length() == 0) {
            return false;
        }
        String[] entries = packages.split(",");
        for (String entry : entries) {
            if (packageName.equals(entry.trim())) {
                return true;
            }
        }
        return false;
    }

    private void addPackagesFromCsv(LinkedHashSet<String> packages, String text) {
        if (text == null || text.length() == 0) {
            return;
        }
        String[] entries = text.split(",");
        for (String entry : entries) {
            String packageName = entry.trim();
            if (isValidPackageName(packageName)) {
                packages.add(packageName);
            }
        }
    }

    private void addPackagesFromText(LinkedHashSet<String> packages, String text) {
        if (text == null || text.length() == 0) {
            return;
        }
        addPackagesFromMarker(packages, text, "\"packageName\":\"", "\"");
        addPackagesFromMarker(packages, text, "packageName&quot;:&quot;", "&quot;");
    }

    private void addPackagesFromMarker(LinkedHashSet<String> packages, String text, String marker, String terminator) {
        int index = 0;
        while ((index = text.indexOf(marker, index)) >= 0) {
            int start = index + marker.length();
            int end = text.indexOf(terminator, start);
            if (end <= start) {
                break;
            }
            String packageName = text.substring(start, end);
            if (isValidPackageName(packageName)) {
                packages.add(packageName);
            }
            index = end + terminator.length();
        }
    }

    private boolean addGameSpaceProviderApp(String packageName) {
        if (!isValidPackageName(packageName)) {
            return false;
        }
        String command = "content insert --uri " + GAME_SPACE_PROVIDER_URI +
                " --bind packagename:s:" + shellSingleQuote(packageName) +
                " --bind classname:s: --bind ischeck:s:true >/dev/null 2>&1; " +
                "content query --uri " + GAME_SPACE_PROVIDER_URI +
                " --projection packagename:ischeck 2>/dev/null | grep -F " +
                shellSingleQuote("packagename=" + packageName) + " | head -n 1";
        return RootShell.run(command).contains(packageName);
    }

    private void forceGameSpaceCompatibilitySettings() {
        RootShell.run("settings put global game_white_list_state 1; " +
                "settings put global os_game_assistant_panel 1; " +
                "settings put global transsion_game_mode 1; " +
                "settings put global transsion_game_picture_optimization 1; " +
                "settings put system transsion_game_picture_optimization 1; " +
                "settings put global pqe_mode_values 2; " +
                "settings put system pqe_mode_values 2; " +
                "settings put global game_space_pace 1; " +
                "settings put global game_sound_effects 1; " +
                "settings put global game_space_resurrection_status 1; " +
                "settings put system settings_game_enhance_switch 1; " +
                "settings put system settings_game_enhance_strength 3; " +
                "settings put system settings_game_sdr2hdr_switch 1; " +
                "settings put system settings_game_sdr2hdr_strength 3; " +
                "settings put global va_hdr_mode 1; " +
                "settings put system va_hdr_mode 1; " +
                setPropCommand("ro.os_game_enhancement_support", "1") + "; " +
                setPropCommand("ro.transsion.frame_override.support", "1") + "; " +
                setPropCommand("ro.tran.display.game.sdr2hdr.support", "1") + "; " +
                setPropCommand("ro.tran.display.sdr2hdr.support", "1"));
    }

    private void forceGameSpaceRuntimeEffects() {
        String packageName = selectedGamePackage();
        if (!isValidPackageName(packageName)) {
            toast("Select app first");
            return;
        }

        String mode = normalizeGameSettingValue(gameSettingValue("pqe_mode_values"));
        if (!mode.matches("\\d+")) {
            mode = "2";
        }
        final String effectMode = mode;

        toast("Forcing runtime effects...");
        new Thread(() -> {
            setPackageInGlobalList(GAME_DRIVER_OPT_IN, packageName, true);
            RootShell.run("settings put global game_white_list_state 1; " +
                    "settings put global os_game_assistant_panel 1; " +
                    "settings put global transsion_game_mode 1; " +
                    "settings put global transsion_game_acceleration 1; " +
                    "settings put global transsion_game_picture_optimization 1; " +
                    "settings put system transsion_game_picture_optimization 1; " +
                    "settings put global pqe_mode_values " + effectMode + "; " +
                    "settings put system pqe_mode_values " + effectMode + "; " +
                    "settings put global game_space_pace 1; " +
                    "settings put global game_sound_effects 1; " +
                    "settings put global game_space_resurrection_status 1; " +
                    "settings put global va_hdr_mode 1; " +
                    "settings put system va_hdr_mode 1; " +
                    "settings put system settings_game_enhance_switch 1; " +
                    "settings put system settings_game_enhance_strength 3; " +
                    "settings put system settings_game_sdr2hdr_switch 1; " +
                    "settings put system settings_game_sdr2hdr_strength 3; " +
                    "[ -e /proc/main_game_state ] && echo 1 > /proc/main_game_state 2>/dev/null; " +
                    "[ -e /proc/game_state ] && echo 1 > /proc/game_state 2>/dev/null; " +
                    setPropCommand("ro.os_game_enhancement_support", "1") + "; " +
                    setPropCommand("ro.tran.display.game.sdr2hdr.support", "1") + "; " +
                    setPropCommand("ro.tran.display.sdr2hdr.support", "1"));

            runOnUiThread(() -> {
                toast("Runtime effects forced");
                if ("game_space".equals(String.valueOf(root.getTag()))) {
                    showGameSpacePage();
                }
            });
        }).start();
    }

    private void clearSmartPanelWhitelist() {
        toast("Clearing SmartPanel list...");
        new Thread(() -> {
            String inner = "rm " + shellSingleQuote(SMART_PANEL_FEATURE_PREF) + " 2>/dev/null";
            runSmartPanelDataCommand(inner);
            RootShell.run("am force-stop " + SMART_PANEL_PACKAGE + " >/dev/null 2>&1");
            getPreferences(MODE_PRIVATE).edit()
                    .remove(PREF_SMART_PANEL_WHITELIST_PACKAGES)
                    .remove(PREF_SMART_PANEL_WHITELIST_STATUS)
                    .apply();
            runOnUiThread(() -> {
                toast("SmartPanel local list cleared");
                if ("game_space".equals(String.valueOf(root.getTag()))) {
                    showGameSpacePage();
                }
            });
        }).start();
    }

    private boolean isPackageInstalled(String packageName) {
        if (!isValidPackageName(packageName)) {
            return false;
        }
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private ArrayList<String> gamePerformanceModeOptions() {
        return new ArrayList<>(Arrays.asList(GAME_MODE_LABELS));
    }

    private String gamePerformanceModeLabel(String value) {
        String clean = normalizeGameSettingValue(value);
        for (int i = 0; i < GAME_MODE_VALUES.length; i++) {
            if (GAME_MODE_VALUES[i].equals(clean)) {
                return GAME_MODE_LABELS[i];
            }
        }
        return "Not detected".equals(value) ? value : clean;
    }

    private void applyGamePerformanceMode(String label) {
        for (int i = 0; i < GAME_MODE_LABELS.length; i++) {
            if (GAME_MODE_LABELS[i].equals(label)) {
                applyGameSettingValue("settings_game_performance_mode", GAME_MODE_VALUES[i], "Game Performance Mode");
                return;
            }
        }
        toast("Game Performance Mode invalid");
    }

    private ArrayList<String> pqeModeOptions() {
        ArrayList<String> options = new ArrayList<>();
        int count = pqeModeCount();
        for (int i = 0; i < PQE_MODE_LABELS.length && i < count; i++) {
            options.add(PQE_MODE_LABELS[i]);
        }
        return options;
    }

    private String pqeModeLabel(String value) {
        String clean = normalizeGameSettingValue(value);
        int count = pqeModeCount();
        for (int i = 0; i < PQE_MODE_VALUES.length && i < count; i++) {
            if (PQE_MODE_VALUES[i].equals(clean)) {
                return PQE_MODE_LABELS[i];
            }
        }
        if (clean.matches("\\d+")) {
            return "Invalid: " + clean;
        }
        return "Not detected".equals(value) ? value : clean;
    }

    private void applyPqeMode(String label) {
        int count = pqeModeCount();
        for (int i = 0; i < PQE_MODE_LABELS.length && i < count; i++) {
            if (PQE_MODE_LABELS[i].equals(label)) {
                applyGameSettingValue("pqe_mode_values", PQE_MODE_VALUES[i], "Enhancement Mode");
                return;
            }
        }
        toast("Enhancement Mode invalid");
    }

    private int pqeModeCount() {
        String screenMode = readSettingValue("global", "screen_mode_type");
        return screenMode.length() == 0 || "0".equals(screenMode) || "Not detected".equals(screenMode) ||
                "null".equals(screenMode) ? 3 : 4;
    }

    private String gameSettingValue(String key) {
        String cleanKey = safeSettingKey(key);
        if (cleanKey.length() == 0) {
            return "Not detected";
        }
        String preferredNs = gameSettingPreferredNamespace(cleanKey);
        if (preferredNs.length() > 0) {
            String value = readSettingValue(preferredNs, cleanKey);
            if (value.length() > 0) {
                return normalizeGameSettingValue(value);
            }
            return normalizeGameSettingValue(firstOutputLine(RootShell.run(
                    "settings get " + preferredNs + " " + cleanKey + " 2>/dev/null")));
        }
        for (String ns : new String[]{"system", "global", "secure"}) {
            String value = readSettingValue(ns, cleanKey);
            if (value.length() > 0) {
                return normalizeGameSettingValue(value);
            }
        }
        String out = RootShell.run("for ns in system global secure; do " +
                "v=$(settings get \"$ns\" " + cleanKey + " 2>/dev/null); " +
                "[ -n \"$v\" ] && [ \"$v\" != null ] && echo \"$v\" && exit 0; done");
        String value = firstOutputLine(out);
        return normalizeGameSettingValue(value);
    }

    private String gameSettingNamespace(String key) {
        String cleanKey = safeSettingKey(key);
        if (cleanKey.length() == 0) {
            return "system";
        }
        String preferredNs = gameSettingPreferredNamespace(cleanKey);
        if (preferredNs.length() > 0) {
            return preferredNs;
        }
        for (String ns : new String[]{"system", "global", "secure"}) {
            if (readSettingValue(ns, cleanKey).length() > 0) {
                return ns;
            }
        }
        String out = RootShell.run("for ns in system global secure; do " +
                "v=$(settings get \"$ns\" " + cleanKey + " 2>/dev/null); " +
                "[ -n \"$v\" ] && [ \"$v\" != null ] && echo \"$ns\" && exit 0; done");
        String ns = firstOutputLine(out);
        return ("system".equals(ns) || "global".equals(ns) || "secure".equals(ns)) ? ns : "system";
    }

    private String readSettingValue(String namespace, String key) {
        try {
            String value;
            if ("global".equals(namespace)) {
                value = Settings.Global.getString(getContentResolver(), key);
            } else if ("secure".equals(namespace)) {
                value = Settings.Secure.getString(getContentResolver(), key);
            } else {
                value = Settings.System.getString(getContentResolver(), key);
            }
            return value == null || "null".equals(value) ? "" : value.trim();
        } catch (Exception ignored) {
            return "";
        }
    }

    private String gameSettingPreferredNamespace(String key) {
        if ("pqe_mode_values".equals(key) ||
                "transsion_game_picture_optimization".equals(key) ||
                "transsion_game_acceleration".equals(key) ||
                "game_acc_state".equals(key) ||
                "game_4d_vibration".equals(key) ||
                "transsion_game_changer_type".equals(key) ||
                "transsion_game_mode".equals(key) ||
                "transsion_game_mode_not_interrupt".equals(key) ||
                "transsion_game_mode_refuse_call".equals(key) ||
                "show_acc_hot_dot".equals(key) ||
                "game_space_health".equals(key) ||
                "game_space_pace".equals(key) ||
                "game_sound_effects".equals(key) ||
                "game_space_resurrection_status".equals(key) ||
                "smart_panel_status".equals(key) ||
                "game_mode_game_env".equals(key)) {
            return "global";
        }
        return "";
    }

    private String normalizeGameSettingValue(String value) {
        if (value == null || value.length() == 0 || "Not detected".equals(value) || "null".equals(value)) {
            return "Not detected";
        }
        return value.trim();
    }

    private String gameSettingSwitchSummary(String key) {
        String value = gameSettingValue(key);
        if ("1".equals(value) || "true".equalsIgnoreCase(value)) {
            return "Current: Enabled";
        }
        if ("0".equals(value) || "false".equalsIgnoreCase(value)) {
            return "Current: Disabled";
        }
        if ("transsion_game_picture_optimization".equals(key) && "Not detected".equals(value)) {
            return "Current: Disabled";
        }
        return "Not detected".equals(value) ? value : "Current: " + value;
    }

    private boolean gameSettingSwitchEnabled(String key) {
        String value = gameSettingValue(key);
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    private void applyGameSettingSwitch(String key, boolean enabled, String label) {
        applyGameSettingValue(key, enabled ? "1" : "0", label);
    }

    private void applyGameSettingValue(String key, String value, String label) {
        String cleanKey = safeSettingKey(key);
        String cleanValue = safeSettingValue(value);
        if (cleanKey.length() == 0 || cleanValue.length() == 0) {
            toast(label + " invalid");
            return;
        }
        String ns = gameSettingNamespace(cleanKey);
        String command = "settings put " + ns + " " + cleanKey + " " + cleanValue;
        if ("global".equals(ns) && shouldMirrorGameSettingToSystem(cleanKey)) {
            command += "; settings put system " + cleanKey + " " + cleanValue;
        }
        RootShell.run(command);
        toast(label + " applied");
    }

    private boolean shouldMirrorGameSettingToSystem(String key) {
        return "pqe_mode_values".equals(key) || "transsion_game_picture_optimization".equals(key);
    }

    private String safeSettingKey(String key) {
        if (key == null) {
            return "";
        }
        return key.matches("[a-zA-Z0-9_\\.\\-]+") ? key : "";
    }

    private String safeSettingValue(String value) {
        if (value == null) {
            return "";
        }
        return value.matches("[a-zA-Z0-9_\\.\\-]+") ? value : "";
    }

    private ArrayList<UserApp> getUserLaunchableApps() {
        ArrayList<UserApp> apps = new ArrayList<>();
        PackageManager pm = getPackageManager();
        try {
            List<ApplicationInfo> installed = pm.getInstalledApplications(0);
            for (ApplicationInfo info : installed) {
                if (info == null || info.packageName == null) {
                    continue;
                }
                if (getPackageName().equals(info.packageName)) {
                    continue;
                }
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
                        (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    continue;
                }
                if (pm.getLaunchIntentForPackage(info.packageName) == null) {
                    continue;
                }
                CharSequence label = info.loadLabel(pm);
                apps.add(new UserApp(
                        label == null ? info.packageName : label.toString(),
                        info.packageName));
            }
        } catch (Exception ignored) {
        }
        Collections.sort(apps, (left, right) -> left.label.compareToIgnoreCase(right.label));
        return apps;
    }

    private boolean containsGlobalPackage(String key, String packageName) {
        return readGlobalPackageList(key).contains(packageName);
    }

    private LinkedHashSet<String> readGlobalPackageList(String key) {
        String value = RootShell.run("settings get global " + key).trim();
        LinkedHashSet<String> packages = new LinkedHashSet<>();
        if (value.length() == 0 || "null".equals(value) || value.startsWith("ERROR:")) {
            return packages;
        }
        String[] entries = value.split(",");
        for (String entry : entries) {
            String item = entry.trim();
            if (isValidPackageName(item)) {
                packages.add(item);
            }
        }
        return packages;
    }

    private void setPackageInGlobalList(String key, String packageName, boolean include) {
        LinkedHashSet<String> packages = readGlobalPackageList(key);
        if (include) {
            packages.add(packageName);
        } else {
            packages.remove(packageName);
        }
        if (packages.size() == 0) {
            RootShell.run("settings delete global " + key);
            return;
        }
        RootShell.run("settings put global " + key + " " + joinPackages(packages));
    }

    private String joinPackages(LinkedHashSet<String> packages) {
        StringBuilder builder = new StringBuilder();
        for (String packageName : packages) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(packageName);
        }
        return builder.toString();
    }

    private boolean isValidPackageName(String packageName) {
        return packageName != null && packageName.matches("[A-Za-z0-9_\\.]+");
    }

    private StorageHealth readStorageHealth() {
        String ufsA = normalizeHealthHex(readStorageNodeRaw(UFS_LIFE_A_NODES), 0);
        String ufsB = normalizeHealthHex(readStorageNodeRaw(UFS_LIFE_B_NODES), 0);
        if (ufsA.length() > 0 || ufsB.length() > 0) {
            return new StorageHealth("UFS", ufsA, ufsB);
        }

        String emmcARaw = readStorageNodeRaw(EMMC_LIFE_A_NODES);
        String emmcBRaw = readStorageNodeRaw(EMMC_LIFE_B_NODES);
        String emmcA = normalizeHealthHex(emmcARaw, 0);
        String emmcB = normalizeHealthHex(emmcBRaw, 0);
        if (emmcB.length() == 0) {
            emmcB = normalizeHealthHex(emmcARaw, 1);
        }
        if (emmcA.length() > 0 || emmcB.length() > 0) {
            return new StorageHealth("eMMC", emmcA, emmcB);
        }

        return new StorageHealth("Not detected", "", "");
    }

    private void refreshStorageHealth() {
        StorageHealth health = readStorageHealth();
        if (storageTypeView != null) {
            storageTypeView.setText(health.type);
        }
        if (storageLifeAView != null) {
            storageLifeAView.setText(storageHealthDisplay(health.lifeA));
        }
        if (storageLifeBView != null) {
            storageLifeBView.setText(storageHealthDisplay(health.lifeB));
        }
        toast("Storage health refreshed");
    }

    private String readStorageNodeRaw(String nodes) {
        String output = RootShell.run("for f in " + nodes + "; do " +
                "[ -e \"$f\" ] || continue; cat \"$f\" 2>/dev/null && exit 0; done");
        if (isBadShellOutput(output)) {
            return "";
        }
        return output.trim();
    }

    private String normalizeHealthHex(String raw, int targetIndex) {
        if (isBadShellOutput(raw)) {
            return "";
        }
        String[] tokens = raw.replace(',', ' ').replace(':', ' ').trim().split("\\s+");
        int found = 0;
        for (String token : tokens) {
            String item = token.trim();
            if (item.length() == 0) {
                continue;
            }
            if (item.startsWith("0x") || item.startsWith("0X")) {
                item = item.substring(2);
            }
            if (!item.matches("[0-9a-fA-F]{1,2}")) {
                continue;
            }
            if (found == targetIndex) {
                try {
                    int value = Integer.parseInt(item, 16);
                    return String.format("0x%02X", value);
                } catch (NumberFormatException ignored) {
                    return "";
                }
            }
            found++;
        }
        return "";
    }

    private String storageHealthDisplay(String hex) {
        if (hex == null || hex.length() == 0) {
            return "Not detected";
        }
        return hex + " (" + storageHealthPercent(hex) + ")";
    }

    private String storageHealthPercent(String hex) {
        int value = parseHealthHex(hex);
        switch (value) {
            case 0x00:
                return "100%";
            case 0x01:
                return "95%";
            case 0x02:
                return "90%";
            case 0x03:
                return "85%";
            case 0x04:
                return "80%";
            case 0x05:
                return "75%";
            case 0x06:
                return "70%";
            case 0x07:
                return "60%";
            case 0x08:
                return "50%";
            case 0x09:
                return "30%";
            case 0x0A:
                return "20%";
            case 0x0B:
                return "10%";
            default:
                return "Unknown";
        }
    }

    private int parseHealthHex(String hex) {
        if (hex == null) {
            return -1;
        }
        String value = hex.trim();
        if (value.startsWith("0x") || value.startsWith("0X")) {
            value = value.substring(2);
        }
        try {
            return Integer.parseInt(value, 16);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private String currentAiChargeLimit() {
        String limit = getPreferences(MODE_PRIVATE).getString(PREF_AI_CHARGE_LIMIT, "80");
        for (String value : AI_CHARGE_LIMITS) {
            if (value.equals(limit)) {
                return limit;
            }
        }
        return "80";
    }

    private String aiChargeLimitLabel(String limit) {
        return limit + "%";
    }

    private void setAiChargeLimit(String limit) {
        if (!isValidAiChargeLimit(limit)) {
            toast("Limit not supported");
            return;
        }
        getPreferences(MODE_PRIVATE).edit().putString(PREF_AI_CHARGE_LIMIT, limit).apply();
        RootShell.run(aiChargeScriptCommand(limit));
        refreshAiChargeStatus();
        toast("AI Charge set " + limit + "%");
    }

    private void disableAiCharge() {
        RootShell.run("old=$(cat " + AI_CHARGE_PID + " 2>/dev/null); " +
                "[ -n \"$old\" ] && kill \"$old\" 2>/dev/null; " +
                "rm -f " + AI_CHARGE_SCRIPT + " " + AI_CHARGE_PID + "; " +
                "[ -e " + AI_CHARGE_STOP_NODE + " ] && echo 0 > " + AI_CHARGE_STOP_NODE + " 2>/dev/null");
        refreshAiChargeStatus();
        toast("AI Charge disabled");
    }

    private String aiChargeScriptCommand(String limit) {
        return "mkdir -p /data/adb/service.d /data/local/tmp; " +
                "printf '%s\\n' " +
                "'#!/system/bin/sh' " +
                "'pidfile=" + AI_CHARGE_PID + "' " +
                "'old=$(cat \"$pidfile\" 2>/dev/null)' " +
                "'[ -n \"$old\" ] && kill \"$old\" 2>/dev/null' " +
                "'(' " +
                "'limit=" + limit + "' " +
                "'node=" + AI_CHARGE_STOP_NODE + "' " +
                "'cap=" + BATTERY_CAPACITY_NODE + "' " +
                "'while true; do' " +
                "'  if [ -e \"$node\" ] && [ -e \"$cap\" ]; then' " +
                "'    level=$(cat \"$cap\" 2>/dev/null | tr -dc 0-9)' " +
                "'    if [ -n \"$level\" ]; then' " +
                "'      if [ \"$level\" -ge \"$limit\" ]; then echo 1 > \"$node\" 2>/dev/null; else echo 0 > \"$node\" 2>/dev/null; fi' " +
                "'    fi' " +
                "'  fi' " +
                "'  sleep 30' " +
                "'done' " +
                "') >/dev/null 2>&1 < /dev/null &' " +
                "'echo $! > \"$pidfile\"' " +
                "> " + AI_CHARGE_SCRIPT + "; " +
                "chmod 755 " + AI_CHARGE_SCRIPT + "; " +
                "sh " + AI_CHARGE_SCRIPT + " >/dev/null 2>&1 < /dev/null";
    }

    private void refreshAiChargeStatus() {
        if (aiChargeStatusView != null) {
            aiChargeStatusView.setText(aiChargeStatus());
        }
    }

    private String aiChargeStatus() {
        if (!isAiChargeEnabled()) {
            return "Disabled";
        }
        String stop = firstLine(RootShell.run("[ -e " + AI_CHARGE_STOP_NODE + " ] && cat " + AI_CHARGE_STOP_NODE));
        if ("Not detected".equals(stop)) {
            return "Node missing";
        }
        String capacity = firstLine(RootShell.run("[ -e " + BATTERY_CAPACITY_NODE + " ] && cat " + BATTERY_CAPACITY_NODE));
        String state = "1".equals(stop) ? "Cut" : "Charging";
        if ("Not detected".equals(capacity)) {
            return state;
        }
        return capacity + "% / " + state;
    }

    private boolean isAiChargeEnabled() {
        return "1".equals(firstLine(RootShell.run("[ -f " + AI_CHARGE_SCRIPT + " ] && echo 1")));
    }

    private boolean isValidAiChargeLimit(String limit) {
        for (String value : AI_CHARGE_LIMITS) {
            if (value.equals(limit)) {
                return true;
            }
        }
        return false;
    }

    private void showCustomResolutionDialog(TextView target) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("1080x2400");
        input.setText(currentDisplaySize());
        new AlertDialog.Builder(this)
                .setTitle("Custom Resolution")
                .setView(input)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String resolution = input.getText().toString().trim().toLowerCase();
                    if (!isValidResolution(resolution)) {
                        toast("Use WIDTHxHEIGHT");
                        return;
                    }
                    applyDisplayResolution(resolution);
                    target.setText(currentDisplaySize());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCustomRefreshDialog(TextView target) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("120");
        input.setText(currentRefreshRate());
        new AlertDialog.Builder(this)
                .setTitle("Custom Refresh Rate")
                .setView(input)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String refresh = normalizeNumber(input.getText().toString());
                    if (!isValidNumber(refresh)) {
                        toast("Refresh rate invalid");
                        return;
                    }
                    applyRefreshRate(refresh);
                    target.setText(refreshRateLabel(refresh));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCustomGameFpsDialog(TextView target) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("120");
        input.setText(currentGameFpsOverride());
        new AlertDialog.Builder(this)
                .setTitle("Game Default FPS")
                .setView(input)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String fps = normalizeNumber(input.getText().toString());
                    if (!fps.matches("[0-9]+")) {
                        toast("FPS invalid");
                        return;
                    }
                    applyGameFpsOverride(fps);
                    target.setText(refreshRateLabel(fps));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showKernelNumberDialog(String title, TextView target, String hint, PickerAction action) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(hint);
        String current = target.getText().toString().trim();
        if (!"Not detected".equals(current) && current.matches("[0-9]+")) {
            input.setText(current);
            input.setSelection(current.length());
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (!value.matches("[0-9]+")) {
                        toast("Value invalid");
                        return;
                    }
                    action.onPick(value);
                    target.setText(value);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void applyDisplayResolution(String resolution) {
        if (!isValidResolution(resolution)) {
            toast("Resolution invalid");
            return;
        }
        RootShell.run("wm size " + resolution);
        getPreferences(MODE_PRIVATE).edit().putString(PREF_DISPLAY_RESOLUTION, resolution).apply();
        RootShell.run(displayBootScriptCommand());
        toast("Resolution set " + resolution);
    }

    private void applyRefreshRate(String refresh) {
        refresh = normalizeNumber(refresh);
        if (!isValidNumber(refresh)) {
            toast("Refresh rate invalid");
            return;
        }
        String size = currentPhysicalDisplaySize();
        int[] dims = parseSize(size);
        String command = refreshRateCommand(refresh, dims[0], dims[1]);
        RootShell.run(command);
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_DISPLAY_REFRESH, refresh)
                .putBoolean(PREF_FORCE_PEAK_REFRESH, false)
                .apply();
        RootShell.run(displayBootScriptCommand());
        toast("Refresh set " + refreshRateLabel(refresh));
    }

    private void applyGameFpsOverride(String fps) {
        fps = normalizeNumber(fps);
        if (!fps.matches("[0-9]+")) {
            toast("FPS invalid");
            return;
        }
        RootShell.run(setPropCommand(GAME_FPS_OVERRIDE_PROP, fps));
        getPreferences(MODE_PRIVATE).edit().putString(PREF_GAME_FPS_OVERRIDE, fps).apply();
        RootShell.run(displayBootScriptCommand());
        toast("Game FPS set " + refreshRateLabel(fps));
    }

    private void setHwOverlayDisabled(boolean disabled) {
        RootShell.run("service call SurfaceFlinger 1008 i32 " + (disabled ? "1" : "0") + " >/dev/null 2>&1");
        getPreferences(MODE_PRIVATE).edit().putBoolean(PREF_DISABLE_HW_OVERLAYS, disabled).apply();
        RootShell.run(displayBootScriptCommand());
        toast(disabled ? "HW Overlay disabled" : "HW Overlay enabled");
    }

    private String refreshRateOverlaySummary() {
        return isRefreshRateOverlayEnabled() ? "Current: Enabled" : "Current: Disabled";
    }

    private boolean isRefreshRateOverlayEnabled() {
        String out = RootShell.run("service call SurfaceFlinger " +
                SURFACE_FLINGER_SHOW_REFRESH_RATE + " i32 2 2>/dev/null");
        return out.contains("00000001") || out.toLowerCase().contains("true");
    }

    private void setRefreshRateOverlay(boolean enabled) {
        RootShell.run(refreshRateOverlayCommand(enabled));
        getPreferences(MODE_PRIVATE).edit().putBoolean(PREF_SHOW_REFRESH_RATE, enabled).apply();
        RootShell.run(displayBootScriptCommand());
        toast(enabled ? "Refresh rate overlay enabled" : "Refresh rate overlay disabled");
    }

    private String refreshRateOverlayCommand(boolean enabled) {
        String value = enabled ? "1" : "0";
        return "service call SurfaceFlinger " + SURFACE_FLINGER_SHOW_REFRESH_RATE +
                " i32 " + value + " >/dev/null 2>&1; " +
                "settings put system show_refresh_rate " + value;
    }

    private String forcePeakRefreshSummary() {
        String target = refreshRateLabel(highestSupportedRefreshRate());
        return isForcePeakRefreshEnabled() ? "Current: Enabled, " + target : "Target: " + target;
    }

    private boolean isForcePeakRefreshEnabled() {
        return getPreferences(MODE_PRIVATE).getBoolean(PREF_FORCE_PEAK_REFRESH, false);
    }

    private void setForcePeakRefreshRate(boolean enabled) {
        if (enabled) {
            String refresh = highestSupportedRefreshRate();
            if (!isValidNumber(refresh)) {
                toast("Highest refresh not detected");
                return;
            }
            int[] dims = parseSize(currentPhysicalDisplaySize());
            RootShell.run(forcePeakRefreshRateCommand(refresh, dims[0], dims[1]));
            getPreferences(MODE_PRIVATE).edit()
                    .putBoolean(PREF_FORCE_PEAK_REFRESH, true)
                    .putString(PREF_DISPLAY_REFRESH, refresh)
                    .apply();
            RootShell.run(displayBootScriptCommand());
            if (displayRefreshView != null) {
                displayRefreshView.setText(refreshRateLabel(refresh));
            }
            toast("Peak refresh forced " + refreshRateLabel(refresh));
        } else {
            RootShell.run(clearRefreshRateCommand());
            getPreferences(MODE_PRIVATE).edit()
                    .putBoolean(PREF_FORCE_PEAK_REFRESH, false)
                    .remove(PREF_DISPLAY_REFRESH)
                    .apply();
            RootShell.run(displayBootScriptCommand());
            if (displayRefreshView != null) {
                displayRefreshView.setText(refreshRateLabel(currentRefreshRate()));
            }
            toast("Peak refresh default");
        }
    }

    private void resetDisplayTweaks() {
        RootShell.run("wm size reset; " +
                clearRefreshRateCommand() + "; " +
                refreshRateOverlayCommand(false) + "; " +
                deletePropCommand(GAME_FPS_OVERRIDE_PROP) + "; " +
                "service call SurfaceFlinger 1008 i32 0 >/dev/null 2>&1; " +
                "rm -f " + DISPLAY_SCRIPT);
        getPreferences(MODE_PRIVATE).edit()
                .remove(PREF_DISPLAY_RESOLUTION)
                .remove(PREF_DISPLAY_REFRESH)
                .putBoolean(PREF_FORCE_PEAK_REFRESH, false)
                .putBoolean(PREF_SHOW_REFRESH_RATE, false)
                .remove(PREF_GAME_FPS_OVERRIDE)
                .putBoolean(PREF_DISABLE_HW_OVERLAYS, false)
                .apply();
        if (displayResolutionView != null) {
            displayResolutionView.setText(currentDisplaySize());
        }
        if (displayRefreshView != null) {
            displayRefreshView.setText(refreshRateLabel(currentRefreshRate()));
        }
        if (gameFpsView != null) {
            gameFpsView.setText(refreshRateLabel(currentGameFpsOverride()));
        }
        toast("Display tweaks reset");
    }

    private String displayBootScriptCommand() {
        String resolution = getPreferences(MODE_PRIVATE).getString(PREF_DISPLAY_RESOLUTION, "");
        String refresh = getPreferences(MODE_PRIVATE).getString(PREF_DISPLAY_REFRESH, "");
        String fps = getPreferences(MODE_PRIVATE).getString(PREF_GAME_FPS_OVERRIDE, "");
        boolean forcePeakRefresh = isForcePeakRefreshEnabled();
        boolean showRefreshRate = getPreferences(MODE_PRIVATE).getBoolean(PREF_SHOW_REFRESH_RATE, false);
        boolean disableOverlay = isHwOverlayDisabled();
        String targetRefresh = forcePeakRefresh ? highestSupportedRefreshRate() : refresh;

        if (!isValidResolution(resolution) && !isValidNumber(targetRefresh) &&
                !fps.matches("[0-9]+") && !showRefreshRate && !disableOverlay) {
            return "rm -f " + DISPLAY_SCRIPT;
        }

        ArrayList<String> lines = new ArrayList<>();
        lines.add("#!/system/bin/sh");
        lines.add("sleep 3");
        if (isValidResolution(resolution)) {
            lines.add("wm size " + resolution);
        }
        if (isValidNumber(targetRefresh)) {
            int[] dims = parseSize(currentPhysicalDisplaySize());
            lines.add(refreshRateCommand(targetRefresh, dims[0], dims[1]));
        }
        if (fps.matches("[0-9]+")) {
            lines.add(setPropCommand(GAME_FPS_OVERRIDE_PROP, fps));
        }
        if (showRefreshRate) {
            lines.add(refreshRateOverlayCommand(true));
        }
        if (disableOverlay) {
            lines.add("service call SurfaceFlinger 1008 i32 1 >/dev/null 2>&1");
        }

        StringBuilder command = new StringBuilder("mkdir -p /data/adb/service.d; printf '%s\\n'");
        for (String line : lines) {
            command.append(' ').append(shellSingleQuote(line));
        }
        command.append(" > ").append(DISPLAY_SCRIPT).append("; chmod 755 ").append(DISPLAY_SCRIPT);
        return command.toString();
    }

    private String clearRefreshRateCommand() {
        return "cmd display clear-user-preferred-display-mode 0 2>/dev/null; " +
                "cmd display clear-user-preferred-display-mode 2>/dev/null; " +
                "settings delete system min_refresh_rate 2>/dev/null; " +
                "settings delete system max_refresh_rate 2>/dev/null; " +
                "settings delete system peak_refresh_rate 2>/dev/null; " +
                "settings delete system tran_refresh_mode 2>/dev/null; " +
                "settings delete system tran_need_recovery_refresh_mode 2>/dev/null; " +
                "settings delete system last_tran_refresh_mode_in_refresh_setting 2>/dev/null; " +
                "settings delete system last_tran_refresh_mode_in_refresh_settings 2>/dev/null; " +
                "settings delete global unisoc.display_refreshrate 2>/dev/null; " +
                "settings delete global user_preferred_refresh_rate 2>/dev/null; " +
                deletePropCommand(TRAN_DEFAULT_REFRESH_MODE_PROP);
    }

    private String refreshRateCommand(String refresh, int width, int height) {
        String value = refreshRateFloatValue(refresh);
        String mode = refreshRateModeValue(refresh);
        String displayValue = matchedDisplayRefreshRate(refresh);
        return "settings put system min_refresh_rate " + value + "; " +
                "settings put system max_refresh_rate " + value + "; " +
                "settings put system peak_refresh_rate " + value + "; " +
                "settings put system tran_refresh_mode " + mode + "; " +
                "settings put system tran_need_recovery_refresh_mode " + mode + "; " +
                "settings put system last_tran_refresh_mode_in_refresh_setting " + mode + "; " +
                "settings put system last_tran_refresh_mode_in_refresh_settings " + mode + "; " +
                "settings put global unisoc.display_refreshrate " + mode + "; " +
                "settings put global user_preferred_refresh_rate " + value + "; " +
                setPropCommand(TRAN_DEFAULT_REFRESH_MODE_PROP, mode) + "; " +
                "if [ \"$(device_config get display_manager com.android.server.display.feature.flags.enable_user_preferred_mode_vote 2>/dev/null)\" = \"true\" ]; then " +
                "cmd display set-user-preferred-display-mode " + width + " " + height + " " + displayValue + " 0 2>/dev/null; " +
                "cmd display set-user-preferred-display-mode " + width + " " + height + " " + displayValue + " 2>/dev/null; " +
                "elif [ \"$(device_config get display_manager enable_user_preferred_mode_vote 2>/dev/null)\" = \"true\" ]; then " +
                "cmd display set-user-preferred-display-mode " + width + " " + height + " " + displayValue + " 0 2>/dev/null; " +
                "cmd display set-user-preferred-display-mode " + width + " " + height + " " + displayValue + " 2>/dev/null; " +
                "else cmd display clear-user-preferred-display-mode 0 2>/dev/null; " +
                "cmd display clear-user-preferred-display-mode 2>/dev/null; fi";
    }

    private String forcePeakRefreshRateCommand(String refresh, int width, int height) {
        String value = refreshRateFloatValue(refresh);
        String mode = refreshRateModeValue(refresh);
        String displayValue = matchedDisplayRefreshRate(refresh);
        return "settings put system min_refresh_rate Infinity 2>/dev/null; " +
                "settings put system min_refresh_rate " + value + " 2>/dev/null; " +
                "settings put system max_refresh_rate " + value + "; " +
                "settings put system peak_refresh_rate " + value + "; " +
                "settings put system tran_refresh_mode " + mode + "; " +
                "settings put system tran_need_recovery_refresh_mode " + mode + "; " +
                "settings put system last_tran_refresh_mode_in_refresh_setting " + mode + "; " +
                "settings put system last_tran_refresh_mode_in_refresh_settings " + mode + "; " +
                "settings put global unisoc.display_refreshrate " + mode + "; " +
                "settings put global user_preferred_refresh_rate " + value + "; " +
                setPropCommand(TRAN_DEFAULT_REFRESH_MODE_PROP, mode) + "; " +
                "cmd display set-user-preferred-display-mode " + width + " " + height + " " + displayValue + " 0 2>/dev/null; " +
                "cmd display set-user-preferred-display-mode " + width + " " + height + " " + displayValue + " 2>/dev/null";
    }

    private String highestSupportedRefreshRate() {
        String out = RootShell.run("dumpsys display | grep -o 'fps=[0-9.]*' | cut -d= -f2");
        float highest = 0f;
        for (String line : out.split("\\s+")) {
            String value = normalizeNumber(line);
            if (!isValidNumber(value)) {
                continue;
            }
            try {
                highest = Math.max(highest, Float.parseFloat(value));
            } catch (NumberFormatException ignored) {
            }
        }
        if (highest > 0f) {
            return formatRefreshRate(highest);
        }
        String current = currentRefreshRate();
        return isValidNumber(current) ? current : "120";
    }

    private String formatRefreshRate(float refresh) {
        int rounded = Math.round(refresh);
        if (Math.abs(refresh - rounded) < 0.02f) {
            return String.valueOf(rounded);
        }
        float twoDecimals = Math.round(refresh * 100f) / 100f;
        return normalizeNumber(Float.toString(twoDecimals));
    }

    private String refreshRateFloatValue(String refresh) {
        refresh = normalizeNumber(refresh);
        return refresh.contains(".") ? refresh : refresh + ".0";
    }

    private String refreshRateModeValue(String refresh) {
        refresh = normalizeNumber(refresh);
        try {
            return String.valueOf(Math.round(Float.parseFloat(refresh)));
        } catch (NumberFormatException ignored) {
            return refresh;
        }
    }

    private String matchedDisplayRefreshRate(String refresh) {
        String fallback = refreshRateFloatValue(refresh);
        float target;
        try {
            target = Float.parseFloat(normalizeNumber(refresh));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
        String out = RootShell.run("dumpsys display | grep -o 'fps=[0-9.]*' | cut -d= -f2 | sort -u");
        String best = "";
        float bestDiff = Float.MAX_VALUE;
        for (String line : out.split("\\n")) {
            String value = line.trim();
            if (!isValidNumber(value)) {
                continue;
            }
            try {
                float rate = Float.parseFloat(value);
                float diff = Math.abs(rate - target);
                if (Math.round(rate) == Math.round(target) && diff < bestDiff) {
                    best = value;
                    bestDiff = diff;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return best.length() > 0 ? best : fallback;
    }

    private String resolutionForWidth(String widthText) {
        int width;
        try {
            width = Integer.parseInt(widthText);
        } catch (NumberFormatException ignored) {
            width = 720;
        }
        int[] base = parseSize(currentPhysicalDisplaySize());
        int height = Math.round((float) width * (float) base[1] / (float) base[0]);
        if ((height % 2) != 0) {
            height++;
        }
        return width + "x" + height;
    }

    private String currentDisplaySize() {
        String out = RootShell.run("wm size");
        String override = findDisplaySizeLine(out, "Override size:");
        if (override.length() > 0) {
            return override;
        }
        String physical = findDisplaySizeLine(out, "Physical size:");
        return physical.length() > 0 ? physical : "Not detected";
    }

    private String currentPhysicalDisplaySize() {
        String out = RootShell.run("wm size");
        String physical = findDisplaySizeLine(out, "Physical size:");
        if (physical.length() > 0) {
            return physical;
        }
        String override = findDisplaySizeLine(out, "Override size:");
        return override.length() > 0 ? override : "720x1600";
    }

    private String findDisplaySizeLine(String out, String prefix) {
        if (out == null) {
            return "";
        }
        String[] lines = out.split("\\n");
        for (String line : lines) {
            String item = line.trim();
            if (item.startsWith(prefix)) {
                String size = item.substring(prefix.length()).trim();
                return isValidResolution(size) ? size : "";
            }
        }
        return "";
    }

    private int[] parseSize(String size) {
        if (!isValidResolution(size)) {
            return new int[]{720, 1600};
        }
        String[] parts = size.toLowerCase().split("x");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    private String currentRefreshRate() {
        String value = normalizeNumber(RootShell.run("settings get system peak_refresh_rate").trim());
        if (isValidNumber(value)) {
            return value;
        }
        String dump = RootShell.run("dumpsys display | grep -m 1 'mActiveRenderFrameRate='");
        int index = dump.indexOf("mActiveRenderFrameRate=");
        if (index >= 0) {
            return normalizeNumber(dump.substring(index + "mActiveRenderFrameRate=".length()).trim());
        }
        return "120";
    }

    private String currentGameFpsOverride() {
        String pref = getPreferences(MODE_PRIVATE).getString(PREF_GAME_FPS_OVERRIDE, "");
        if (pref.matches("[0-9]+")) {
            return pref;
        }
        String prop = normalizeNumber(RootShell.run("getprop " + GAME_FPS_OVERRIDE_PROP).trim());
        return prop.matches("[0-9]+") ? prop : "60";
    }

    private boolean isHwOverlayDisabled() {
        return getPreferences(MODE_PRIVATE).getBoolean(PREF_DISABLE_HW_OVERLAYS, false);
    }

    private String refreshRateLabel(String value) {
        value = normalizeNumber(value);
        return isValidNumber(value) ? value + "Hz" : "Not detected";
    }

    private String normalizeNumber(String value) {
        if (value == null) {
            return "";
        }
        String result = value.trim();
        while (result.endsWith(".0")) {
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

    private boolean isValidNumber(String value) {
        return value != null && value.matches("[0-9]+(\\.[0-9]+)?");
    }

    private boolean isValidResolution(String resolution) {
        if (resolution == null || !resolution.matches("[0-9]{3,5}x[0-9]{3,5}")) {
            return false;
        }
        int[] size = parseSizeUnchecked(resolution);
        return size[0] >= 320 && size[1] >= 320 && size[0] <= 4096 && size[1] <= 8192;
    }

    private int[] parseSizeUnchecked(String size) {
        String[] parts = size.toLowerCase().split("x");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    private String shellSingleQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }

    private String batteryValue(String file) {
        String node = safeNodeFile(file);
        if (node.length() == 0) {
            return "Not detected";
        }
        String raw = firstOutputLine(RootShell.run("for d in /sys/class/power_supply/battery /sys/class/power_supply/*; do " +
                "[ -d \"$d\" ] || continue; " +
                "type=$(cat \"$d/type\" 2>/dev/null); [ \"$type\" = Battery ] || continue; " +
                "[ -e \"$d/" + node + "\" ] && cat \"$d/" + node + "\" 2>/dev/null && exit 0; done"));
        return formatPowerValue(node, raw);
    }

    private String powerSupplyValue(String file) {
        String node = safeNodeFile(file);
        if (node.length() == 0) {
            return "Not detected";
        }
        String raw = firstOutputLine(RootShell.run("for d in /sys/class/power_supply/*; do " +
                "[ -d \"$d\" ] || continue; [ -e \"$d/" + node + "\" ] || continue; " +
                "cat \"$d/" + node + "\" 2>/dev/null && exit 0; done"));
        return formatPowerValue(node, raw);
    }

    private String formatPowerValue(String file, String raw) {
        if (raw == null || "Not detected".equals(raw)) {
            return "Not detected";
        }
        String value = raw.trim();
        if ("capacity".equals(file) && value.matches("-?[0-9]+")) {
            return value + "%";
        }
        if ("temp".equals(file) && value.matches("-?[0-9]+")) {
            return formatTemperature(value);
        }
        if (file.startsWith("voltage") && value.matches("-?[0-9]+")) {
            return formatMicroUnit(value, "V");
        }
        if (file.startsWith("current") || file.contains("current")) {
            if (value.matches("-?[0-9]+")) {
                return formatMicroUnit(value, "A");
            }
        }
        return value;
    }

    private String formatMicroUnit(String value, String unit) {
        try {
            long raw = Long.parseLong(value);
            double scaled = raw / 1000000.0d;
            if ("A".equals(unit)) {
                return String.format("%.0f mA", scaled * 1000.0d);
            }
            return String.format("%.2f %s", scaled, unit);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private void setBypassCharge(boolean enabled) {
        if (enabled) {
            String current = readFirstNode(CHARGE_CURRENT_NODES);
            if (isPositiveNumber(current)) {
                getPreferences(MODE_PRIVATE).edit().putString("bypass_charge_current", current).apply();
            }
            RootShell.run("[ -e " + QUICK_CHARGE_NODE + " ] && echo 0 > " + QUICK_CHARGE_NODE + " 2>/dev/null; " +
                    "[ -e " + CHARGE_SPEED_NODE + " ] && echo 0:0 > " + CHARGE_SPEED_NODE + " 2>/dev/null; " +
                    "for f in " + CHARGE_CURRENT_NODES + "; do " +
                    "[ -e \"$f\" ] && echo 0 > \"$f\" 2>/dev/null; done; " +
                    "[ -e " + BYPASS_CHARGE_NODE + " ] && echo 1 > " + BYPASS_CHARGE_NODE + " 2>/dev/null; " +
                    "[ -e " + AICHG_DISABLE_NODE + " ] && echo 1 > " + AICHG_DISABLE_NODE + " 2>/dev/null");
            toast("Bypass Charge enabled");
        } else {
            String restore = getPreferences(MODE_PRIVATE).getString("bypass_charge_current", "480000");
            if (!isPositiveNumber(restore)) restore = "480000";
            RootShell.run("for f in " + CHARGE_CURRENT_NODES + "; do " +
                    "[ -e \"$f\" ] && echo " + restore + " > \"$f\" 2>/dev/null; done; " +
                    "[ -e " + BYPASS_CHARGE_NODE + " ] && echo 0 > " + BYPASS_CHARGE_NODE + " 2>/dev/null; " +
                    "[ -e " + AICHG_DISABLE_NODE + " ] && echo 0 > " + AICHG_DISABLE_NODE + " 2>/dev/null");
            toast("Bypass Charge disabled");
        }
    }

    private boolean isBypassChargeEnabled() {
        String bypass = RootShell.run("[ -e " + BYPASS_CHARGE_NODE + " ] && cat " + BYPASS_CHARGE_NODE);
        if (bypass.contains("bypass_discharging = 1")) return true;
        String current = readFirstNode(CHARGE_CURRENT_NODES);
        if (!current.matches("[0-9]+")) return false;
        try {
            return Long.parseLong(current) <= 1000;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setFastCharge(boolean enabled) {
        if (enabled && isBypassChargeEnabled()) {
            toast("Disable Bypass Charge first");
            return;
        }
        if (enabled) {
            String input = readFirstNode(INPUT_CURRENT_NODES);
            String current = readFirstNode(CHARGE_CURRENT_NODES);
            if (isPositiveNumber(input)) {
                getPreferences(MODE_PRIVATE).edit().putString("fast_input_current", input).apply();
            }
            if (isPositiveNumber(current)) {
                getPreferences(MODE_PRIVATE).edit().putString("fast_charge_current", current).apply();
            }
            RootShell.run("[ -e " + QUICK_CHARGE_NODE + " ] && echo 1 > " + QUICK_CHARGE_NODE + " 2>/dev/null; " +
                    "[ -e " + CHARGE_SPEED_NODE + " ] && echo 1:2 > " + CHARGE_SPEED_NODE + " 2>/dev/null; " +
                    "for f in " + INPUT_CURRENT_NODES + "; do [ -e \"$f\" ] && echo 2000000 > \"$f\" 2>/dev/null; done; " +
                    "for f in " + CHARGE_CURRENT_NODES + "; do [ -e \"$f\" ] && echo 2000000 > \"$f\" 2>/dev/null; done");
            toast("Fast Charge enabled");
        } else {
            String input = getPreferences(MODE_PRIVATE).getString("fast_input_current", "500000");
            String current = getPreferences(MODE_PRIVATE).getString("fast_charge_current", "480000");
            if (!isPositiveNumber(input)) input = "500000";
            if (!isPositiveNumber(current)) current = "480000";
            String restoreCharge = isBypassChargeEnabled() ? "" :
                    "for f in " + CHARGE_CURRENT_NODES + "; do [ -e \"$f\" ] && echo " + current + " > \"$f\" 2>/dev/null; done; ";
            RootShell.run("[ -e " + QUICK_CHARGE_NODE + " ] && echo 0 > " + QUICK_CHARGE_NODE + " 2>/dev/null; " +
                    "[ -e " + CHARGE_SPEED_NODE + " ] && echo 0:0 > " + CHARGE_SPEED_NODE + " 2>/dev/null; " +
                    "for f in " + INPUT_CURRENT_NODES + "; do [ -e \"$f\" ] && echo " + input + " > \"$f\" 2>/dev/null; done; " +
                    restoreCharge);
            toast("Fast Charge disabled");
        }
    }

    private boolean isFastChargeEnabled() {
        String quick = firstLine(RootShell.run("[ -e " + QUICK_CHARGE_NODE + " ] && cat " + QUICK_CHARGE_NODE));
        String speed = firstLine(RootShell.run("[ -e " + CHARGE_SPEED_NODE + " ] && cat " + CHARGE_SPEED_NODE));
        return "1".equals(quick) || speed.startsWith("1:");
    }

    private String readFirstNode(String nodes) {
        return firstLine(RootShell.run("for f in " + nodes + "; do [ -e \"$f\" ] && cat \"$f\" && break; done"));
    }

    private boolean isPositiveNumber(String value) {
        if (value == null || !value.matches("[0-9]+")) return false;
        try {
            return Long.parseLong(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void applyCpuGovernor(String gov) {
        if (gov.length() == 0) {
            toast("Governor not detected");
            return;
        }
        RootShell.run("for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_governor; do " +
                "[ -e \"$f\" ] || continue; echo " + safe(gov) + " > \"$f\" 2>/dev/null; done");
        toast("CPU Governor applied");
    }

    private void applyGpuGovernor(String gov) {
        if (gov.length() == 0) {
            toast("Governor not detected");
            return;
        }
        RootShell.run("for d in /sys/class/devfreq/*gpu* /sys/class/devfreq/*.gpu; do " +
                "[ -d \"$d\" ] || continue; [ -f \"$d/governor\" ] || continue; " +
                "echo " + safe(gov) + " > \"$d/governor\" 2>/dev/null; done");
        toast("GPU Governor applied");
    }

    private void applyCpuFrequency(String freq) {
        if (!freq.matches("[0-9]+")) {
            toast("Frequency not detected");
            return;
        }
        RootShell.run("for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_max_freq; do " +
                "[ -e \"$f\" ] || continue; echo " + freq + " > \"$f\" 2>/dev/null; done");
        toast("CPU frequency applied");
    }

    private void applyCpuLittleFrequency(String freq) {
        applyCpuClusterFrequency(freq, "head -n 1", "CPU Little frequency applied");
    }

    private void applyCpuBigFrequency(String freq) {
        applyCpuClusterFrequency(freq, "tail -n 1", "CPU Big frequency applied");
    }

    private void applyCpuClusterFrequency(String freq, String picker, String message) {
        if (!freq.matches("[0-9]+")) {
            toast("Frequency not detected");
            return;
        }
        RootShell.run("p=$(for d in /sys/devices/system/cpu/cpufreq/policy*; do " +
                "[ -f \"$d/scaling_max_freq\" ] && echo \"$d\"; done | sort -V | " + picker + "); " +
                "[ -n \"$p\" ] && echo " + freq + " > \"$p/scaling_max_freq\" 2>/dev/null");
        toast(message);
    }

    private void applyGpuFrequency(String freq) {
        if (!freq.matches("[0-9]+")) {
            toast("Frequency not detected");
            return;
        }
        RootShell.run("for d in /sys/class/devfreq/*gpu* /sys/class/devfreq/*.gpu; do " +
                "[ -d \"$d\" ] || continue; " +
                "[ -f \"$d/max_freq\" ] && echo " + freq + " > \"$d/max_freq\" 2>/dev/null; " +
                "[ -f \"$d/min_freq\" ] && echo " + freq + " > \"$d/min_freq\" 2>/dev/null; done");
        toast("GPU frequency applied");
    }

    private String currentCpuGovernor() {
        return firstLine(RootShell.run("for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_governor; do [ -e \"$f\" ] && cat \"$f\" && break; done"));
    }

    private String currentGpuGovernor() {
        return firstLine(RootShell.run("for d in /sys/class/devfreq/*gpu* /sys/class/devfreq/*.gpu; do [ -d \"$d\" ] || continue; [ -f \"$d/governor\" ] && cat \"$d/governor\" && break; done"));
    }

    private String currentCpuFrequency() {
        return firstLine(RootShell.run("for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_max_freq; do [ -e \"$f\" ] && cat \"$f\" && break; done"));
    }

    private String currentCpuLittleFrequency() {
        return firstLine(RootShell.run("p=$(for d in /sys/devices/system/cpu/cpufreq/policy*; do [ -f \"$d/scaling_max_freq\" ] && echo \"$d\"; done | sort -V | head -n 1); [ -n \"$p\" ] && cat \"$p/scaling_max_freq\""));
    }

    private String currentCpuBigFrequency() {
        return firstLine(RootShell.run("p=$(for d in /sys/devices/system/cpu/cpufreq/policy*; do [ -f \"$d/scaling_max_freq\" ] && echo \"$d\"; done | sort -V | tail -n 1); [ -n \"$p\" ] && cat \"$p/scaling_max_freq\""));
    }

    private String currentGpuFrequency() {
        return firstLine(RootShell.run("for d in /sys/class/devfreq/*gpu* /sys/class/devfreq/*.gpu; do [ -d \"$d\" ] || continue; [ -f \"$d/max_freq\" ] && cat \"$d/max_freq\" && break; done"));
    }

    private ArrayList<String> getCpuGovernors() {
        String out = RootShell.run("for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_available_governors; do [ -e \"$f\" ] && cat \"$f\"; done | tr ' ' '\\n' | sort -u");
        return splitLines(out);
    }

    private ArrayList<String> getCpuFrequencies() {
        String out = RootShell.run("for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_available_frequencies; do [ -e \"$f\" ] && cat \"$f\"; done | tr ' ' '\\n' | sort -nu");
        return splitLines(out);
    }

    private ArrayList<String> getCpuLittleFrequencies() {
        String out = RootShell.run("p=$(for d in /sys/devices/system/cpu/cpufreq/policy*; do [ -f \"$d/scaling_available_frequencies\" ] && echo \"$d\"; done | sort -V | head -n 1); [ -n \"$p\" ] && cat \"$p/scaling_available_frequencies\" | tr ' ' '\\n' | sort -nu");
        return splitLines(out);
    }

    private ArrayList<String> getCpuBigFrequencies() {
        String out = RootShell.run("p=$(for d in /sys/devices/system/cpu/cpufreq/policy*; do [ -f \"$d/scaling_available_frequencies\" ] && echo \"$d\"; done | sort -V | tail -n 1); [ -n \"$p\" ] && cat \"$p/scaling_available_frequencies\" | tr ' ' '\\n' | sort -nu");
        return splitLines(out);
    }

    private ArrayList<String> getGpuGovernors() {
        String out = RootShell.run("for d in /sys/class/devfreq/*gpu* /sys/class/devfreq/*.gpu; do [ -d \"$d\" ] || continue; [ -f \"$d/available_governors\" ] && cat \"$d/available_governors\"; done | tr ' ' '\\n' | sort -u");
        return splitLines(out);
    }

    private ArrayList<String> getGpuFrequencies() {
        String out = RootShell.run("for d in /sys/class/devfreq/*gpu* /sys/class/devfreq/*.gpu; do [ -d \"$d\" ] || continue; [ -f \"$d/available_frequencies\" ] && cat \"$d/available_frequencies\"; done | tr ' ' '\\n' | sort -nu");
        return splitLines(out);
    }

    private ArrayList<String> getDevfreqDevices() {
        String out = RootShell.run("for d in /sys/class/devfreq/*; do " +
                "[ -d \"$d\" ] || continue; n=${d##*/}; echo \"$n\"; done | sort");
        return splitDeviceLines(out);
    }

    private String currentDevfreqDevice() {
        String pref = getPreferences(MODE_PRIVATE).getString(PREF_DEVFREQ_DEVICE, "");
        if (devfreqDeviceExists(pref)) {
            return pref;
        }
        ArrayList<String> devices = getDevfreqDevices();
        if (devices.size() > 0 && !"Not detected".equals(devices.get(0))) {
            String value = devices.get(0);
            getPreferences(MODE_PRIVATE).edit().putString(PREF_DEVFREQ_DEVICE, value).apply();
            return value;
        }
        return "Not detected";
    }

    private void setDevfreqDevice(String device) {
        if (!devfreqDeviceExists(device)) {
            toast("Devfreq device not detected");
            return;
        }
        getPreferences(MODE_PRIVATE).edit().putString(PREF_DEVFREQ_DEVICE, device).apply();
        toast("Devfreq device selected");
    }

    private boolean devfreqDeviceExists(String device) {
        String path = devfreqPath(device);
        return path.length() > 0 && kernelNodeExists(path);
    }

    private String devfreqPath(String device) {
        String clean = safeDeviceName(device);
        if (clean.length() == 0) {
            return "";
        }
        return "/sys/class/devfreq/" + clean;
    }

    private String devfreqNode(String file) {
        String cleanFile = safeNodeFile(file);
        String path = devfreqPath(currentDevfreqDevice());
        if (path.length() == 0 || cleanFile.length() == 0) {
            return "Not detected";
        }
        return path + "/" + cleanFile;
    }

    private String devfreqValue(String file) {
        return kernelNodeValue(devfreqNode(file));
    }

    private ArrayList<String> getDevfreqGovernors() {
        return splitLines(readKernelLine(devfreqNode("available_governors")));
    }

    private ArrayList<String> getDevfreqFrequencies() {
        return splitLines(readKernelLine(devfreqNode("available_frequencies")));
    }

    private void applyDevfreqNode(String file, String value, String label) {
        if ("Not detected".equals(value)) {
            toast(label + " not detected");
            return;
        }
        if (("min_freq".equals(file) || "max_freq".equals(file)) && !value.matches("[0-9]+")) {
            toast(label + " invalid");
            return;
        }
        writeKernelNode(devfreqNode(file), value, label);
    }

    private String currentIoScheduler() {
        return activeKernelOption(readFirstKernelLineFromGlob(IO_SCHEDULER_NODES));
    }

    private ArrayList<String> getIoSchedulers() {
        String out = RootShell.run("for f in " + IO_SCHEDULER_NODES + "; do " +
                "[ -e \"$f\" ] && cat \"$f\"; done | tr ' []' '\\n' | sort -u");
        return splitLines(out);
    }

    private void applyIoScheduler(String scheduler) {
        String clean = safe(scheduler);
        if (clean.length() == 0 || "Not detected".equals(scheduler)) {
            toast("I/O Scheduler not detected");
            return;
        }
        String out = RootShell.run("found=0; failed=0; for f in " + IO_SCHEDULER_NODES + "; do " +
                "[ -e \"$f\" ] || continue; found=1; echo " + clean + " > \"$f\" 2>/dev/null || failed=1; done; " +
                "[ \"$found\" = 0 ] && echo TA_NOT_FOUND; [ \"$failed\" = 1 ] && echo TA_WRITE_FAILED");
        if (showKernelWriteResult(out, "I/O Scheduler")) {
            toast("I/O Scheduler applied");
        }
    }

    private String currentBlockQueueValue(String file) {
        String node = safeNodeFile(file);
        if (node.length() == 0) {
            return "Not detected";
        }
        return firstOutputLine(RootShell.run("for q in " + BLOCK_QUEUE_PATHS + "; do " +
                "[ -e \"$q/" + node + "\" ] && cat \"$q/" + node + "\" 2>/dev/null && exit 0; done"));
    }

    private void applyBlockQueueValue(String file, String value, String label) {
        String node = safeNodeFile(file);
        if (node.length() == 0 || !value.matches("[0-9]+")) {
            toast(label + " invalid");
            return;
        }
        String out = RootShell.run("found=0; failed=0; for q in " + BLOCK_QUEUE_PATHS + "; do " +
                "[ -e \"$q/" + node + "\" ] || continue; found=1; " +
                "echo " + value + " > \"$q/" + node + "\" 2>/dev/null || failed=1; done; " +
                "[ \"$found\" = 0 ] && echo TA_NOT_FOUND; [ \"$failed\" = 1 ] && echo TA_WRITE_FAILED");
        if (showKernelWriteResult(out, label)) {
            toast(label + " applied");
        }
    }

    private String blockQueueSwitchSummary(String file) {
        String value = currentBlockQueueValue(file);
        if ("1".equals(value)) {
            return "Current: Enabled";
        }
        if ("0".equals(value)) {
            return "Current: Disabled";
        }
        return "Not detected".equals(value) ? value : "Current: " + value;
    }

    private boolean blockQueueSwitchEnabled(String file) {
        return "1".equals(currentBlockQueueValue(file));
    }

    private void applyBlockQueueSwitch(String file, boolean enabled, String label) {
        applyBlockQueueValue(file, enabled ? "1" : "0", label);
    }

    private String zramSummary() {
        if (!kernelNodeExists(ZRAM_DISKSIZE_NODE)) {
            return "Not detected";
        }
        return isZramEnabled() ? "Current: Enabled" : "Current: Disabled";
    }

    private boolean isZramEnabled() {
        return "1".equals(firstLine(RootShell.run("grep -q 'zram0' /proc/swaps 2>/dev/null && echo 1 || echo 0")));
    }

    private void setZramEnabled(boolean enabled) {
        if (enabled) {
            String size = currentZramSize();
            if (!isValidZramSize(size)) {
                size = "6GB";
            }
            String algorithm = currentZramAlgorithm();
            if ("Not detected".equals(algorithm)) {
                algorithm = firstUsableZramAlgorithm();
            }
            configureZram(size, algorithm, "Zram");
        } else {
            String block = currentZramBlock();
            String out = RootShell.run("if [ ! -e " + shellSingleQuote(ZRAM_RESET_NODE) + " ]; then echo TA_NOT_FOUND; else " +
                    "swapoff " + shellSingleQuote(block) + " 2>/dev/null; " +
                    "echo 1 > " + shellSingleQuote(ZRAM_RESET_NODE) + " 2>/dev/null || echo TA_WRITE_FAILED; fi");
            if (showKernelWriteResult(out, "Zram")) {
                toast("Zram disabled");
            }
        }
    }

    private void applyZramSize(String size) {
        if (!isValidZramSize(size)) {
            toast("Zram Size invalid");
            return;
        }
        String algorithm = currentZramAlgorithm();
        if ("Not detected".equals(algorithm)) {
            algorithm = firstUsableZramAlgorithm();
        }
        configureZram(size, algorithm, "Zram Size");
    }

    private void applyZramAlgorithm(String algorithm) {
        if ("Not detected".equals(algorithm) || safe(algorithm).length() == 0) {
            toast("Compression Algorithm not detected");
            return;
        }
        String size = currentZramSize();
        if (!isValidZramSize(size)) {
            size = "6GB";
        }
        configureZram(size, algorithm, "Compression Algorithm");
    }

    private void configureZram(String size, String algorithm, String label) {
        long bytes = zramSizeBytes(size);
        String cleanAlgorithm = safe(algorithm);
        if (bytes <= 0 || cleanAlgorithm.length() == 0) {
            toast(label + " invalid");
            return;
        }
        String block = currentZramBlock();
        String out = RootShell.run("if [ ! -e " + shellSingleQuote(ZRAM_DISKSIZE_NODE) + " ] || " +
                "[ ! -e " + shellSingleQuote(ZRAM_RESET_NODE) + " ]; then echo TA_NOT_FOUND; else " +
                "swapoff " + shellSingleQuote(block) + " 2>/dev/null; " +
                "echo 1 > " + shellSingleQuote(ZRAM_RESET_NODE) + " 2>/dev/null || echo TA_RESET_FAILED; " +
                "if [ -e " + shellSingleQuote(ZRAM_COMP_ALGORITHM_NODE) + " ]; then " +
                "echo " + cleanAlgorithm + " > " + shellSingleQuote(ZRAM_COMP_ALGORITHM_NODE) + " 2>/dev/null || echo TA_ALGORITHM_FAILED; fi; " +
                "echo " + bytes + " > " + shellSingleQuote(ZRAM_DISKSIZE_NODE) + " 2>/dev/null || echo TA_WRITE_FAILED; " +
                "mkswap " + shellSingleQuote(block) + " >/dev/null 2>&1 || echo TA_MKSWAP_FAILED; " +
                "swapon " + shellSingleQuote(block) + " 2>/dev/null || echo TA_SWAPON_FAILED; fi");
        if (showKernelWriteResult(out, label)) {
            toast(label + " applied");
        }
    }

    private void compactZram() {
        String out = RootShell.run("if [ ! -e " + shellSingleQuote(ZRAM_COMPACT_NODE) + " ]; then echo TA_NOT_FOUND; " +
                "else echo 1 > " + shellSingleQuote(ZRAM_COMPACT_NODE) + " 2>/dev/null || echo TA_WRITE_FAILED; fi");
        if (showKernelWriteResult(out, "Compact Zram")) {
            toast("Zram compact requested");
        }
    }

    private String currentZramSize() {
        String value = kernelNodeValue(ZRAM_DISKSIZE_NODE);
        if (!isPositiveNumber(value)) {
            return "Not detected";
        }
        try {
            long bytes = Long.parseLong(value);
            long gb = Math.round(bytes / 1073741824.0d);
            return gb > 0 ? gb + "GB" : "Not detected";
        } catch (NumberFormatException e) {
            return "Not detected";
        }
    }

    private ArrayList<String> getZramSizes() {
        return new ArrayList<>(Arrays.asList(ZRAM_SIZE_VALUES));
    }

    private String currentZramAlgorithm() {
        return activeKernelOption(readKernelLine(ZRAM_COMP_ALGORITHM_NODE));
    }

    private ArrayList<String> getZramAlgorithms() {
        ArrayList<String> available = parseKernelOptions(readKernelLine(ZRAM_COMP_ALGORITHM_NODE));
        if (available.size() == 1 && "Not detected".equals(available.get(0))) {
            if (!kernelNodeExists(ZRAM_COMP_ALGORITHM_NODE)) {
                return available;
            }
            return new ArrayList<>(Arrays.asList(ZRAM_ALGORITHM_PREFERENCE));
        }
        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        for (String preferred : ZRAM_ALGORITHM_PREFERENCE) {
            if (available.contains(preferred)) {
                ordered.add(preferred);
            }
        }
        ordered.addAll(available);
        return new ArrayList<>(ordered);
    }

    private String firstUsableZramAlgorithm() {
        ArrayList<String> algorithms = getZramAlgorithms();
        if (algorithms.size() > 0 && !"Not detected".equals(algorithms.get(0))) {
            return algorithms.get(0);
        }
        return "lz4";
    }

    private boolean isValidZramSize(String size) {
        for (String item : ZRAM_SIZE_VALUES) {
            if (item.equals(size)) {
                return true;
            }
        }
        return false;
    }

    private long zramSizeBytes(String size) {
        String digits = size == null ? "" : size.replaceAll("[^0-9]", "");
        if (!digits.matches("[0-9]+")) {
            return 0;
        }
        try {
            return Long.parseLong(digits) * 1024L * 1024L * 1024L;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String currentZramBlock() {
        String out = RootShell.run("[ -b " + shellSingleQuote(ZRAM_BLOCK_NODE) + " ] && echo " +
                shellSingleQuote(ZRAM_BLOCK_NODE) + " && exit 0; " +
                "[ -b " + shellSingleQuote(ZRAM_ALT_BLOCK_NODE) + " ] && echo " +
                shellSingleQuote(ZRAM_ALT_BLOCK_NODE));
        String value = firstLine(out);
        return value.startsWith("/dev/") ? value : ZRAM_BLOCK_NODE;
    }

    private String kernelNodeValue(String node) {
        return firstLine(RootShell.run("[ -e " + shellSingleQuote(node) + " ] && cat " +
                shellSingleQuote(node) + " 2>/dev/null"));
    }

    private String kernelSwitchSummary(String node) {
        String value = kernelNodeValue(node);
        if ("1".equals(value)) {
            return "Current: Enabled";
        }
        if ("0".equals(value)) {
            return "Current: Disabled";
        }
        return "Not detected".equals(value) ? value : "Current: " + value;
    }

    private boolean kernelSwitchEnabled(String node) {
        return "1".equals(kernelNodeValue(node));
    }

    private void applyKernelSwitchNode(String node, boolean enabled, String label) {
        writeKernelNode(node, enabled ? "1" : "0", label);
    }

    private void applyKernelNodeValue(String node, String value, String label) {
        if (value == null || safe(value).length() == 0) {
            toast(label + " invalid");
            return;
        }
        writeKernelNode(node, value, label);
    }

    private void applyGpuBoostLevel(String value) {
        String clean = safe(value);
        if (clean.length() == 0 || !clean.matches("[0-9]+")) {
            toast("GPU Boost Level invalid");
            return;
        }
        String path = shellSingleQuote(GPU_BOOST_LEVEL2_NODE);
        String out = RootShell.run("if [ ! -e " + path + " ]; then echo TA_NOT_FOUND; " +
                "elif [ ! -w " + path + " ]; then echo TA_NOT_WRITABLE; " +
                "else echo " + clean + " > " + path + " 2>/dev/null || echo TA_WRITE_FAILED; " +
                "now=$(cat " + path + " 2>/dev/null); echo TA_READ:$now; fi");
        if (!showKernelWriteResult(out, "GPU Boost Level")) {
            return;
        }
        String readBack = markerValue(out, "TA_READ:");
        if (clean.equals(readBack)) {
            toast("GPU Boost Level applied: " + readBack);
        } else if (readBack.length() > 0) {
            toast("GPU Boost rejected, read " + readBack);
        } else {
            toast("GPU Boost Level applied");
        }
    }

    private boolean writeKernelNode(String node, String value, String label) {
        if (node == null || "Not detected".equals(node)) {
            toast(label + " not detected");
            return false;
        }
        String clean = safe(value);
        if (clean.length() == 0) {
            toast(label + " invalid");
            return false;
        }
        String path = shellSingleQuote(node);
        String out = RootShell.run("if [ ! -e " + path + " ]; then echo TA_NOT_FOUND; " +
                "elif [ ! -w " + path + " ]; then echo TA_NOT_WRITABLE; " +
                "else echo " + clean + " > " + path + " 2>/dev/null || echo TA_WRITE_FAILED; fi");
        if (showKernelWriteResult(out, label)) {
            toast(label + " applied");
            return true;
        }
        return false;
    }

    private boolean showKernelWriteResult(String output, String label) {
        String out = output == null ? "" : output.trim();
        if (out.contains("TA_NOT_FOUND")) {
            toast(label + " not detected");
            return false;
        }
        if (out.contains("TA_NOT_WRITABLE")) {
            toast(label + " not writable");
            return false;
        }
        if (out.contains("TA_RESET_FAILED") || out.contains("TA_ALGORITHM_FAILED") ||
                out.contains("TA_WRITE_FAILED") || out.contains("TA_MKSWAP_FAILED") ||
                out.contains("TA_SWAPON_FAILED")) {
            toast(label + " write failed");
            return false;
        }
        if (out.length() > 0 && isBadShellOutput(out)) {
            toast(label + " write failed");
            return false;
        }
        if (out.length() > 0 && out.toLowerCase().contains("invalid")) {
            toast(label + " invalid");
            return false;
        }
        return true;
    }

    private String markerValue(String input, String marker) {
        if (input == null || marker == null) {
            return "";
        }
        int start = input.indexOf(marker);
        if (start < 0) {
            return "";
        }
        start += marker.length();
        int end = input.indexOf('\n', start);
        if (end < 0) {
            end = input.length();
        }
        return input.substring(start, end).trim();
    }

    private boolean kernelNodeExists(String node) {
        return "1".equals(firstLine(RootShell.run("[ -e " + shellSingleQuote(node) + " ] && echo 1")));
    }

    private ArrayList<String> getSchedPeltMultiplierValues() {
        return listWithCurrent(SCHED_PELT_MULTIPLIER_VALUES, kernelNodeValue(SCHED_PELT_MULTIPLIER_NODE));
    }

    private ArrayList<String> getTcpCongestionAlgorithms() {
        return splitLines(RootShell.run("[ -e " + shellSingleQuote(TCP_AVAILABLE_CONGESTION_CONTROL_NODE) + " ] && cat " +
                shellSingleQuote(TCP_AVAILABLE_CONGESTION_CONTROL_NODE)));
    }

    private ArrayList<String> getPstoreCompressValues() {
        if (!kernelNodeExists(PSTORE_COMPRESS_NODE)) {
            ArrayList<String> values = new ArrayList<>();
            values.add("Not detected");
            return values;
        }
        return listWithCurrent(PSTORE_COMPRESS_VALUES, kernelNodeValue(PSTORE_COMPRESS_NODE));
    }

    private String currentKeyboardPollingInterval() {
        String node = keyboardPollingNode();
        if ("Not detected".equals(node)) {
            return node;
        }
        return kernelNodeValue(node);
    }

    private void applyKeyboardPollingInterval(String value) {
        String node = keyboardPollingNode();
        if ("Not detected".equals(node)) {
            toast("Keyboard Polling Interval not detected");
            return;
        }
        applyKernelNodeValue(node, value, "Keyboard Polling Interval");
    }

    private String keyboardPollingNode() {
        String out = RootShell.run("for d in /sys/class/input/input*; do " +
                "[ -d \"$d\" ] || continue; name=$(cat \"$d/name\" 2>/dev/null); " +
                "case \"$name\" in *Keyboard*|*keyboard*|*gpio-keys*|*fp-keys*|*keys*) " +
                "node=$(find \"$d\" -maxdepth 2 -type f \\( -iname '*poll*' -o -iname '*interval*' -o -iname '*rate*' \\) 2>/dev/null | head -n 1); " +
                "[ -n \"$node\" ] && echo \"$node\" && exit 0;; esac; done");
        String node = firstLine(out);
        return node.startsWith("/sys/") ? node : "Not detected";
    }

    private String touchInputName() {
        String out = RootShell.run("for d in /sys/class/input/input*; do " +
                "[ -d \"$d\" ] || continue; name=$(cat \"$d/name\" 2>/dev/null); " +
                "lower=$(echo \"$name\" | tr 'A-Z' 'a-z'); " +
                "case \"$lower\" in *touch*|*tcm*|*fts*|*goodix*|*novatek*|*synaptics*|*sec_ts*) " +
                "echo \"$name\" && exit 0;; esac; done");
        return firstOutputLine(out);
    }

    private String touchNode(String file) {
        String node = safeNodeFile(file);
        if (node.length() == 0) {
            return "Not detected";
        }
        String out = RootShell.run("for d in /sys/class/input/input*; do " +
                "[ -d \"$d\" ] || continue; name=$(cat \"$d/name\" 2>/dev/null); " +
                "lower=$(echo \"$name\" | tr 'A-Z' 'a-z'); " +
                "case \"$lower\" in *touch*|*tcm*|*fts*|*goodix*|*novatek*|*synaptics*|*sec_ts*) " +
                "found=$(find \"$d\" \"$d/device\" /sys/devices/platform/tran_touch -maxdepth 4 -type f -name " +
                shellSingleQuote(node) + " 2>/dev/null | head -n 1); " +
                "[ -n \"$found\" ] && echo \"$found\" && exit 0;; esac; done; " +
                "find /sys/devices/platform/tran_touch /sys/class/input -maxdepth 5 -type f -name " +
                shellSingleQuote(node) + " 2>/dev/null | head -n 1");
        String value = firstOutputLine(out);
        return value.startsWith("/sys/") ? value : "Not detected";
    }

    private String touchNodeValue(String file) {
        String node = touchNode(file);
        return "Not detected".equals(node) ? node : kernelNodeValue(node);
    }

    private String touchSwitchSummary(String file) {
        String value = touchNodeValue(file);
        if ("1".equals(value) || "true".equalsIgnoreCase(value)) {
            return "Current: Enabled";
        }
        if ("0".equals(value) || "false".equalsIgnoreCase(value)) {
            return "Current: Disabled";
        }
        return "Not detected".equals(value) ? value : "Current: " + value;
    }

    private boolean touchSwitchEnabled(String file) {
        String value = touchNodeValue(file);
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    private void applyTouchSwitch(String file, boolean enabled, String label) {
        writeKernelNode(touchNode(file), enabled ? "1" : "0", label);
    }

    private void applyTouchValue(String file, String value, String label) {
        if (!value.matches("[0-9]+")) {
            toast(label + " invalid");
            return;
        }
        writeKernelNode(touchNode(file), value, label);
    }

    private String thermalTemp(String target) {
        String pattern = thermalCasePattern(target);
        if (pattern.length() == 0) {
            return "Not detected";
        }
        String out = RootShell.run("for z in /sys/class/thermal/thermal_zone*; do " +
                "[ -d \"$z\" ] || continue; type=$(cat \"$z/type\" 2>/dev/null); " +
                "lower=$(echo \"$type\" | tr 'A-Z' 'a-z'); " +
                "case \"$lower\" in " + pattern + ") cat \"$z/temp\" 2>/dev/null && exit 0;; esac; done");
        return formatTemperature(firstOutputLine(out));
    }

    private String coolingState(String target) {
        String pattern = thermalCasePattern(target);
        if (pattern.length() == 0) {
            return "Not detected";
        }
        String out = RootShell.run("for c in /sys/class/thermal/cooling_device*; do " +
                "[ -d \"$c\" ] || continue; type=$(cat \"$c/type\" 2>/dev/null); " +
                "lower=$(echo \"$type\" | tr 'A-Z' 'a-z'); " +
                "case \"$lower\" in " + pattern + ") " +
                "cur=$(cat \"$c/cur_state\" 2>/dev/null); max=$(cat \"$c/max_state\" 2>/dev/null); " +
                "[ -n \"$cur\" ] && echo \"$cur/$max\" && exit 0;; esac; done");
        return firstOutputLine(out);
    }

    private String thermalCasePattern(String target) {
        if ("cpu".equals(target)) {
            return "*cpu*|*soc*";
        }
        if ("gpu".equals(target)) {
            return "*gpu*";
        }
        if ("board".equals(target)) {
            return "*board*|*pcb*";
        }
        if ("battery".equals(target)) {
            return "*battery*|*batt*";
        }
        if ("charger".equals(target)) {
            return "*chg*|*charger*";
        }
        return "";
    }

    private String thermalZoneCount() {
        String out = firstOutputLine(RootShell.run("count=0; for z in /sys/class/thermal/thermal_zone*; do " +
                "[ -d \"$z\" ] && count=$((count + 1)); done; echo \"$count\""));
        return out.matches("[0-9]+") ? out : "Not detected";
    }

    private String formatTemperature(String raw) {
        if (raw == null || "Not detected".equals(raw) || !raw.trim().matches("-?[0-9]+")) {
            return "Not detected";
        }
        try {
            int value = Integer.parseInt(raw.trim());
            double temp;
            int abs = Math.abs(value);
            if (abs >= 10000) {
                temp = value / 1000.0d;
            } else if (abs >= 100) {
                temp = value / 10.0d;
            } else {
                temp = value;
            }
            return String.format("%.1f C", temp);
        } catch (NumberFormatException e) {
            return "Not detected";
        }
    }

    private ArrayList<String> listWithCurrent(String[] values, String current) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (current != null && current.matches("[a-zA-Z0-9_\\-]+")) {
            set.add(current);
        }
        set.addAll(Arrays.asList(values));
        ArrayList<String> list = new ArrayList<>(set);
        if (list.size() == 0) {
            list.add("Not detected");
        }
        return list;
    }

    private String readFirstKernelLineFromGlob(String nodes) {
        return firstOutputLine(RootShell.run("for f in " + nodes + "; do " +
                "[ -e \"$f\" ] && cat \"$f\" 2>/dev/null && break; done"));
    }

    private String readKernelLine(String node) {
        return firstOutputLine(RootShell.run("[ -e " + shellSingleQuote(node) + " ] && cat " +
                shellSingleQuote(node) + " 2>/dev/null"));
    }

    private String readSysNode(String node) {
        if (node == null || !node.startsWith("/")) {
            return "Not detected";
        }
        return firstOutputLine(RootShell.run("[ -e " + shellSingleQuote(node) + " ] && cat " +
                shellSingleQuote(node) + " 2>/dev/null"));
    }

    private String readFirstExistingNodeLine(String nodes) {
        return firstOutputLine(RootShell.run("for f in " + nodes + "; do " +
                "[ -e \"$f\" ] && cat \"$f\" 2>/dev/null && exit 0; done"));
    }

    private String firstOutputLine(String input) {
        if (isBadShellOutput(input)) {
            return "Not detected";
        }
        return input.trim().split("\\n")[0].trim();
    }

    private String activeKernelOption(String line) {
        if (line == null || "Not detected".equals(line)) {
            return "Not detected";
        }
        int start = line.indexOf('[');
        int end = line.indexOf(']', start + 1);
        if (start >= 0 && end > start) {
            String active = line.substring(start + 1, end).trim();
            return active.matches("[a-zA-Z0-9_\\-]+") ? active : "Not detected";
        }
        ArrayList<String> options = parseKernelOptions(line);
        return options.size() > 0 ? options.get(0) : "Not detected";
    }

    private ArrayList<String> parseKernelOptions(String input) {
        return splitLines(input == null ? "" : input.replace('[', ' ').replace(']', ' '));
    }

    private String currentLedChannel() {
        String pref = getPreferences(MODE_PRIVATE).getString(PREF_LED_CHANNEL, "");
        if (ledChannelExists(pref)) {
            return pref;
        }
        ArrayList<String> channels = getLedChannels();
        if (channels.size() > 0 && !"Not detected".equals(channels.get(0))) {
            getPreferences(MODE_PRIVATE).edit().putString(PREF_LED_CHANNEL, channels.get(0)).apply();
            return channels.get(0);
        }
        return "Not detected";
    }

    private ArrayList<String> getLedChannels() {
        String out = RootShell.run("for d in " + LED_CLASS_PATH + "/*; do " +
                "[ -d \"$d\" ] || continue; n=${d##*/}; " +
                "case \"$n\" in mmc*|*::|*sdio*|*flash*|*torch*|*vibrator*) continue;; esac; " +
                "[ -e \"$d/brightness\" ] && echo \"$n\"; done");
        return splitLedLines(out);
    }

    private void setLedChannel(String channel) {
        if (!ledChannelExists(channel)) {
            toast("LED Channel not detected");
            return;
        }
        getPreferences(MODE_PRIVATE).edit().putString(PREF_LED_CHANNEL, channel).apply();
        toast("LED Channel selected");
    }

    private String ledStateSummary() {
        String channel = currentLedChannel();
        if ("Not detected".equals(channel)) {
            return "Not detected";
        }
        return "Channel: " + channel;
    }

    private boolean isLedEnabled() {
        String value = currentLedBrightness();
        if (!value.matches("[0-9]+")) {
            return false;
        }
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setLedEnabled(boolean enabled) {
        String channel = currentLedChannel();
        if (!ledChannelExists(channel)) {
            toast("LED not detected");
            return;
        }
        if (enabled) {
            if (ledTriggerAvailable("none")) {
                RootShell.run("echo none > " + shellSingleQuote(ledNode(channel, "trigger")) + " 2>/dev/null");
            }
            setLedBrightness(currentLedMaxBrightness());
        } else {
            setLedBrightness("0");
        }
    }

    private String currentLedBrightness() {
        String node = ledNode(currentLedChannel(), "brightness");
        return node.length() == 0 ? "Not detected" : kernelNodeValue(node);
    }

    private String currentLedMaxBrightness() {
        String node = ledNode(currentLedChannel(), "max_brightness");
        String value = node.length() == 0 ? "" : kernelNodeValue(node);
        return isPositiveNumber(value) ? value : "255";
    }

    private void setLedBrightness(String brightness) {
        if (!brightness.matches("[0-9]+")) {
            toast("LED Brightness invalid");
            return;
        }
        String max = currentLedMaxBrightness();
        try {
            int value = Integer.parseInt(brightness);
            int maxValue = Integer.parseInt(max);
            if (value > maxValue) {
                brightness = String.valueOf(maxValue);
            }
        } catch (NumberFormatException ignored) {
        }
        String node = ledNode(currentLedChannel(), "brightness");
        writeKernelNode(node, brightness, "LED Brightness");
    }

    private String currentLedTrigger() {
        String node = ledNode(currentLedChannel(), "trigger");
        return node.length() == 0 ? "Not detected" : activeKernelOption(readKernelLine(node));
    }

    private ArrayList<String> getLedTriggers() {
        String node = ledNode(currentLedChannel(), "trigger");
        if (node.length() == 0) {
            ArrayList<String> values = new ArrayList<>();
            values.add("Not detected");
            return values;
        }
        return parseKernelOptions(readKernelLine(node));
    }

    private void applyLedTrigger(String trigger) {
        if ("Not detected".equals(trigger) || safe(trigger).length() == 0) {
            toast("LED Trigger not detected");
            return;
        }
        String node = ledNode(currentLedChannel(), "trigger");
        writeKernelNode(node, trigger, "LED Trigger");
    }

    private boolean isLedTimerEnabled() {
        return "timer".equals(currentLedTrigger());
    }

    private void setLedTimer(boolean enabled) {
        applyLedTrigger(enabled ? "timer" : "none");
    }

    private String currentLedDelay(String file) {
        String node = ledNode(currentLedChannel(), file);
        if (node.length() > 0 && kernelNodeExists(node)) {
            return kernelNodeValue(node);
        }
        return ledTriggerAvailable("timer") ? "500" : "Not detected";
    }

    private void applyLedDelay(String file, String value) {
        if (!value.matches("[0-9]+")) {
            toast("Blink Delay invalid");
            return;
        }
        String node = ledNode(currentLedChannel(), file);
        if (node.length() == 0) {
            toast("Blink Delay not detected");
            return;
        }
        if (!kernelNodeExists(node) && ledTriggerAvailable("timer")) {
            RootShell.run("echo timer > " + shellSingleQuote(ledNode(currentLedChannel(), "trigger")) + " 2>/dev/null");
        }
        writeKernelNode(node, value, "Blink Delay");
    }

    private boolean ledTriggerAvailable(String trigger) {
        ArrayList<String> triggers = getLedTriggers();
        return triggers.contains(trigger);
    }

    private boolean ledChannelExists(String channel) {
        String node = ledNode(channel, "brightness");
        return node.length() > 0 && kernelNodeExists(node);
    }

    private String ledNode(String channel, String file) {
        String clean = safeLedName(channel);
        if (clean.length() == 0 || file == null || !file.matches("[a-zA-Z0-9_\\-]+")) {
            return "";
        }
        return LED_CLASS_PATH + "/" + clean + "/" + file;
    }

    private String safeLedName(String input) {
        if (input == null || "Not detected".equals(input)) {
            return "";
        }
        return input.matches("[a-zA-Z0-9_:\\-.]+") ? input : "";
    }

    private ArrayList<String> splitLedLines(String input) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (!isBadShellOutput(input)) {
            String[] lines = input.trim().split("\\s+");
            for (String line : Arrays.asList(lines)) {
                if (line.matches("[a-zA-Z0-9_:\\-.]+")) {
                    set.add(line);
                }
            }
        }
        ArrayList<String> list = new ArrayList<>(set);
        if (list.size() == 0) {
            list.add("Not detected");
        }
        return list;
    }

    private ArrayList<String> splitDeviceLines(String input) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (!isBadShellOutput(input)) {
            String[] lines = input.trim().split("\\s+");
            for (String line : Arrays.asList(lines)) {
                if (safeDeviceName(line).length() > 0) {
                    set.add(line.trim());
                }
            }
        }
        ArrayList<String> list = new ArrayList<>(set);
        if (list.size() == 0) {
            list.add("Not detected");
        }
        return list;
    }

    private boolean overlayMonitorEnabled() {
        return getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE)
                .getBoolean(OverlayPrefs.KEY_ENABLED, false);
    }

    private String overlayMonitorSummary() {
        if (!canDrawOverlay()) {
            return "Overlay permission required";
        }
        return overlayMonitorEnabled() ? "Current: Enabled" : "Current: Disabled";
    }

    private void setOverlayMonitorEnabled(boolean enabled) {
        if (enabled && !canDrawOverlay()) {
            getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).edit()
                    .putBoolean(OverlayPrefs.KEY_ENABLED, false)
                    .apply();
            openOverlayPermissionSettings();
            toast("Allow overlay permission first");
            return;
        }
        getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).edit()
                .putBoolean(OverlayPrefs.KEY_ENABLED, enabled)
                .apply();
        if (enabled) {
            requestNotificationPermissionIfNeeded();
            startOverlayMonitorService();
            toast("Overlay Monitor enabled");
        } else {
            stopService(new Intent(this, OverlayMonitorService.class));
            toast("Overlay Monitor disabled");
        }
    }

    private boolean canDrawOverlay() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
    }

    private void openOverlayPermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 920);
        }
    }

    private void startOverlayMonitorService() {
        Intent intent = new Intent(this, OverlayMonitorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private boolean overlayPrefBoolean(String key, boolean fallback) {
        return getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).getBoolean(key, fallback);
    }

    private String overlayPrefString(String key, String fallback) {
        return getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).getString(key, fallback);
    }

    private String overlayPositionValue() {
        if (overlayPrefBoolean(OverlayPrefs.KEY_CUSTOM_POSITION, false)) {
            return OverlayPrefs.POSITION_CUSTOM;
        }
        return overlayPrefString(OverlayPrefs.KEY_POSITION, OverlayPrefs.DEFAULT_POSITION);
    }

    private String overlayPrefIntText(String key, int fallback) {
        return String.valueOf(getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).getInt(key, fallback));
    }

    private void setOverlayPrefBoolean(String key, boolean value) {
        getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).edit().putBoolean(key, value).apply();
        pokeOverlayMonitorService();
    }

    private void setOverlayPrefString(String key, String value) {
        android.content.SharedPreferences.Editor editor =
                getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).edit().putString(key, value);
        if (OverlayPrefs.KEY_POSITION.equals(key)) {
            editor.putBoolean(OverlayPrefs.KEY_CUSTOM_POSITION, false);
            editor.putBoolean(OverlayPrefs.KEY_DRAG_MODE, false);
        }
        editor.apply();
        pokeOverlayMonitorService();
    }

    private void setOverlayDragMode(boolean enabled) {
        getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).edit()
                .putBoolean(OverlayPrefs.KEY_DRAG_MODE, enabled)
                .apply();
        pokeOverlayMonitorService();
        toast(enabled ? "Drag Overlay enabled" : "Touch passthrough enabled");
    }

    private void resetOverlayPosition() {
        getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).edit()
                .putBoolean(OverlayPrefs.KEY_CUSTOM_POSITION, false)
                .putBoolean(OverlayPrefs.KEY_DRAG_MODE, false)
                .remove(OverlayPrefs.KEY_X)
                .remove(OverlayPrefs.KEY_Y)
                .apply();
        pokeOverlayMonitorService();
        toast("Overlay position reset");
    }

    private void setOverlayPrefInt(String key, String value, int min, int max, String label) {
        if (!value.matches("[0-9]+")) {
            toast(label + " invalid");
            return;
        }
        int number;
        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            toast(label + " invalid");
            return;
        }
        if (number < min || number > max) {
            toast(label + " range " + min + "-" + max);
            return;
        }
        getSharedPreferences(OverlayPrefs.PREFS_NAME, MODE_PRIVATE).edit().putInt(key, number).apply();
        pokeOverlayMonitorService();
        toast(label + " applied");
    }

    private void pokeOverlayMonitorService() {
        if (overlayMonitorEnabled() && canDrawOverlay()) {
            startOverlayMonitorService();
        }
    }

    private ArrayList<String> splitLines(String input) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (!isBadShellOutput(input)) {
            String[] lines = input.trim().split("\\s+");
            for (String line : Arrays.asList(lines)) {
                if (line.matches("[a-zA-Z0-9_\\-]+")) {
                    set.add(line);
                }
            }
        }
        ArrayList<String> list = new ArrayList<>(set);
        if (list.size() == 0) {
            list.add("Not detected");
        }
        return list;
    }

    private String firstLine(String input) {
        if (isBadShellOutput(input)) {
            return "Not detected";
        }
        return input.trim().split("\\s+")[0];
    }

    private String displayValue(String raw) {
        if (raw == null || raw.trim().length() == 0) {
            return "Not detected";
        }
        if ("Not detected".equals(raw)) {
            return raw;
        }
        String[] pieces = raw.trim().split("_+");
        StringBuilder builder = new StringBuilder();
        for (String piece : pieces) {
            if (piece.length() == 0) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(titleCasePart(piece));
        }
        return builder.length() == 0 ? raw : builder.toString();
    }

    private String displayRawValue(String raw) {
        if (raw == null || raw.trim().length() == 0) {
            return "Not detected";
        }
        return raw.trim();
    }

    private String titleCasePart(String raw) {
        StringBuilder builder = new StringBuilder();
        boolean upperNext = true;
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '-') {
                builder.append(c);
                upperNext = true;
            } else if (upperNext) {
                builder.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    private boolean isBadShellOutput(String input) {
        if (input == null || input.trim().length() == 0) {
            return true;
        }
        String lower = input.toLowerCase();
        return lower.startsWith("error:")
                || lower.contains("permission denied")
                || lower.contains("not found")
                || lower.contains("inaccessible");
    }

    private String safe(String input) {
        return input.replaceAll("[^a-zA-Z0-9_\\-]", "");
    }

    private String safeNodeFile(String input) {
        if (input == null) {
            return "";
        }
        return input.matches("[a-zA-Z0-9_\\-.]+") ? input : "";
    }

    private String safeDeviceName(String input) {
        if (input == null || "Not detected".equals(input)) {
            return "";
        }
        return input.matches("[a-zA-Z0-9_:\\-.]+") ? input : "";
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private static class UserApp {
        final String label;
        final String packageName;

        UserApp(String label, String packageName) {
            this.label = label;
            this.packageName = packageName;
        }
    }

    private static class StorageHealth {
        final String type;
        final String lifeA;
        final String lifeB;

        StorageHealth(String type, String lifeA, String lifeB) {
            this.type = type;
            this.lifeA = lifeA;
            this.lifeB = lifeB;
        }
    }

    private interface PickerAction {
        void onPick(String value);
    }

    private interface ToggleAction {
        void onChanged(boolean checked);
    }

    private class MiniSwitch extends FrameLayout {
        private final boolean interactive;
        private final View thumb;
        private boolean checked;
        private ToggleAction listener;

        MiniSwitch(boolean interactive) {
            this(interactive, false);
        }

        MiniSwitch(boolean interactive, boolean initialChecked) {
            super(MainActivity.this);
            this.interactive = interactive;
            this.checked = initialChecked;
            setClickable(true);
            setPadding(dp(3), dp(3), dp(3), dp(3));

            thumb = new View(MainActivity.this);
            thumb.setBackground(round(Color.WHITE, dp(10), 0, 0));
            thumb.setElevation(dp(1));
            addView(thumb, new FrameLayout.LayoutParams(dp(20), dp(20)));
            setOnClickListener(v -> {
                if (!this.interactive) {
                    checked = false;
                    updateSwitch();
                    if (listener != null) {
                        listener.onChanged(false);
                    }
                    return;
                }
                checked = !checked;
                updateSwitch();
                if (listener != null) {
                    listener.onChanged(checked);
                }
            });
            updateSwitch();
        }

        void setOnToggleChangedListener(ToggleAction listener) {
            this.listener = listener;
        }

        private void updateSwitch() {
            setBackground(round(checked ? SWITCH_ON : SWITCH_OFF, dp(13), 0, 0));
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(20), dp(20));
            lp.gravity = Gravity.CENTER_VERTICAL | (checked ? Gravity.RIGHT : Gravity.LEFT);
            thumb.setLayoutParams(lp);
        }
    }
}
