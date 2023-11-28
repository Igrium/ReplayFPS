package com.igrium.replayfps_viewer.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps_viewer.ClientCapViewer;
import com.igrium.replayfps_viewer.LoadedClientCap;
import com.igrium.replayfps_viewer.util.GraphedChannel;
import com.mojang.logging.LogUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.SplitPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.minecraft.util.Util;

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

    private Stage loadingPopup;

    @FXML
    protected void initialize() throws Exception {
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
        if (loadingPopup == null) {
            loadingPopup = LoadingPopup.createLoadingPopup(mainPanel.getScene().getWindow());
        }

        loadingPopup.show();
        CompletableFuture.supplyAsync(() -> {
            try {
                return GraphedChannel.create(loadedFile.getReader(), channel);
            } catch (IOException e) {
                throw new RuntimeException("Unable to load channel", e);
            }
        }, Util.getMainWorkerExecutor()).handleAsync(this::onChannelLoaded, Platform::runLater);
        
    }

    private Object onChannelLoaded(List<Series<Number, Number>> seriesList, Throwable e) {
        if (seriesList != null) {
            channelGraph.getData().clear();
            channelGraph.getData().addAll(seriesList);
        }
        if (e != null) {
            LogUtils.getLogger().error("Error loading channel graph.", e);
        }
        loadingPopup.hide();
        return null;
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
