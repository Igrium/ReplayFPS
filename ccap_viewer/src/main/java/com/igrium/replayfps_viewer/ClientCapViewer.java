package com.igrium.replayfps_viewer;

import com.igrium.craftfx.application.ApplicationType;
import com.igrium.craftfx.application.CraftApplication;
import com.igrium.replayfps_viewer.ui.ViewerUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.minecraft.client.MinecraftClient;

public class ClientCapViewer extends CraftApplication {

    protected ViewerUI viewerUI;

    public ClientCapViewer(ApplicationType<?> type, MinecraftClient client) {
        super(type, client);
    }

    @Override
    public void start(Stage primaryStage, Application parent) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewerUI.FXML_PATH));
        Parent root = loader.load();
        viewerUI = loader.getController();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    protected void onClosed() {

    }
    
}