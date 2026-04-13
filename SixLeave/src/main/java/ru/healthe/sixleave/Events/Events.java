package ru.healthe.sixleave.Events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import ru.healthe.sixleave.ItemLeave.ItemLeave;
import ru.healthe.sixleave.Utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Events implements Listener {

    private final Plugin plugin;
    private final Utils utils;
    private final ItemLeave itemLeave;
    private final Random random = new Random();

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public Events(Plugin plugin, Utils utils, ItemLeave itemLeave) {
        this.plugin = plugin;
        this.utils = utils;
        this.itemLeave = itemLeave;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !itemLeave.hasLocalName(item)) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);

            String key = item.getItemMeta().getLocalizedName().replace("SixLeave.", "");
            Player player = event.getPlayer();

            if (isCooldown(player, key)) {
                long timeLeft = getRemainingCooldown(player, key);
                player.sendMessage(utils.color(plugin.getConfig().getString("message.cooldown").replace("%leave_name%", item.getItemMeta().getDisplayName()).replace("%cooldown%", String.valueOf(timeLeft))));
                return;
            }

            activateLeave(player, key, item);
        }
    }

    private void activateLeave(Player player, String key, ItemStack item) {
        ConfigurationSection settings = plugin.getConfig().getConfigurationSection(key + ".settings");
        if (settings == null) return;

        double height = settings.getDouble("teleport-height", 0);
        double radius = settings.getDouble("teleport-radius", 0);
        double jump = settings.getDouble("teleport-jump", 0);

        Location loc = player.getLocation();
        double offsetX = (random.nextDouble() * 2 - 1) * radius;
        double offsetZ = (random.nextDouble() * 2 - 1) * radius;
        loc.add(offsetX, height, offsetZ);

        if (height > 0 || radius > 0) player.teleport(loc);
        if (jump > 0) player.setVelocity(new Vector(0, jump, 0));

        String soundName = settings.getString("sound");
        if (soundName != null) player.playSound(player.getLocation(), Sound.valueOf(soundName), 1.0f, 1.0f);
        settings.getStringList("message").forEach(msg -> player.sendMessage(utils.color(msg)));

        item.setAmount(item.getAmount() - 1);

        int cooldownTime = settings.getInt("cooldown", 0);
        if (cooldownTime > 0) {
            setCooldown(player, key, cooldownTime);
        }
    }

    private void setCooldown(Player player, String key, int seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(key, System.currentTimeMillis() + (seconds * 1000L));
    }

    private boolean isCooldown(Player player, String key) {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        return playerCooldowns.containsKey(key) && playerCooldowns.get(key) > System.currentTimeMillis();
    }

    private long getRemainingCooldown(Player player, String key) {
        long end = cooldowns.get(player.getUniqueId()).get(key);
        return (end - System.currentTimeMillis()) / 1000;
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (itemLeave.hasLocalName(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }
}
