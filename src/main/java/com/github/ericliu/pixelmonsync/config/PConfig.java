package com.github.ericliu.pixelmonsync.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class PConfig {

    @Setting(value = "host")
    public String host = "127.0.0.1";

    @Setting
    public int port = 3306;

    @Setting
    public String user = "root";

    @Setting
    public String passwd = "932065";

    @Setting
    public String database = "sync";

    @Setting
    public String table = "PixelmonSync";

    @Setting
    public String serverName = "Server1";

    @Setting(comment = "milliseconds")
    public int delay = 500;

}
