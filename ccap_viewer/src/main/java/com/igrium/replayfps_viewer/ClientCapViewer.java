package com.igrium.replayfps_viewer;

import java.io.File;
import java.io.IOException;

import com.igrium.craftfx.application.ApplicationType;
import com.igrium.craftfx.application.CraftApplication;
import com.igrium.replayfps_viewer.ui.MainUI;
import com.mojang.logging.LogUtils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.minecraft.client.MinecraftClient;

public class ClientCapViewer extends CraftApplication {

    protected MainUI mainUI;

    public ClientCapViewer(ApplicationType<?> type, MinecraftClient client) {
        super(type, client);
    }

    @Override
    public void start(Stage primaryStage, Application parent) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MainUI.FXML_PATH));
        Parent root = loader.load();
        mainUI = loader.getController();
        mainUI.setAppInstance(this);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
        
    public MainUI getMainUI() {
        return mainUI;
    }

    protected File currentFile;

    public File getCurrentFile() {
        return currentFile;
    }

    public void loadFile(File file) {
        if (file.equals(currentFile)) return;
        if (mainUI.getLoadedFile() != null) {
            try {
                mainUI.getLoadedFile().close();
            } catch (IOException e) {
                LogUtils.getLogger().error("Error closing file.", e);
            }
        }

        try {
            mainUI.setLoadedFile(file != null ? new LoadedClientCap(file) : null);
        } catch (IOException e) {
            LogUtils.getLogger().error("Unable to load " + file, e);
            mainUI.setLoadedFile(null);
        }
        LogUtils.getLogger().info("Opened file " + file);
    }

    @Override
    protected void onClosed() {
        if (mainUI.getLoadedFile() != null) {
            try {
                mainUI.getLoadedFile().close();
            } catch (IOException e) {
                LogUtils.getLogger().error("Error closing file.", e);
            }
        }
    }
}