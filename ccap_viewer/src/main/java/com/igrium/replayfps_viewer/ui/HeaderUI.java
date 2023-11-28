package com.igrium.replayfps_viewer.ui;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.channel.handler.ChannelHandlers;
import com.igrium.replayfps.recording.ClientCapHeader;

import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import net.minecraft.util.Identifier;

public class HeaderUI {

    @FXML
    private TextField framerateField;

    @FXML
    private TextField framerateBaseField;

    @FXML
    private TextField finalFramerateField;

    @FXML
    private TextField playerIDField;

    @FXML
    private TitledPane channelsPane;

    @FXML
    private TableView<ChannelEntry> channelsTable;

    public TableView<ChannelEntry> getChannelsTable() {
        return channelsTable;
    }

    @FXML
    public void initialize() {
        setColumnName(0, "index");
        setColumnName(1, "id");
        setColumnName(2, "type");
        setColumnName(3, "length");

        channelsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setColumnName(int index, String name) {
        channelsTable.getColumns().get(index).setCellValueFactory(new PropertyValueFactory<>(name));
    }

    public void loadHeader(ClientCapHeader header) {
        if (header == null) {
            clear();
            return;
        }

        framerateField.setText(Integer.toString(header.getFramerate()));
        framerateBaseField.setText(Integer.toString(header.getFramerateBase()));
        finalFramerateField.setText(Float.toString(header.getFramerateFloat()));
        playerIDField.setText(Integer.toString(header.getLocalPlayerID()));

        channelsPane.setText("Channels (%d)".formatted(header.numChannels()));

        channelsTable.getItems().clear();
        int i = 0;
        for (ChannelHandler<?> handler : header.getChannels()) {
            channelsTable.getItems().add(new ChannelEntry().apply(handler, i));
            i++;
        }
    }

    public void clear() {
        framerateField.clear();
        framerateBaseField.clear();
        finalFramerateField.clear();
        playerIDField.clear();
        channelsTable.getItems().clear();

        channelsPane.setText("Channels");
    }

    public static class ChannelEntry {
        private int index;
        
        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        private int length;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public ChannelEntry apply(ChannelHandler<?> handler, int index) {
            this.index = index;
            
            Identifier id = ChannelHandlers.REGISTRY.inverse().get(handler);
            this.id = id != null ? id.toString() : "";

            this.type = handler.getChannelType().getName();
            this.length = handler.getChannelType().getSize();

            return this;
        }
    }
}
