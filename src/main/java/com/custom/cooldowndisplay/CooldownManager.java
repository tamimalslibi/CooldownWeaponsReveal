package com.custom.cooldowndisplay;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    // player UUID -> (weapon key -> cooldown end time in millis)
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    // player UUID -> (weapon key -> active boss bar)
    private final Map<UUID, Map<String, BossBar>> bars = new HashMap<>();

    public void startCooldown(Player player, WeaponConfig weapon) {
        long endTime = System.currentTimeMillis() + (weapon.getCooldownSeconds() * 1000L);
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(weapon.getKey(), endTime);
    }

    public boolean isOnCooldown(Player player, WeaponConfig weapon) {
        Map<String, Long> playerMap = cooldowns.get(player.getUniqueId());
        if (playerMap == null) return false;
        Long endTime = playerMap.get(weapon.getKey());
        return endTime != null && endTime > System.currentTimeMillis();
    }

    public double getSecondsRemaining(Player player, WeaponConfig weapon) {
        Map<String, Long> playerMap = cooldowns.get(player.getUniqueId());
        if (playerMap == null) return 0;
        Long endTime = playerMap.get(weapon.getKey());
        if (endTime == null) return 0;
        long remainingMs = endTime - System.currentTimeMillis();
        return Math.max(0, remainingMs / 1000.0);
    }

    public void clearCooldown(Player player, WeaponConfig weapon) {
        Map<String, Long> playerMap = cooldowns.get(player.getUniqueId());
        if (playerMap != null) playerMap.remove(weapon.getKey());
    }

    public BossBar getOrCreateBar(Player player, WeaponConfig weapon) {
        Map<String, BossBar> playerBars = bars.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        BossBar bar = playerBars.get(weapon.getKey());
        if (bar == null) {
            bar = Bukkit.createBossBar(weapon.getDisplayName(), weapon.getBarColor(), org.bukkit.boss.BarStyle.SOLID);
            bar.addPlayer(player);
            playerBars.put(weapon.getKey(), bar);
        }
        return bar;
    }

    public void removeBar(Player player, WeaponConfig weapon) {
        Map<String, BossBar> playerBars = bars.get(player.getUniqueId());
        if (playerBars == null) return;
        BossBar bar = playerBars.remove(weapon.getKey());
        if (bar != null) {
            bar.removeAll();
        }
    }

    public void removeAllForPlayer(Player player) {
        Map<String, BossBar> playerBars = bars.remove(player.getUniqueId());
        if (playerBars != null) {
            playerBars.values().forEach(BossBar::removeAll);
        }
        cooldowns.remove(player.getUniqueId());
    }
}
