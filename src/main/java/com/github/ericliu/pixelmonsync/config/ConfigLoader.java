package com.github.ericliu.pixelmonsync.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    public static ConfigLoader configLoader;
    private File setting;
    private PConfig config;

    public ConfigLoader(File dir){
        configLoader = this;
        this.createDir(dir);
        this.load();
    }

    private void createDir(File dir){
        if (!dir.exists()){
            dir.mkdir();
        }
        setting = new File(dir, "psync.conf");
    }

    private void load(){
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder()
                .setFile(setting)
                .setDefaultOptions(ConfigurationOptions.defaults().withShouldCopyDefaults(true))
                .build();
        try {
            CommentedConfigurationNode node = loader.load();
            config = node.getValue(TypeToken.of(PConfig.class), new PConfig());
            loader.save(node);
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public PConfig getConfig() {
        return config;
    }
}
