package com.github.ericliu.pixelmonsync.data.serializers;

import com.github.ericliu.pixelmonsync.data.DataPack;
import com.github.ericliu.pixelmonsync.data.ISerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PartySerializer implements ISerializer {

    private static final Gson GSON = new Gson();
    private static final Type TYPE = new TypeToken<HashMap<Integer, String>>(){}.getType();

    @Override
    public String serialize(Player player) {
        Map<Integer, String> data = new HashMap<>();
        PartyStorage partyStorage = Pixelmon.storageManager.getParty(player.getUniqueId());
        for (int i = 0; i < 6; i++) {
            Pokemon pokemon = partyStorage.get(i);
            if (pokemon != null){
                data.put(i, pokemon.writeToNBT(new NBTTagCompound()).toString());
            }
        }
        return GSON.toJson(data, TYPE);
    }

    @Override
    public void deserialize(Player player, DataPack dataPack) {
        String jsonData = dataPack.getData(getUniqueName());
        if (jsonData == null) return;
        Map<Integer, String> map = GSON.fromJson(jsonData, TYPE);
        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty(player.getUniqueId());

        for (int i = 0; i < 6; i++) {
            if (map.containsKey(i)){
                try {
                    NBTTagCompound nbtTagCompound = JsonToNBT.getTagFromJson(map.get(i));
                    Pokemon pokemon = Pixelmon.pokemonFactory.create(nbtTagCompound);
                    partyStorage.set(i, pokemon);
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }else {
                partyStorage.set(i, null);
            }
        }
    }

    @Override
    public String getUniqueName() {
        return "party";
    }
}
