# Transsion Addons

Transsion Addons is an Android tuning utility for rooted Transsion-family devices and compatible Android devices. It exposes display, kernel, thermal, game space, LED, overlay monitor, charging, and devfreq controls from one lightweight app.

Created by **Sparxiee22**.

## Features

- Display refresh, resolution, debug and renderer controls
- CPU/GPU governor and frequency controls
- Kernel settings for I/O, memory, scheduler, networking and misc nodes
- Thermal monitor and thermal throttling toggle
- Game Space compatibility and runtime controls
- LED backlight controls when the kernel exposes LED nodes
- Floating overlay monitor for FPS, CPU/GPU, RAM, temperatures and battery

## Screenshots Preview

<details>
<summary>Klik untuk lihat semua Screenshots</summary>

![photo_6062316389957051682_w.jpg](screenshots/photo_6062316389957051682_w.jpg)
![photo_6062316389957051683_w.jpg](screenshots/photo_6062316389957051683_w.jpg)
![photo_6062316389957051684_w.jpg](screenshots/photo_6062316389957051684_w.jpg)
![photo_6062316389957051685_w.jpg](screenshots/photo_6062316389957051685_w.jpg)
![photo_6062316389957051686_w.jpg](screenshots/photo_6062316389957051686_w.jpg)
![photo_6062316389957051687_w.jpg](screenshots/photo_6062316389957051687_w.jpg)
![photo_6062316389957051688_w.jpg](screenshots/photo_6062316389957051688_w.jpg)
![photo_6062316389957051689_w.jpg](screenshots/photo_6062316389957051689_w.jpg)
![photo_6062316389957051690_w.jpg](screenshots/photo_6062316389957051690_w.jpg)
![photo_6062316389957051691_w.jpg](screenshots/photo_6062316389957051691_w.jpg)
![photo_6062316389957051692_w.jpg](screenshots/photo_6062316389957051692_w.jpg)
![photo_6062316389957051693_w.jpg](screenshots/photo_6062316389957051693_w.jpg)
![photo_6062316389957051694_w.jpg](screenshots/photo_6062316389957051694_w.jpg)
![photo_6062316389957051695_w.jpg](screenshots/photo_6062316389957051695_w.jpg)
![photo_6062316389957051696_w.jpg](screenshots/photo_6062316389957051696_w.jpg)
![photo_6062316389957051697_w.jpg](screenshots/photo_6062316389957051697_w.jpg)

</details>

## Install Modes

### ROM or Settings-Patched Install

This build has no launcher icon. It is meant to be opened from a Settings entry or an explicit intent:

```java
Intent intent = new Intent();
intent.setClassName(
    "com.kurumidev.transsionaddons",
    "com.kurumidev.transsionaddons.MainActivity"
);
startActivity(intent);
