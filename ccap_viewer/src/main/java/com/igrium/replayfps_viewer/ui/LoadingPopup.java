package com.igrium.replayfps_viewer.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LoadingPopup {
    public static Stage createLoadingPopup(Window owner) {
        Parent loading;
        try {
            loading = FXMLLoader.load(LoadingPopup.class.getResource("/assets/replayfps_viewer/ui/loading.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(loading);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.setTitle("loading");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        return stage;
    }
}
