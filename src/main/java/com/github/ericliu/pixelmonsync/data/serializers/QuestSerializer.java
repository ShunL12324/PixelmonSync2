package com.github.ericliu.pixelmonsync.data.serializers;

import com.github.ericliu.pixelmonsync.data.DataPack;
import com.github.ericliu.pixelmonsync.data.ISerializer;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

public class QuestSerializer implements ISerializer {

    @Override
    public String serialize(Player player) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        Pixelmon.storageManager.getParty(player.getUniqueId()).getQuestData().writeToNBT(nbtTagCompound);
        return nbtTagCompound.toString();
    }

    @Override
    public void deserialize(Player player, DataPack dataPack) {
        String data = dataPack.getData(getUniqueName());
        if (data == null) return;
        try {
            NBTTagCompound nbtTagCompound = JsonToNBT.getTagFromJson(data);
            Pixelmon.storageManager.getParty(player.getUniqueId()).getQuestData().readFromNBT(nbtTagCompound);
        } catch (NBTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUniqueName() {
        return "quest";
    }
}
