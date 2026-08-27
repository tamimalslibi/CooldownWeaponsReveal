package com.custom.cooldowndisplay;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemListener implements Listener {

    private final CooldownDisplay plugin;

    public ItemListener(CooldownDisplay plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Only fire once per interaction (ignore the off-hand duplicate event)
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return;

        String name = getCleanDisplayName(item);
        if (name == null) return;

        for (WeaponConfig weapon : plugin.getWeapons()) {
            if (weapon.getDisplayName().equalsIgnoreCase(name)) {
                if (!plugin.getCooldownManager().isOnCooldown(player, weapon)) {
                    plugin.getCooldownManager().startCooldown(player, weapon);
                }
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getCooldownManager().removeAllForPlayer(event.getPlayer());
    }

    private String getCleanDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;
        return ChatColor.stripColor(meta.getDisplayName());
    }
}
