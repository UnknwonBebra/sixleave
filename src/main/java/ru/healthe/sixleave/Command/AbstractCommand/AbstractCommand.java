package ru.healthe.sixleave.Command.AbstractCommand;

import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    private Plugin plugin;

    public AbstractCommand(Plugin plugin, String command) {
        this.plugin = plugin;
        PluginCommand pluginCommand = plugin.getServer().getPluginCommand(command);
        if(pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    public abstract void command(CommandSender sender, String[] args);
    public abstract List<String> list(CommandSender sender, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        command(commandSender, strings);
        return true;
    }
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return list(sender, args);
    }
}
