package com.github.ericliu.pixelmonsync.data.serializers;

import com.github.ericliu.pixelmonsync.data.DataPack;
import com.github.ericliu.pixelmonsync.data.ISerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MiscSerializer implements ISerializer {

    private static final Gson GSON = new Gson();
    private static final Type TYPE = new TypeToken<Map<String, String>>(){}.getType();

    @Override
    public String serialize(Player player) {
        Map<String, String> map = new HashMap<>();
        PlayerPartyStorage playerPartyStorage = Pixelmon.storageManager.getParty(player.getUniqueId());
        String pokedex = playerPartyStorage.pokedex.writeToNBT(new NBTTagCompound()).toString();
        NBTTagCompound playerTag = new NBTTagCompound();
        playerPartyStorage.playerData.writeToNBT(playerTag);
        String playerData = playerTag.toString();
        map.put("pokedex", pokedex);
        map.put("playerData", playerData);
        return GSON.toJson(map, TYPE);
    }

    @Override
    public void deserialize(Player player, DataPack dataPack) {
        String jsonData = dataPack.getData(getUniqueName());
        if (jsonData == null) return;
        Map<String, String> map = GSON.fromJson(jsonData, TYPE);
        PlayerPartyStorage playerPartyStorage = Pixelmon.storageManager.getParty(player.getUniqueId());
        try {
            playerPartyStorage.pokedex.readFromNBT(JsonToNBT.getTagFromJson(map.get("pokedex")));
            playerPartyStorage.playerData.readFromNBT(JsonToNBT.getTagFromJson(map.get("playerData")));
        } catch (NBTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUniqueName() {
        return "misc";
    }
}
