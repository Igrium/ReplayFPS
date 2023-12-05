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

    private boolean playClientCap = true;

    /**
     * Whether to use the client-cap system in the first place.
     */
    public boolean shouldPlayClientCap() {
        return playClientCap;
    }

    /**
     * Whether to use the client-cap system in the first place.
     */
    public void setPlayClientCap(boolean playClientCap) {
        this.playClientCap = playClientCap;
    }

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

    private boolean drawHotbar = true;

    public boolean shouldDrawHotbar() {
        return drawHotbar;
    }

    public void setDrawHotbar(boolean drawHotbar) {
        this.drawHotbar = drawHotbar;
    }

    private boolean drawScreens = true;

    public boolean shouldDrawScreens() {
        return drawScreens;
    }

    public void setDrawScreens(boolean drawScreens) {
        this.drawScreens = drawScreens;
    }

    public Screen getScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.replayfps.config"));
                

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.replayfps.general"));
        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("option.replayfps.use_clientcap"), playClientCap)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.replayfps.use_clientcap.tooltip"))
                .setSaveConsumer(val -> this.setPlayClientCap(val))
                .build());
        
        ConfigCategory hud = builder.getOrCreateCategory(Text.translatable("category.replayfps.hud"));
        hud.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("option.replayfps.drawhud"), drawHud)
                .setDefaultValue(false)
                .setTooltip(Text.of("option.replayfps.drawhud.tooltip"))
                .setSaveConsumer(val -> setDrawHud(val))
                .build());
        
        hud.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("option.replayfps.drawhotbar"), drawHotbar)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.replayfps.drawhotbar.tooltip"))
                .setSaveConsumer(val -> setDrawHotbar(val))
                .build());

        hud.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("option.replayfps.drawscreens"), drawScreens)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.replayfps.drawscreens"))
                .setSaveConsumer(val -> setDrawScreens(val))
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
