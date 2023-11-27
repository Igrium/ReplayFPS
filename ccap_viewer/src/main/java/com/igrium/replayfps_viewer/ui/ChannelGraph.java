package com.igrium.replayfps_viewer.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ChannelGraph {

    private final DoubleProperty xScale = new SimpleDoubleProperty(5);
    
    public double getXScale() {
        return xScale.get();
    }

    public void setXScale(double xScale) {
        this.xScale.set(xScale);
    }

    public DoubleProperty xScaleProperty() {
        return xScale;
    }

    private final DoubleProperty yScale = new SimpleDoubleProperty(2);

    public double getYScale() {
        return yScale.get();
    }

    public void setYScale(double yScale) {
        this.yScale.set(yScale);
    }

    public DoubleProperty yScaleProperty() {
        return yScale;
    }

    @FXML
    private LineChart<Number, Number> chart;

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    @FXML
    private NumberAxis xAxis;

    public NumberAxis getXAxis() {
        return xAxis;
    }

    @FXML
    private NumberAxis yAxis;

    public NumberAxis getYAxis() {
        return yAxis;
    }

    private double lastMouseX;
    private double lastMouseY;

    @FXML
    private void initialize() {
        xScale.addListener((prop, oldVal, newVal) -> recalculateX());
        yScale.addListener((prop, oldVal, newVal) -> recalculateY());

        xAxis.lowerBoundProperty().addListener((prop, oldVal, newVal) -> recalculateX());
        yAxis.lowerBoundProperty().addListener((prop, xAxis, yAxis) -> recalculateY());

        recalculateX();
        recalculateY();

        chart.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            e.consume();
        });
        
        chart.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            double deltaX = e.getX() - lastMouseX;
            double deltaY = e.getY() - lastMouseY;
            lastMouseX = e.getX();
            lastMouseY = e.getY();

            // This was not a continuous move.
            if (deltaX > 64) deltaX = 0;
            if (deltaY > 64) deltaY = 0;

            translateX(-deltaX * getXScale());
            translateY(deltaY * getYScale());
            e.consume();
        });

        chart.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isAltDown()) {
                setXScale(getXScale() * Math.pow(2, -e.getDeltaY() / 1000));
            } else {
                setXScale(getXScale() * Math.pow(2, -e.getDeltaX() / 1000));
                setYScale(getYScale() * Math.pow(2, -e.getDeltaY() / 1000));
            }
            e.consume();
        });

        // BOGUS TESTING DATA
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        addData(series, 1970, 15);
        addData(series, 1980, 30);
        addData(series, 1990, 60);
        addData(series, 2000, 120);
        addData(series, 2014, 300);
        addData(series, 2013, 240);
        chart.getData().add(series);

    }

    private void addData(XYChart.Series<Number, Number> series, Number first, Number second) {
        series.getData().add(new XYChart.Data<>(first, second));
    }
    
    private void translateX(double amount) {
        if (amount == 0) return;
        xAxis.setLowerBound(xAxis.getLowerBound() + amount);
    }

    private void translateY(double amount) {
        if (amount == 0) return;
        yAxis.setLowerBound(yAxis.getLowerBound() + amount);
    }

    private void recalculateX() {
        xAxis.setUpperBound(chart.getWidth() * getXScale() + xAxis.getLowerBound());
    }

    private void recalculateY() {
        yAxis.setUpperBound(chart.getHeight() * getYScale() + yAxis.getLowerBound());
    }
}
