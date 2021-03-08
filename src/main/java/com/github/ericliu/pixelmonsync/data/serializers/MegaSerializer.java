package com.github.ericliu.pixelmonsync.data.serializers;

import com.github.ericliu.pixelmonsync.data.DataPack;
import com.github.ericliu.pixelmonsync.data.ISerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.enums.EnumFeatureState;
import com.pixelmonmod.pixelmon.enums.EnumMegaItem;
import com.pixelmonmod.pixelmon.enums.EnumMegaItemsUnlocked;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MegaSerializer implements ISerializer {

    private static final Gson GSON = new Gson();
    private static final Type TYPE = new TypeToken<Map<String, String>>(){}.getType();

    @Override
    public String serialize(Player player) {
        Map<String,String> map = new HashMap<>();
        PlayerPartyStorage playerPartyStorage = Pixelmon.storageManager.getParty(player.getUniqueId());

        EnumMegaItemsUnlocked enumMegaItemsUnlocked = playerPartyStorage.getMegaItemsUnlocked();
        switch (enumMegaItemsUnlocked){
            case Mega:
                map.put("megaItemUnlocked", "mega");
                break;
            case Both:
                map.put("megaItemUnlocked", "both");
                break;
            case None:
                map.put("megaItemUnlocked", "none");
                break;
            case Dynamax:
                map.put("megaItemUnlocked", "dynamax");
                break;
        }

        EnumMegaItem megaItem = playerPartyStorage.getMegaItem();
        switch (megaItem){
            case Disabled:
                map.put("megaItem", "disable");
                break;
            case MegaAnchor:
                map.put("megaItem", "anchor");
                break;
            case MegaGlasses:
                map.put("megaItem", "glasses");
                break;
            case BraceletORAS:
                map.put("megaItem", "oras");
                break;
            case BoostNecklace:
                map.put("megaItem", "necklace");
                break;
            default:
                map.put("megaItem", "none");
                break;
        }

        EnumFeatureState shiny = playerPartyStorage.getShinyCharm();
        switch (shiny){
            case Active:
                map.put("shiny", "active");
                break;
            case Available:
                map.put("shiny", "available");
                break;
            default:
                map.put("shiny", "disable");
                break;
        }
        EnumFeatureState hweenRobe = playerPartyStorage.getHweenRobe();
        switch (hweenRobe){
            case Active:
                map.put("hweenRobe", "active");
                break;
            case Available:
                map.put("hweenRobe", "available");
                break;
            default:
                map.put("hweenRobe", "disable");
                break;
        }

        EnumFeatureState oval = playerPartyStorage.getOvalCharm();
        switch (oval){
            case Active:
                map.put("ovalCharm", "active");
                break;
            case Disabled:
                map.put("ovalCharm", "disable");
                break;
            case Available:
                map.put("ovalCharm", "available");
                break;
        }

        HashMap<EnumSpecies, int[]> speciesHashMap;

        ItemStack itemStack = playerPartyStorage.getLureStack();
        if (itemStack != null){
            map.put("lure", itemStack.writeToNBT(new NBTTagCompound()).toString());
        }

        return GSON.toJson(map, TYPE);
    }

    @Override
    public void deserialize(Player player, DataPack dataPack) {
        PlayerPartyStorage playerPartyStorage = Pixelmon.storageManager.getParty(player.getUniqueId());
        Map<String,String> map = GSON.fromJson(dataPack.getData(getUniqueName()), TYPE);

        String megaItemUnlocked = map.get("megaItemUnlocked");
        if (megaItemUnlocked != null){
            switch (megaItemUnlocked){
                case "mega":
                    playerPartyStorage.setMegaItemsUnlocked(EnumMegaItemsUnlocked.Mega);
                    break;
                case "both":
                    playerPartyStorage.setMegaItemsUnlocked(EnumMegaItemsUnlocked.Both);
                    break;
                case "none":
                    playerPartyStorage.setMegaItemsUnlocked(EnumMegaItemsUnlocked.None);
                    break;
                case "dynamax":
                    playerPartyStorage.setMegaItemsUnlocked(EnumMegaItemsUnlocked.Dynamax);
                    break;
            }
        }

        String megaItem = map.get("megaItem");
        if (megaItem != null){
            switch (megaItem){
                case "disable":
                    playerPartyStorage.setMegaItem(EnumMegaItem.Disabled, false);
                    break;
                case "anchor":
                    playerPartyStorage.setMegaItem(EnumMegaItem.MegaAnchor, false);
                    break;
                case "glasses":
                    playerPartyStorage.setMegaItem(EnumMegaItem.MegaGlasses, false);
                    break;
                case "oras":
                    playerPartyStorage.setMegaItem(EnumMegaItem.BraceletORAS, false);
                    break;
                case "necklace":
                    playerPartyStorage.setMegaItem(EnumMegaItem.BoostNecklace, false);
                    break;
                default:
                    playerPartyStorage.setMegaItem(EnumMegaItem.None, false);
                    break;
            }
        }

        String shiny = map.get("shiny");
        if (shiny != null){
            switch (shiny){
                case "active":
                    playerPartyStorage.setShinyCharm(EnumFeatureState.Active);
                    break;
                case "available":
                    playerPartyStorage.setShinyCharm(EnumFeatureState.Available);
                    break;
                default:
                    playerPartyStorage.setShinyCharm(EnumFeatureState.Disabled);
            }
        }

        String hweenRobe = map.get("hweenRobe");
        EnumFeatureState hweenRobeCurrently = playerPartyStorage.getHweenRobe();
        switch (hweenRobe){
            case "active":
                playerPartyStorage.setHweenRobe(EnumFeatureState.Active);
                if (hweenRobeCurrently != EnumFeatureState.Active){
                    playerPartyStorage.setHasChanged(true);
                }
                break;
            case "available":
                playerPartyStorage.setHweenRobe(EnumFeatureState.Available);
                if (hweenRobeCurrently != EnumFeatureState.Available){
                    playerPartyStorage.setHasChanged(true);
                }
                break;
            default:
                playerPartyStorage.setHweenRobe(EnumFeatureState.Disabled);
                if (hweenRobeCurrently != EnumFeatureState.Disabled){
                    playerPartyStorage.setHasChanged(true);
                }
        }

        String ovalCharm = map.get("ovalCharm");
        switch (ovalCharm){
            case "active":
                playerPartyStorage.setOvalCharm(EnumFeatureState.Active);
                break;
            case "disable":
                playerPartyStorage.setOvalCharm(EnumFeatureState.Disabled);
                break;
            case "available":
                playerPartyStorage.setOvalCharm(EnumFeatureState.Available);
                break;
        }

        if (map.containsKey("lure")){
            ItemStack itemStack = null;
            try {
                itemStack = new ItemStack(JsonToNBT.getTagFromJson(map.get("lure")));
            } catch (NBTException e) {
                e.printStackTrace();
            }
            playerPartyStorage.setLureStack(itemStack);
        }
    }

    @Override
    public String getUniqueName() {
        return "mega";
    }
}
