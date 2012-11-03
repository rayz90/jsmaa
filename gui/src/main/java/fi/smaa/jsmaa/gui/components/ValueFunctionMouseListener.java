package fi.smaa.jsmaa.gui.components;

import java.awt.Component;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

import fi.smaa.jsmaa.model.InvalidValuePointException;
import fi.smaa.jsmaa.model.Point2D;
import fi.smaa.jsmaa.model.ScaleCriterion;

public class ValueFunctionMouseListener implements ChartMouseListener {

	private ChartPanel chartPanel;
	private XYPlot plot;
	private ScaleCriterion crit;
	private Component parent;

	public ValueFunctionMouseListener(ChartPanel chartPanel, ScaleCriterion criterion, Component parent) {
		JFreeChart chart = chartPanel.getChart();
		this.chartPanel = chartPanel;
		plot = chart.getXYPlot();
		this.crit = criterion;
		this.parent = parent;
	}

	@Override
	public void chartMouseMoved(ChartMouseEvent ev) {
		double realX = getRealX(ev);
		double realY = getRealY(ev);
		plot.setDomainCrosshairValue(realX);
		plot.setRangeCrosshairValue(realY);
	}

	@Override
	public void chartMouseClicked(ChartMouseEvent ev) {
		ChartEntity ent = ev.getEntity();
		
		if (ent instanceof XYItemEntity) {
			XYItemEntity xyItemEntity = (XYItemEntity) ent;
			int idx = xyItemEntity.getItem();
			if (idx > 0 && idx < (crit.getValuePoints().size()-1)) {
				crit.deleteValuePoint(idx);
			}
		} else if (ent instanceof PlotEntity) { // add a new point
			double realX = getRealX(ev);
			double realY = getRealY(ev);
			try {
				crit.addValuePoint(new Point2D(realX, realY));
			} catch (InvalidValuePointException e) {
				JOptionPane.showMessageDialog(parent,
						"Cannot add partial value function segments: "+ e.getMessage(), 
						"Unable to add point to the partial value function", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private double getRealY(ChartMouseEvent ev) {
		int relativeY = ev.getTrigger().getY();
		Rectangle2D dataArea = chartPanel.getScreenDataArea();
		RectangleEdge redge = plot.getRangeAxisEdge();
		return plot.getRangeAxis().java2DToValue(relativeY, dataArea, redge);
	}

	private double getRealX(ChartMouseEvent ev) {
		int relativeX = ev.getTrigger().getX();
		Rectangle2D dataArea = chartPanel.getScreenDataArea();		
		RectangleEdge dedge = plot.getDomainAxisEdge();
		return plot.getDomainAxis().java2DToValue(relativeX, dataArea, dedge);
	}
}
