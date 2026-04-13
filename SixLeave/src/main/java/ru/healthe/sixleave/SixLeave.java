package ru.healthe.sixleave;

import org.bukkit.plugin.java.JavaPlugin;
import ru.healthe.sixleave.Command.Command;
import ru.healthe.sixleave.Events.Events;
import ru.healthe.sixleave.ItemLeave.ItemLeave;
import ru.healthe.sixleave.Utils.Utils;

public final class SixLeave extends JavaPlugin {

    private Utils utils;
    private ItemLeave itemLeave;
    private Command command;
    private Events events;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.utils = new Utils();
        this.itemLeave = new ItemLeave(this, utils);
        this.command = new Command(this, utils, itemLeave);
        this.events = new Events(this, utils, itemLeave);
        getServer().getPluginManager().registerEvents(events, this);
    }

    @Override
    public void onDisable() {

    }
}
