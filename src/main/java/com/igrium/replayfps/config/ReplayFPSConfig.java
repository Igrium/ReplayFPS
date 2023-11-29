package com.igrium.replayfps.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.igrium.replayfps.ReplayFPS;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ReplayFPSConfig {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final String CONFIG_FILE = "config/replayfps.json";

    private boolean drawHud = false;

    /**
     * Whether the HUD will be rendered in first-person replays.
     */
    public boolean shouldDrawHud() {
        return drawHud;
    }

    /**
     * Whether the HUD will be rendered in first-person replays.
     */
    public void setDrawHud(boolean drawHud) {
        this.drawHud = drawHud;
    }

    public Screen getScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.replayfps.config"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.replayfps.general"));

        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("option.replayfps.drawhud.tooltip"), drawHud)
                .setDefaultValue(false)
                .setTooltip(Text.of(""))
                .setSaveConsumer(val -> setDrawHud(val))
                .build());
        
        builder.setSavingRunnable(this::save);
        
        return builder.build();
    }

    public static ReplayFPSConfig load() {
        MinecraftClient client = MinecraftClient.getInstance();
        File configFile = new File(client.runDirectory, CONFIG_FILE);

        if (configFile.exists()) {
            try(BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                return gson.fromJson(reader, ReplayFPSConfig.class);
            } catch (Exception e) {
                ReplayFPS.LOGGER.error("Unable to load Replay FPS config!", e);
            }
        }

        return new ReplayFPSConfig();
    }

    public void save() {
        MinecraftClient client = MinecraftClient.getInstance();
        File configFile = new File(client.runDirectory, CONFIG_FILE);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(gson.toJson(this));
        } catch (Exception e) {
            ReplayFPS.LOGGER.error("Error saving Replay FPS config!", e);
        }

        ReplayFPS.LOGGER.info("Saved config to " + configFile);
    }
    
}
