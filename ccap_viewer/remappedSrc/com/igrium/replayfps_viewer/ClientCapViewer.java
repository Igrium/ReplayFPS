package com.igrium.replayfps_viewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;

import com.igrium.craftfx.application.ApplicationType;
import com.igrium.craftfx.application.CraftApplication;
import com.igrium.replayfps_viewer.ui.LoadingPopup;
import com.igrium.replayfps_viewer.ui.MainUI;
import com.mojang.logging.LogUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

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

        if (FilenameUtils.getExtension(file.getName()).equals("mcpr")) {
            extractZipAsync(file);
            return;
        }

        try {
            mainUI.setLoadedFile(file != null ? new LoadedClientCap(file) : null);
        } catch (IOException e) {
            LogUtils.getLogger().error("Unable to load " + file, e);
            mainUI.setLoadedFile(null);
        }
        LogUtils.getLogger().info("Opened file " + file);
    }

    private void extractZipAsync(File file) {
        LoadingPopup popup = mainUI.getLoadingPopup();
        popup.getLabel().setText("Extracting client-cap");
        popup.getStage().show();
        CompletableFuture.supplyAsync(() -> {
            try {
                return extractZip(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, Util.getMainWorkerExecutor()).handleAsync((extracted, e) -> {
            popup.getStage().close();
            if (extracted != null) {
                loadFile(extracted);
            }
            if (e != null) {
                LogUtils.getLogger().error("Error extracting client-cap", e);
            }
            return null;
        }, Platform::runLater);
    }


    private File extractZip(File file) throws IOException {
        File dest = File.createTempFile("extracted", ".ccap");
        try (ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
            
            ZipEntry entry;
            boolean success = false;
            while ((entry = in.getNextEntry()) != null) {
                if (entry.getName().equals("client.ccap")) {
                    in.transferTo(out);
                    success = true;
                    break;
                }
                in.closeEntry();
            }

            if (!success) {
                throw new IOException("No 'client.ccap' found!");
            }
        }
        dest.deleteOnExit();
        return dest;
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