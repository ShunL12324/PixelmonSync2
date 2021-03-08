package com.github.ericliu.pixelmonsync.data.serializers;

import com.github.ericliu.pixelmonsync.data.DataPack;
import com.github.ericliu.pixelmonsync.data.ISerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PCSerializer implements ISerializer {

    private final static Gson GSON = new Gson();
    private final static Type TYPE = new TypeToken<HashMap<String, String>>(){}.getType();

    @Override
    public String serialize(Player player) {
        Map<String, String> map = new HashMap<>();
        PCStorage pcStorage = Pixelmon.storageManager.getPCForPlayer(player.getUniqueId());
        for (int i = 0; i < pcStorage.getBoxCount(); i++) {
            for (int j = 0; j < 30; j++) {
                Pokemon pokemon = pcStorage.get(i, j);
                if (pokemon != null){
                    map.put(i + "," + j, pokemon.writeToNBT(new NBTTagCompound()).toString());
                }
            }
        }
        return GSON.toJson(map, TYPE);
    }

    @Override
    public void deserialize(Player player, DataPack dataPack) {
        String jsonData = dataPack.getData(getUniqueName());
        if (jsonData == null) return;
        Map<String, String> map = GSON.fromJson(jsonData, TYPE);
        PCStorage pcStorage = Pixelmon.storageManager.getPCForPlayer(player.getUniqueId());
        for (int i = 0; i < pcStorage.getBoxCount(); i++) {
            for (int j = 0; j < 30; j++) {
                String posStr = i + "," + j;
                if (map.containsKey(posStr)){
                    try {
                        Pokemon pokemon = Pixelmon.pokemonFactory.create(JsonToNBT.getTagFromJson(map.get(posStr)));
                        pcStorage.set(i, j, pokemon);
                    } catch (NBTException e) {
                        e.printStackTrace();
                    }
                }else {
                    pcStorage.set(i, j, null);
                }
            }
        }
    }

    @Override
    public String getUniqueName() {
        return "pc";
    }
}
