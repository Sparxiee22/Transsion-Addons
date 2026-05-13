#!/system/bin/sh

ui_print "- Installing Transsion Addons as system priv-app"
ui_print "- Open it from the module action button or Settings integration"

set_perm_recursive "$MODPATH/system" 0 0 0755 0644
set_perm "$MODPATH/action.sh" 0 0 0755
set_perm "$MODPATH/service.sh" 0 0 0755
