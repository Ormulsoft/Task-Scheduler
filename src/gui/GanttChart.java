package gui;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class GanttChart<X,Y> extends XYChart<X,Y> {

    public static class ExtraData {

        public long length;
        public String styleClass;
        protected String label;
        public ExtraData(long lengthMs, String styleClass,String label) {
            super();
            this.length = lengthMs;
            this.label = label;
            this.styleClass = styleClass;
        }
        public long getLength() {
            return length;
        }
        public void setLength(long length) {
            this.length = length;
        }
        public String getStyleClass() {
            return styleClass;
        }
        public void setStyleClass(String styleClass) {
            this.styleClass = styleClass;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }



    }

    private double blockHeight = 10;

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    private static String getStyleClass( Object obj) {
        return ((ExtraData) obj).getStyleClass();
    }

    private static double getLength( Object obj) {
        return ((ExtraData) obj).getLength();
    }
    private static String getLabel(Object obj) {
        return ((ExtraData) obj).getLabel();
}

    @Override
    protected void layoutPlotChildren() {
        getData().forEach(e -> getDisplayedDataIterator(e).forEachRemaining(n -> {
            double x, y;
            x = getXAxis().getDisplayPosition(n.getXValue());
            y = getYAxis().getDisplayPosition(n.getYValue());
            if (Double.isNaN(x) || Double.isNaN(y)) return;
            Node block = n.getNode();
            Rectangle rect;
             final String label = getLabel(n.getExtraValue());
            Text text = new Text(label);
            text.setFont(Font.font("Courier New", FontWeight.EXTRA_BOLD, 20));
            text.setTranslateX(x);
            text.setTranslateY(getBlockHeight());
            text.setBoundsType(TextBoundsType.VISUAL);
            if (block != null) {
                if (block instanceof StackPane) {
                    StackPane region = (StackPane) block;
                    if (region.getShape() == null) {
                        rect = new Rectangle(getLength(n.getExtraValue()), getBlockHeight());
                    } else if (region.getShape() instanceof Rectangle) {
                        rect = (Rectangle) region.getShape();
                    } else {
                        return;
                    }
                    text.setTranslateX(getLength(n.getExtraValue()) * 0.5d);
                    text.setTranslateY(getBlockHeight() * 0.09d);
                    if (!region.getChildren().contains(rect) && !region.getChildren().contains(text)) {
                        region.getChildren().addAll(text);
                    }
                    rect.setWidth(getLength(n.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getXAxis()).getScale()) : 1));
                    rect.setHeight((getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getYAxis()).getScale()) : 1)) * (1d / 6d));
                    y = y - getBlockHeight() * 0.41d;
                    region.setStyle(getStyleClass(n.getExtraValue()).toString());
                    region.setShape(null);
                    region.setShape(rect);
                    region.setScaleShape(false);
                    region.setCenterShape(false);
                    region.setCacheShape(false);
                    region.setTranslateY(getBlockHeight() * 0.35d);
                    block.setLayoutX(x);
                    block.setLayoutY(y);
                }
            }
        }));

}

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight( double blockHeight) {
        this.blockHeight = blockHeight;
    }

    @Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    @Override protected  void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node block = item.getNode();
            getPlotChildren().remove(block);
            removeDataItemFromDisplay(series, item);
    }

    @Override protected void dataItemChanged(Data<X, Y> item) {
    }

    @Override protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    @Override protected  void seriesRemoved(final Series<X,Y> series) {
        for (XYChart.Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }


    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {

        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add( getStyleClass( item.getExtraValue()));

        return container;
    }

    @Override protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<X>();
        if(ya.isAutoRanging()) yData = new ArrayList<Y>();
        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                    if(yData != null){
                        yData.add(data.getYValue());
                    }
                }
            }
            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

}