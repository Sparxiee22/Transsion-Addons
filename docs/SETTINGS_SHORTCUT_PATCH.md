# Settings Shortcut Patch

This guide adds a **Transsion Add-ons** shortcut inside Transsion Settings. It is useful when Transsion Addons is installed as a hidden system app or priv-app.

Credit: **Sparxiee22**

## Target Activity

Use this explicit component:

```text
com.kurumidev.transsionaddons/.MainActivity
```

Because Settings opens the explicit `targetClass`, Transsion Addons can stay hidden from the launcher.

## Patch `tran_my_devices_info.xml`

Decompile `Settings.apk`, open:

```text
res/xml/tran_my_devices_info.xml
```

Add:

```xml
<Preference
    android:persistent="false"
    android:enabled="true"
    android:title="Transsion Add-ons"
    android:selectable="true"
    android:key="transsion_addons"
    android:summary=""
    android:order="370"
    android:widgetLayout="@layout/tr_widget_ic_next"
    settings:layout="@layout/tr_font_end_summary_preference_addons">
    <intent
        android:targetPackage="com.kurumidev.transsionaddons"
        android:action="android.intent.action.MAIN"
        android:targetClass="com.kurumidev.transsionaddons.MainActivity" />
</Preference>
```

## Add `res/layout/tr_widget_ic_next.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
    android:gravity="center"
    android:layout_width="24.0dip"
    android:layout_height="fill_parent"
    xmlns:android="http://schemas.android.com/apk/res/android">
    <ImageView
        android:layout_gravity="center"
        android:layout_width="22.0dip"
        android:layout_height="22.0dip"
        android:src="@drawable/ic_next"
        android:scaleType="fitCenter"
        android:fillColor="#ffbdbdbd"
        android:fillAlpha="1.0" />
</FrameLayout>
```

## Add `res/drawable/ic_next.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector
    android:height="24.0dip"
    android:width="24.0dip"
    android:autoMirrored="true"
    android:viewportWidth="72.0"
    android:viewportHeight="72.0"
    xmlns:android="http://schemas.android.com/apk/res/android">
    <path
        android:pathData="M 0 0 H 72 V 72 H 0 V 0 Z"
        android:strokeWidth="1.0"
        android:strokeAlpha="0.21616909"
        android:fillAlpha="0.21616909"
        android:fillType="evenOdd" />
    <path
        android:fillColor="#ffa1a1a1"
        android:pathData="M53.4113463,19.5783748 L68.4139256,34.49959 C68.8265545,34.9099812 69.0210733,35.456712 68.9970898,35.9956421 C69.0230163,36.5337259 68.8314401,37.0806604 68.4215575,37.4927781 L68.4139256,37.50041 L68.4139256,37.50041 L53.4113463,52.4216252 C52.6311867,53.1975537 51.3707798,53.1975537 50.5906202,52.4216252 L50.5860744,52.4171041 C49.8088798,51.6441245 49.8054629,50.38746 50.5784425,49.6102654 L50.5860744,49.6026335 L50.5860744,49.6026335 L64.262,36 L50.5860744,22.3973665 C49.8088798,21.6243869 49.8054629,20.3677224 50.5784425,19.5905279 L50.5906202,19.5783748 C51.3707798,18.8024463 52.6311867,18.8024463 53.4113463,19.5783748 Z"
        android:strokeWidth="1.0"
        android:fillAlpha="0.6"
        android:fillType="evenOdd" />
</vector>
```

## Add `res/layout/tr_font_end_summary_preference_addons.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    android:gravity="center_vertical"
    android:background="@drawable/tr_settings_press_primary_bg"
    android:clipToPadding="false"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:baselineAligned="false"
    android:minHeight="@dimen/tr_global_item_min_height"
    android:layout_marginHorizontal="20.0dip"
    android:paddingHorizontal="12.0dip"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <include
        layout="@layout/image_frame" />
    <com.transsion.settings.SameHorizontalTitleSummaryLayout
        android:layout_width="0.0dip"
        android:layout_height="wrap_content"
        android:layout_weight="1.0" />
    <LinearLayout
        android:gravity="center"
        android:orientation="vertical"
        android:id="@android:id/widget_frame"
        android:paddingLeft="0.0dip"
        android:paddingRight="0.0dip"
        android:layout_width="24.0dip"
        android:layout_height="fill_parent"
        android:paddingStart="0.0dip"
        android:paddingEnd="0.0dip" />
</LinearLayout>
```

## Check `res/layout/image_frame.xml`

Make sure it matches:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    android:gravity="start|center"
    android:orientation="horizontal"
    android:id="@id/icon_frame"
    android:paddingLeft="0.0dip"
    android:paddingTop="4.0dip"
    android:paddingRight="8.0dip"
    android:paddingBottom="4.0dip"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:minWidth="56.0dip"
    android:paddingStart="0.0dip"
    android:paddingEnd="8.0dip"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <androidx.preference.internal.PreferenceImageView
        android:id="@android:id/icon"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:maxHeight="48.0dip"
        app:maxWidth="48.0dip" />
</LinearLayout>
```

Recompile and sign `Settings.apk` after patching.
