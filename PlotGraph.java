import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
//import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.*;
import java.awt.Dimension;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

import javax.swing.JFrame;
import javax.swing.*;

public class PlotGraph extends JFrame{

	public static void main(String args[]){
		ArrayList<Point2D.Double> pts = new ArrayList<Point2D.Double>();

		int xMinBound = -1;
		int xMaxBound = 10;

		int yMinBound = -1;
		int yMaxBound = 1;

		for(int i=0;i<20;i++){
				Point2D.Double point = new Point2D.Double();

				double x = i;
				double y = Math.sin(i);

				point.setLocation(x, y);
				System.out.println(point.toString());
				pts.add(point);
		}

		Evaluator eval = new Evaluator(args);

		//new PlotGraph(pts, xMinBound, xMaxBound, yMinBound, yMaxBound, "Still nothing here yet.");
	}

	static class Evaluator {
		String expression;

		public Evaluator(String args[]){
			System.out.println(args[0]);
		}

	}

	public PlotGraph(ArrayList<Point2D.Double> pts, int xMinBound, int xMaxBound, int yMinBound, int yMaxBound, String name){
		super("Plot of function: " + name);
		
		Graph g = new Graph(pts, xMinBound, xMaxBound, yMinBound, yMaxBound);

		setContentPane(g);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize((int)g.getXDimension() + 17, (int)g.getYDimension() + 37);
		setResizable(false);
		setVisible(true);
	}
		
	class Graph extends JPanel{
		ArrayList<Point2D.Double> pts = new ArrayList<Point2D.Double>();

		double ratio;

		int xMinBound;
		int xMaxBound;
		int yMinBound;
		int yMaxBound;

		int xLength;
		int yLength;
				
		public Graph(ArrayList<Point2D.Double> ps, int xMinB, int xMaxB, int yMinB, int yMaxB){
			pts = ps;

			xMinBound = xMinB;
			xMaxBound = xMaxB;

			yMinBound = yMinB;
			yMaxBound = yMaxB;

			xLength = (Math.abs(xMinBound) + Math.abs(xMaxBound));
			yLength = (Math.abs(yMinBound) + Math.abs(yMaxBound));

			//For the ratio, we want to plot everything at about 1000x1000 px
			if (xLength > yLength){
				ratio = 500 / xLength;
			} else {
				ratio = 500 / yLength;
			}
			System.out.println(ratio);
		}

		public double getXDimension(){			
			return ratio * xLength;
		}

		public double getYDimension(){
			return ratio * yLength;
		}

		public void paintComponent(Graphics g){
			Graphics2D graphic = (Graphics2D) g;
			GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, pts.size()+3);

			// X Axis
			polyline.moveTo(0, ratio * yMaxBound);
			polyline.lineTo(getXDimension(), ratio * yMaxBound);

			// Y Axis
			polyline.moveTo(ratio * Math.abs(xMinBound), 0);
			polyline.lineTo(ratio * Math.abs(xMinBound), getYDimension());

			// In order to keep the output on the screen, the absolute value of the lowest point is added to the plotter
			polyline.moveTo(ratio * (pts.get(0).getX() + Math.abs(xMinBound)), ratio * (Math.abs(yMaxBound) - pts.get(0).getY()));

			for(int i=1; i<pts.size(); ++i){
				double xpos = pts.get(i).getX() + Math.abs(xMinBound);
				double ypos = Math.abs(yMaxBound) - pts.get(i).getY();

				polyline.lineTo(ratio * xpos, ratio * ypos);
			}

			graphic.draw(polyline);
		}
	}
}