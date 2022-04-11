package com.github.ericliu.pixelmonsync;

import com.github.ericliu.pixelmonsync.command.Commands;
import com.github.ericliu.pixelmonsync.config.ConfigLoader;
import com.github.ericliu.pixelmonsync.data.serializers.*;
import com.github.ericliu.pixelmonsync.handler.BCHandler;
import com.github.ericliu.pixelmonsync.handler.DatabaseManger;
import com.github.ericliu.pixelmonsync.handler.EventHandler;
import com.github.ericliu.pixelmonsync.handler.SyncHandler;
import com.github.ericliu.pixelmonsync.pref.Reference;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

@Plugin(
        id = Reference.PLUGIN_ID,
        name = Reference.PLUGIN_NAME
)
public class Pixelmonsync {

    public static Pixelmonsync instance;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File dir;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        new ConfigLoader(dir);
        new DatabaseManger();
        new SyncHandler();
        new EventHandler();
        new BCHandler();
        new Commands();

        SyncHandler.instance.register(new PartySerializer());
        SyncHandler.instance.register(new PCSerializer());
        SyncHandler.instance.register(new MegaSerializer());
        SyncHandler.instance.register(new QuestSerializer());
        SyncHandler.instance.register(new MiscSerializer());
        SyncHandler.instance.register(new MoneySerializer());
    }

    @Listener
    public void onStop(GameStoppingServerEvent event){
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            SyncHandler.instance.save(player);
        });
        EventHandler.instance.unregister();
    }
}
