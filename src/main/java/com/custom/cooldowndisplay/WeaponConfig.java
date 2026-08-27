package com.custom.cooldowndisplay;

import org.bukkit.boss.BarColor;

public class WeaponConfig {

    private final String key;
    private final String displayName;
    private final int cooldownSeconds;
    private final String hexColor;
    private final BarColor barColor;

    public WeaponConfig(String key, String displayName, int cooldownSeconds, String hexColor, BarColor barColor) {
        this.key = key;
        this.displayName = displayName;
        this.cooldownSeconds = cooldownSeconds;
        this.hexColor = hexColor;
        this.barColor = barColor;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public String getHexColor() {
        return hexColor;
    }

    public BarColor getBarColor() {
        return barColor;
    }
}
