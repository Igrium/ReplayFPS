package com.igrium.replayfps_viewer.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ChannelGraph {
    
    private final DoubleProperty xScale = new SimpleDoubleProperty(20);
    
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
            double factor =  Math.pow(2, -e.getDeltaY() / 500d);
            if (e.isAltDown()) {
                zoom(factor, 1, e.getX(), e.getY());
            } else if (e.isControlDown()) {
                zoom(1, factor, e.getX(), e.getY());
            } else {
                zoom(factor, factor, e.getX(), e.getY());
            }
            e.consume();
        });

    }

    public void zoom(double xFactor, double yFactor, double centerX, double centerY) {
        if (xFactor <= 0) throw new IllegalArgumentException("X factor must be greater than 0.");
        if (yFactor <= 0) throw new IllegalArgumentException("Y factor must be greater than 0.");
        

        // These calculations happen in arbitrary variables away from the actual values
        // to avoid graph interval recalculation.
        if (xFactor != 1) {
            double oldScale = getXScale();
            double newScale = oldScale * xFactor;
            double xRoot = xAxis.getLowerBound();

            xRoot += centerX * oldScale;
            xRoot -= centerX * newScale;

            xAxis.setLowerBound(xRoot);
            setXScale(newScale);
        }

        if (yFactor != 1) {
            centerY = chart.getHeight() - centerY;
            double oldScale = getYScale();
            double newScale = oldScale * yFactor;
            double yRoot = yAxis.getLowerBound();
            
            yRoot += centerY * oldScale;
            yRoot -= centerY * newScale;

            yAxis.setLowerBound(yRoot);
            setYScale(newScale);
        }
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
        xAxis.setUpperBound(getChartX(chart.getWidth()));
    }

    private void recalculateY() {
        yAxis.setUpperBound(getChartY(chart.getHeight()));
    }

    public double getChartX(double screenX) {
        return screenX * getXScale() + xAxis.getLowerBound();
    }

    public double getChartY(double screenY) {
        return screenY * getYScale() + yAxis.getLowerBound();
    }

    public void setMaxX(double maxX) {
        setXScale((maxX - xAxis.getLowerBound()) / chart.getWidth());
    }

    public void setMaxY(double maxY) {
        setYScale((maxY - yAxis.getLowerBound()) / chart.getHeight());
    }

    public void fitGraph() {
        BoundsFinder bounds = new BoundsFinder().execute();
        if (!bounds.didInit) return;

        xAxis.setLowerBound(bounds.minX);
        yAxis.setLowerBound(bounds.minY);
        setMaxX(bounds.maxX);
        setMaxY(bounds.maxY);
    }

    // The fact that this needs to be a dedicated object is dumb, but the use of the
    // forEach loop won't let minX (etc) be local variables.
    private class BoundsFinder {
        boolean didInit = false;
        double minX;
        double maxX;
        double minY;
        double maxY;

        public BoundsFinder execute() {
            chart.getData().stream().flatMap(s -> s.getData().stream()).forEach(v -> {
                double xVal = v.getXValue().doubleValue();
                double yVal = v.getYValue().doubleValue();
                if (didInit) {
                    if (xVal < minX) minX = xVal;
                    if (xVal > maxX) maxX = xVal;
                    if (yVal < minY) minY = yVal;
                    if (yVal > maxY) maxY = yVal;
                } else {
                    minX = xVal;
                    maxX = xVal;
                    minY = yVal;
                    maxY = yVal;
                    didInit = true;
                }
            });
            return this;
        }
    }
}
