package com.github.ericliu.pixelmonsync.handler;

import com.github.ericliu.pixelmonsync.data.DataPack;
import com.github.ericliu.pixelmonsync.data.ISerializer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;

public class SyncHandler {

    public static SyncHandler instance;

    private final Map<String, ISerializer> serializers = new HashMap<>();

    public SyncHandler(){
        instance = this;
    }

    public void register(ISerializer serializer){
        serializers.put(serializer.getUniqueName(), serializer);
    }

    public void unregister(ISerializer serializer){
        serializers.remove(serializer.getUniqueName());
    }

    public void save(Player player){
        DataPack dataPack = new DataPack();
        serializers.values().forEach(serializer -> {
            dataPack.putData(serializer.getUniqueName(), serializer.serialize(player));
        });
        DatabaseManger.instance.save(player, dataPack);
    }

    public void load(Player player){
        DataPack dataPack = DatabaseManger.instance.load(player);
        serializers.values().forEach(serializer -> {
            serializer.deserialize(player, dataPack);
        });
    }
}
