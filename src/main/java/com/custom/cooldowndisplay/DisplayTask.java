package com.custom.cooldowndisplay;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class DisplayTask extends BukkitRunnable {

    private final CooldownDisplay plugin;

    public DisplayTask(CooldownDisplay plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        boolean requireInHand = plugin.isRequireInHand();

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (WeaponConfig weapon : plugin.getWeapons()) {

                boolean carrying = requireInHand
                        ? isHeldInHand(player, weapon)
                        : isAnywhereInInventory(player, weapon);

                boolean onCooldown = plugin.getCooldownManager().isOnCooldown(player, weapon);

                if (carrying && onCooldown) {
                    double remaining = plugin.getCooldownManager().getSecondsRemaining(player, weapon);
                    double progress = Math.max(0.0, Math.min(1.0, remaining / weapon.getCooldownSeconds()));

                    BossBar bar = plugin.getCooldownManager().getOrCreateBar(player, weapon);
                    bar.setProgress(progress);

                    ChatColor color = ChatColor.of(weapon.getHexColor());
                    String title = color + weapon.getDisplayName() + ChatColor.GRAY + " - "
                            + color + String.format("%.1fs", remaining);
                    bar.setTitle(title);
                    bar.setVisible(true);
                } else {
                    // hide/remove the bar if it's no longer relevant
                    plugin.getCooldownManager().removeBar(player, weapon);
                }
            }
        }
    }

    private boolean isHeldInHand(Player player, WeaponConfig weapon) {
        return matches(player.getInventory().getItemInMainHand(), weapon)
                || matches(player.getInventory().getItemInOffHand(), weapon);
    }

    private boolean isAnywhereInInventory(Player player, WeaponConfig weapon) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (matches(item, weapon)) return true;
        }
        return matches(player.getInventory().getItemInOffHand(), weapon);
    }

    private boolean matches(ItemStack item, WeaponConfig weapon) {
        if (item == null || item.getType().isAir()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        String clean = org.bukkit.ChatColor.stripColor(meta.getDisplayName());
        return weapon.getDisplayName().equalsIgnoreCase(clean);
    }
}
