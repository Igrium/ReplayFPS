package com.igrium.replayfps_viewer.ui;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps.util.NoHeaderException;
import com.igrium.replayfps_viewer.ClientCapViewer;
import com.igrium.replayfps_viewer.LoadedClientCap;
import com.igrium.replayfps_viewer.util.GraphedChannel;
import com.mojang.logging.LogUtils;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
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

    @FXML
    private LineChart<Number, Number> channelGraph;
    @FXML
    private ChannelGraph channelGraphController;

    private LoadedClientCap loadedFile;

    public LoadedClientCap getLoadedFile() {
        return loadedFile;
    }

    @FXML
    protected void initialize() {
        headerViewController.getChannelsTable().getSelectionModel().selectedItemProperty().addListener((prop, oldVal, newVal) -> {
            if (newVal == null) {
                channelGraph.getData().clear();
                return;
            }

            loadChannelGraph(newVal.getIndex());
            if (oldVal == null) {
                channelGraphController.fitGraph();
            }
        });
    }
    
    public void setLoadedFile(@Nullable LoadedClientCap loadedFile) {
        if (this.loadedFile == loadedFile) return;
        this.loadedFile = loadedFile;

        headerViewController.getChannelsTable().getSelectionModel().clearSelection();
        channelGraph.getData().clear();


        if (loadedFile != null) {
            headerViewController.loadHeader(loadedFile.getHeader());
        } else {
            headerViewController.clear();
        }
    }

    private void loadChannelGraph(int index) {
        var channel = loadedFile.getHeader().getChannels().get(index);
        try {
            var seriesList = GraphedChannel.create(loadedFile.getReader(), channel);
            channelGraph.getData().clear();
            channelGraph.getData().addAll(seriesList);
        } catch (NoHeaderException | IOException e) {
            LogUtils.getLogger().error("Error loading channel path.", e);
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

    @FXML
    public void fitGraph() {
        channelGraphController.fitGraph();
    }
}
