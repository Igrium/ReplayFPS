package com.igrium.replayfps_viewer.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LoadingPopup {

    @FXML
    private Label label;

    public Label getLabel() {
        return label;
    }

    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public static LoadingPopup createLoadingPopup(Window owner) {
        FXMLLoader loader = new FXMLLoader(LoadingPopup.class.getResource("/assets/replayfps_viewer/ui/loading.fxml"));

        Parent loading;
        try {
            loading = loader.load();
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

        LoadingPopup controller = loader.getController();
        controller.stage = stage;

        return controller;
    }


}
