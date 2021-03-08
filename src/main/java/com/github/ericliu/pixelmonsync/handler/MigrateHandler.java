package com.github.ericliu.pixelmonsync.handler;

import com.github.ericliu.pixelmonsync.Pixelmonsync;
import com.github.ericliu.pixelmonsync.config.ConfigLoader;
import com.github.ericliu.pixelmonsync.config.PConfig;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.sql.*;
import java.util.UUID;

public class MigrateHandler {

    private final String db;
    private final String table;

    public MigrateHandler(String database, String table){
        this.table = table;
        PConfig config = ConfigLoader.configLoader.getConfig();
        db = "jdbc:mysql://" + config.host + ":" + config.port + "/" + database + "?" + "user=" + config.user + "&password=" + config.passwd;
    }

    public void migrate(){
        try(
                Connection connection = DriverManager.getConnection(db);
                PreparedStatement statement = connection.prepareStatement("select * from " + table)
                ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                NBTTagCompound party = JsonToNBT.getTagFromJson(resultSet.getString("party"));
                NBTTagCompound pc = JsonToNBT.getTagFromJson(resultSet.getString("pc"));
                String uuid = resultSet.getString("uuid");
                party(uuid, party);
                pc(uuid, pc);
            }
        } catch (SQLException | NBTException e) {
            e.printStackTrace();
        }
    }

    private void party(String uuid, NBTTagCompound tagCompound){
        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty(UUID.fromString(uuid));
        if (partyStorage != null){
            partyStorage.readFromNBT(tagCompound);
            partyStorage.updatePartyCache();
            partyStorage.shouldSendUpdates = true;
        }
        Pixelmon.storageManager.getSaveAdapter().save(partyStorage);
    }

    private void pc(String uuid, NBTTagCompound tagCompound){
        PCStorage storage = Pixelmon.storageManager.getPCForPlayer(UUID.fromString(uuid));
        if (storage != null){
            storage.readFromNBT(tagCompound);
            storage.shouldSendUpdates = true;
        }
        Pixelmon.storageManager.getSaveAdapter().save(storage);
    }
}
