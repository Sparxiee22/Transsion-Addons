package com.kurumidev.transsionaddons;

final class OverlayPrefs {
    static final String PREFS_NAME = "overlay_monitor";

    static final String KEY_ENABLED = "enabled";
    static final String KEY_POSITION = "position";
    static final String KEY_DRAG_MODE = "drag_mode";
    static final String KEY_CUSTOM_POSITION = "custom_position";
    static final String KEY_X = "x";
    static final String KEY_Y = "y";
    static final String KEY_TEXT_SIZE = "text_size";
    static final String KEY_OPACITY = "opacity";
    static final String KEY_SHADOW = "shadow";
    static final String KEY_SHOW_CPU_FREQ = "show_cpu_freq";
    static final String KEY_SHOW_GPU_FREQ = "show_gpu_freq";
    static final String KEY_SHOW_RAM = "show_ram";
    static final String KEY_SHOW_FPS = "show_fps";
    static final String KEY_SHOW_BATTERY_TEMP = "show_battery_temp";
    static final String KEY_SHOW_CPU_TEMP = "show_cpu_temp";
    static final String KEY_SHOW_GPU_TEMP = "show_gpu_temp";
    static final String KEY_SHOW_BATTERY_PERCENT = "show_battery_percent";

    static final String POSITION_TOP_LEFT = "Top Left";
    static final String POSITION_TOP_RIGHT = "Top Right";
    static final String POSITION_BOTTOM_LEFT = "Bottom Left";
    static final String POSITION_BOTTOM_RIGHT = "Bottom Right";
    static final String POSITION_CUSTOM = "Custom";
    static final String[] POSITION_VALUES = {
            POSITION_TOP_LEFT,
            POSITION_TOP_RIGHT,
            POSITION_BOTTOM_LEFT,
            POSITION_BOTTOM_RIGHT
    };

    static final String DEFAULT_POSITION = POSITION_TOP_LEFT;
    static final int DEFAULT_TEXT_SIZE = 12;
    static final int DEFAULT_OPACITY = 72;
    static final boolean DEFAULT_SHADOW = true;

    private OverlayPrefs() {
    }
}
