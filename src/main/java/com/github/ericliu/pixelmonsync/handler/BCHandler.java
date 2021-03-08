package com.github.ericliu.pixelmonsync.handler;

import com.github.ericliu.pixelmonsync.Pixelmonsync;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BCHandler {

    public static BCHandler instance;

    private final ChannelBinding.RawDataChannel channel;
    public List<String> cachedList = new ArrayList<>();

    public BCHandler(){
        instance = this;
        channel = Sponge.getChannelRegistrar().getOrCreateRaw(Pixelmonsync.instance, "BungeeCord");
        PSyncListener listener = new PSyncListener();
        channel.addListener(listener);
        getServers();
    }

    public void sendMessage(Player player, String msg){
        channel.sendTo(player, channelBuf -> channelBuf.writeUTF(player.getUniqueId().toString()).writeUTF(msg));
    }

    public void connectToServer(Player player, String server){
        channel.sendTo(player, buf -> buf.writeUTF("Connect").writeUTF(server));
    }

    public void getServers(){
        channel.sendToAll(buf -> buf.writeUTF("GetServers"));
    }

    public static class PSyncListener implements RawDataListener{

        private final ConcurrentHashMap<Predicate<ChannelBuf>, Consumer<ChannelBuf>> map = new ConcurrentHashMap<>();

        @Override
        public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {
            if (data.readUTF().equals("GetServers")){
                instance.cachedList = Arrays.asList(data.readUTF().split(", "));
            }
        }
    }
}
