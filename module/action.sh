#!/system/bin/sh

PKG="com.kurumidev.transsionaddons"
ACTIVITY="com.kurumidev.transsionaddons/.MainActivity"
ACTION="com.kurumidev.transsionaddons.OPEN"

am start -a "$ACTION" >/dev/null 2>&1 || am start -n "$ACTIVITY" >/dev/null 2>&1
