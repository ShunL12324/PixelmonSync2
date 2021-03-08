package com.github.ericliu.pixelmonsync.data.serializers;

import com.github.ericliu.pixelmonsync.data.DataPack;
import com.github.ericliu.pixelmonsync.data.ISerializer;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class MoneySerializer implements ISerializer {

    @Override
    public String serialize(Player player) {
        Optional<? extends IPixelmonBankAccount> optionalAccount = Pixelmon.moneyManager.getBankAccount(player.getUniqueId());
        return optionalAccount.map(iPixelmonBankAccount -> String.valueOf(iPixelmonBankAccount.getMoney())).orElse("unknown");
    }



    @Override
    public void deserialize(Player player, DataPack dataPack) {
        String moneyStr = dataPack.getData(getUniqueName());
        if (moneyStr == null || moneyStr.equals("unknown")) return;
        int money = Integer.parseInt(moneyStr);
        Pixelmon.moneyManager.getBankAccount(player.getUniqueId()).ifPresent(account -> account.setMoney(money));
    }

    @Override
    public String getUniqueName() {
        return "money";
    }
}
