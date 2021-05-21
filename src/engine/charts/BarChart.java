package engine.charts;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class BarChart extends JFrame {

    DefaultCategoryDataset dataset = setDataset();

    public BarChart(String applicationTitle, String chartTitle) {
        super(applicationTitle);

        // based on the dataset we create the chart
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, "Gender", "Amount", dataset, PlotOrientation.VERTICAL, true, true, false);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);

    }


    public DefaultCategoryDataset setDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(5, "Male", "Deer");
        dataset.addValue(5, "Female", "Deer");
        dataset.addValue(5, "Male", "Tiger");
        dataset.addValue(5, "Female", "Tiger");

        JFreeChart chart = ChartFactory.createBarChart("Genders", "Animals & Genders", "Amount", dataset, PlotOrientation.VERTICAL, true, true, false);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);
        return dataset;
    }

    public void resetDataset(int deerMales, int deerFemales, int tigerMales, int tigerFemales){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(deerMales, "Male", "Deer");
        dataset.addValue(deerFemales, "Female", "Deer");
        dataset.addValue(tigerMales, "Male", "Tiger");
        dataset.addValue(tigerFemales, "Female", "Tiger");

        JFreeChart chart = ChartFactory.createBarChart("Genders", "Animals & Genders", "Amount", dataset, PlotOrientation.VERTICAL, true, true, false);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);
    }


}

