package com.igrium.replayfps_viewer.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.playback.ClientCapReader;
import com.igrium.replayfps.playback.UnserializedFrame;
import com.igrium.replayfps.recording.ClientCapHeader;
import com.igrium.replayfps.util.AnimationUtils;
import com.igrium.replayfps.util.NoHeaderException;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class GraphedChannel {
    public static <T> List<Series<Number, Number>> create(ClientCapReader reader, ChannelHandler<T> channel) throws NoHeaderException, IOException {
        List<Series<Number, Number>> data = new ArrayList<>();
        ClientCapHeader header = reader.getHeader();
        reader.seek(0);

        UnserializedFrame first = reader.readFrame();
        T firstVal = first.getValue(channel);

        // Initialize series objects using first frame.
        for (float val : channel.getChannelType().getRawValues(firstVal)) {
            Series<Number, Number> series = new Series<>();
            series.getData().add(new XYChart.Data<>(0, val));
        }
        
        if (data.isEmpty()) return data;

        // Read all frames.
        int frameNum = 1;
        while (!reader.isEndOfFile()) {
            UnserializedFrame frame = reader.readFrame();
            T value = frame.getValue(channel);

            if (value == null) {
                frameNum++;
                continue;
            }

            long timestamp = AnimationUtils.getDuration(frameNum, header.getFramerate(), header.getFramerateBase());
            int i = 0;
            for (float val : channel.getChannelType().getRawValues(value)) {
                data.get(i).getData().add(new XYChart.Data<>(timestamp, val));
                i++;
            }
            frameNum++;
        }

        return data;
    }

}
