package chart;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

public class ChartDisplayer {
    private final LineChart chart;
    public ChartDisplayer(String applicationTitle,
                          String chartTitle,
                          String xAxisTitle,
                          String yAxisTitle,
                          DefaultCategoryDataset dataset) {
        chart = new LineChart(applicationTitle, chartTitle, xAxisTitle, yAxisTitle, dataset);
    }

    public void display() {
        new Thread(() -> {
            chart.pack();
            RefineryUtilities.centerFrameOnScreen(chart);
            chart.setVisible(true);
        }).start();
    }
}
