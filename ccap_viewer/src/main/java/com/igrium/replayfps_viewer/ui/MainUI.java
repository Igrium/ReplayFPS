package com.igrium.replayfps_viewer.ui;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps_viewer.ClientCapViewer;
import com.igrium.replayfps_viewer.LoadedClientCap;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainUI {
    public static final String FXML_PATH = "/assets/replayfps_viewer/ui/main.fxml";

    @FXML
    private SplitPane mainPanel;
    private ClientCapViewer appInstance;

    @FXML
    private HeaderUI headerViewController;

    public SplitPane getMainPanel() {
        return mainPanel;
    }

    public HeaderUI getHeaderViewController() {
        return headerViewController;
    }

    private LoadedClientCap loadedFile;

    public LoadedClientCap getLoadedFile() {
        return loadedFile;
    }
    
    public void setLoadedFile(@Nullable LoadedClientCap loadedFile) {
        if (this.loadedFile == loadedFile) return;
        this.loadedFile = loadedFile;

        if (loadedFile != null) {
            headerViewController.loadHeader(loadedFile.getHeader());
        } else {
            headerViewController.clear();
        }
    }

    public void setAppInstance(ClientCapViewer appInstance) {
        this.appInstance = appInstance;
    }

    public ClientCapViewer getAppInstance() {
        return appInstance;
    }

    /**
     * Open the file selection screen.
     */
    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open client-cap file");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Client-cap files", ".ccap"));

        File file = fileChooser.showOpenDialog(mainPanel.getScene().getWindow());
        if (file == null) return;

        appInstance.loadFile(file);
    }
}
