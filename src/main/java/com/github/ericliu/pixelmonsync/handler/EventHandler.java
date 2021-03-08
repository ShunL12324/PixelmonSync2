package com.github.ericliu.pixelmonsync.handler;

import com.github.ericliu.pixelmonsync.Pixelmonsync;
import com.github.ericliu.pixelmonsync.config.ConfigLoader;
import com.github.ericliu.pixelmonsync.config.PConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class EventHandler {

    private final PConfig config;
    public static EventHandler instance;

    public EventHandler(){
        instance = this;
        Sponge.getEventManager().registerListeners(Pixelmonsync.instance, this);
        config = ConfigLoader.configLoader.getConfig();
    }

    public void unregister(){
        Sponge.getEventManager().unregisterListeners(this);
    }

    @Listener(order = Order.PRE)
    public void onLeave(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity")Player player){
        if (player.hasPermission("pixelmonsync.base")){
            SyncHandler.instance.save(player);
        }
    }

    @Listener(order = Order.POST)
    public void onJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity")Player player){
        if (!player.hasPermission("pixelmonsync.base")) return;
        Task.builder()
                .execute(()-> SyncHandler.instance.load(player))
                .delay(config.delay, TimeUnit.MILLISECONDS)
                .submit(Pixelmonsync.instance);
    }
}
