package com.github.ericliu.pixelmonsync.data;

import org.spongepowered.api.entity.living.player.Player;

public interface ISerializer {

    String serialize(Player player);

    void deserialize(Player player, DataPack dataPack);

    String getUniqueName();

}
