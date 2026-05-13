#!/system/bin/sh

PKG="com.kurumidev.transsionaddons"

for _ in $(seq 1 60); do
    pm path "$PKG" >/dev/null 2>&1 && break
    sleep 2
done

pm grant "$PKG" android.permission.POST_NOTIFICATIONS >/dev/null 2>&1
pm grant "$PKG" android.permission.WRITE_SECURE_SETTINGS >/dev/null 2>&1
appops set "$PKG" SYSTEM_ALERT_WINDOW allow >/dev/null 2>&1
appops set "$PKG" WRITE_SETTINGS allow >/dev/null 2>&1
appops set "$PKG" GET_USAGE_STATS allow >/dev/null 2>&1
