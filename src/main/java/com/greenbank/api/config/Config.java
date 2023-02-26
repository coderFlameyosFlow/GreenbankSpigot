package com.greenbank.api.config;

import java.io.IOException;
import java.io.File;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;

import org.flameyosflow.greenbank.GreenBankMain;

public class Config {
    private final GreenBankMain plugin;

    public Config(GreenBankMain plugin) {
        this.plugin = plugin;
    }

    public YamlDocument configFile() throws IOException {
        return YamlDocument.create(
                new File(plugin.getDataFolder(), "config.yml"),
                plugin.getConfigYaml("config.yml"),
                GeneralSettings.builder().setSerializer(SpigotSerializer.getInstance()).build(),
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
    }
}
