package ru.healthe.sixleave.ItemLeave;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import ru.healthe.sixleave.Utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class ItemLeave {

    private final Plugin plugin;
    private final Utils utils;
    private static final String IDENTIFIER = "SixLeave.";

    public ItemLeave(Plugin plugin, Utils utils) {
        this.plugin = plugin;
        this.utils = utils;
    }

    public ItemStack createLeave(Player player, String typeLeave, int amount) {
        FileConfiguration config = plugin.getConfig();

        if (config.getConfigurationSection(typeLeave) == null) {
            return null;
        }

        String matName = config.getString(typeLeave + ".material");
        Material material = Material.matchMaterial(matName);
        if (material == null) material = Material.APPLE;

        String name = config.getString(typeLeave + ".name");
        boolean glowing = config.getBoolean(typeLeave + ".glowing");

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(utils.color(name));

            if (glowing) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            List<String> lore = config.getStringList(typeLeave + ".lore").stream()
                    .map(utils::color)
                    .collect(Collectors.toList());

            meta.setLore(lore);

            meta.setLocalizedName(IDENTIFIER + typeLeave);
            item.setItemMeta(meta);
        }

        return item;
    }

    public boolean hasLocalName(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }

        String localName = item.getItemMeta().getLocalizedName();

        if (!localName.startsWith(IDENTIFIER)) {
            return false;
        }
        String typeLeave = localName.replace(IDENTIFIER, "");
        return plugin.getConfig().contains(typeLeave);
    }
}
