package engine.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import javax.swing.*;

public class LineChart extends JFrame {

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public LineChart(String applicationTitle, String chartTitle) {
        super(applicationTitle);

        // based on the dataset we create the chart
        JFreeChart chart = ChartFactory.createLineChart(chartTitle, "Time", "Number of Entities", dataset, PlotOrientation.VERTICAL, true, true, false);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);

    }


    public void addToDataset(int deers, int tigers, int minutes, String title) {
        dataset.addValue(deers, "deers", Integer.toString(minutes));
        dataset.addValue(tigers, "tigers", Integer.toString(minutes));


        JFreeChart chart = ChartFactory.createLineChart(title, "Time (minutes)", "Number of Entities", dataset, PlotOrientation.VERTICAL, true, true, false);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);
    }


    public void addToDataset(float deerVal, float tigerVal, int minutes, String metric) {
        dataset.addValue(deerVal, "deers", Integer.toString(minutes));
        dataset.addValue(tigerVal, "tigers", Integer.toString(minutes));


        JFreeChart chart = ChartFactory.createLineChart("Average " + metric, "Time (minutes)", metric, dataset, PlotOrientation.VERTICAL, true, true, false);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);
    }

}
