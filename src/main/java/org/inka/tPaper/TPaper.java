package org.inka.tPaper;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.inka.tPaper.CentralTP.CustomTPItem;
import org.inka.tPaper.CentralTP.MainTPItem;
import org.inka.tPaper.commands.TPaperCommands;
import org.inka.tPaper.listeners.CustomTPUse;
import org.inka.tPaper.listeners.DelayedTP;
import org.inka.tPaper.listeners.MainTPUse;
import org.inka.tPaper.teleports.CustomTP;
import org.inka.tPaper.teleports.MaintownTP;

public final class TPaper extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        MainTPItem mainTPItem = new MainTPItem();
        CustomTPItem customTPItem = new CustomTPItem();
        TPaperCommands tPaperCommands = new TPaperCommands(mainTPItem, customTPItem, this);
        DelayedTP delayedTP = new DelayedTP(this);

        getServer().getPluginManager().registerEvents(new MainTPUse(this, new MaintownTP(this), delayedTP), this);
        getServer().getPluginManager().registerEvents(new CustomTPUse(this, new CustomTP(this), delayedTP) , this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralCommandNode<CommandSourceStack> paperNodes = tPaperCommands.createStartCommand().build();
            commands.registrar().register(paperNodes);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
