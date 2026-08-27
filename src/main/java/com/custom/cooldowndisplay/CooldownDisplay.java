package com.custom.cooldowndisplay;

import org.bukkit.boss.BarColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CooldownDisplay extends JavaPlugin {

    private final CooldownManager cooldownManager = new CooldownManager();
    private final List<WeaponConfig> weapons = new ArrayList<>();
    private boolean requireInHand;
    private DisplayTask displayTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadWeapons();

        getServer().getPluginManager().registerEvents(new ItemListener(this), this);

        int interval = getConfig().getInt("settings.check-interval-ticks", 4);
        displayTask = new DisplayTask(this);
        displayTask.runTaskTimer(this, 0L, interval);

        getLogger().info("CooldownDisplay enabled with " + weapons.size() + " weapon(s) configured.");
    }

    @Override
    public void onDisable() {
        if (displayTask != null) {
            displayTask.cancel();
        }
        for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
            cooldownManager.removeAllForPlayer(player);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            weapons.clear();
            loadWeapons();
            sender.sendMessage("§aCooldownDisplay config reloaded (" + weapons.size() + " weapons loaded).");
            return true;
        }
        sender.sendMessage("§eUsage: /cd reload");
        return true;
    }

    private void loadWeapons() {
        requireInHand = getConfig().getBoolean("settings.require-in-hand", false);

        ConfigurationSection weaponsSection = getConfig().getConfigurationSection("weapons");
        if (weaponsSection == null) {
            getLogger().warning("No 'weapons' section found in config.yml!");
            return;
        }

        for (String key : weaponsSection.getKeys(false)) {
            ConfigurationSection section = weaponsSection.getConfigurationSection(key);
            if (section == null) continue;

            String displayName = section.getString("display-name", key);
            int cooldownSeconds = section.getInt("cooldown-seconds", 10);
            String hexColor = section.getString("color", "#FFFFFF");

            BarColor barColor;
            try {
                barColor = BarColor.valueOf(section.getString("bar-color", "WHITE").toUpperCase());
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid bar-color for weapon '" + key + "', defaulting to WHITE.");
                barColor = BarColor.WHITE;
            }

            weapons.add(new WeaponConfig(key, displayName, cooldownSeconds, hexColor, barColor));
        }
    }

    public List<WeaponConfig> getWeapons() {
        return weapons;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public boolean isRequireInHand() {
        return requireInHand;
    }
}
