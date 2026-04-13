package ru.healthe.sixleave.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.healthe.sixleave.Command.AbstractCommand.AbstractCommand;
import ru.healthe.sixleave.ItemLeave.ItemLeave;
import ru.healthe.sixleave.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Command extends AbstractCommand {

    private final Plugin plugin;
    private final Utils utils;
    private final ItemLeave itemLeave;

    public Command(Plugin plugin, Utils utils, ItemLeave itemLeave) {
        super(plugin, "SixLeave");
        this.plugin = plugin;
        this.utils = utils;
        this.itemLeave = itemLeave;
    }

    @Override
    public void command(CommandSender sender, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (!sender.hasPermission("SixLeave.*")) {
            sender.sendMessage(utils.color(config.getString("message.permission")));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(utils.color("&cИспользуйте: /sixleave give <игрок> <тип> <кол-во>"));
            return;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 4) {
                sender.sendMessage(utils.color("&cНедостаточно аргументов!"));
                return;
            }

            String name = args[1];
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                sender.sendMessage(utils.color(config.getString("message.player-null").replace("%player%", name)));
                return;
            }

            String leaveType = args[2];
            if (config.getConfigurationSection(leaveType) == null) {
                sender.sendMessage(utils.color(config.getString("message.leave-null", "&cТип %leave% не найден").replace("%leave%", leaveType)));
                return;
            }

            try {
                int amount = Integer.parseInt(args[3]);
                ItemStack leave = itemLeave.createLeave(player, leaveType, amount);
                player.getInventory().addItem(leave);

                sender.sendMessage(utils.color(config.getString("message.give")
                        .replace("%player%", name)
                        .replace("%leave%", leaveType)
                        .replace("%amount%", String.valueOf(amount))));
            } catch (NumberFormatException e) {
                sender.sendMessage(utils.color("&cКоличество должно быть числом!"));
            }
        }
    }

    @Override
    public List<String> list(CommandSender sender, String[] args) {
        if (!sender.hasPermission("SixLeave.*")) return Collections.emptyList();

        if (args.length == 1) {
            return Collections.singletonList("give");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return new ArrayList<>(plugin.getConfig().getKeys(false)).stream()
                    .filter(key -> !key.equals("message"))
                    .filter(key -> key.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
