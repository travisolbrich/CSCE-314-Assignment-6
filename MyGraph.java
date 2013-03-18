import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
//import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.*;

import java.lang.Math;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyGraph extends JFrame{

	public static void main(String args[]){
		new MyGraph();
	}

	public MyGraph(){
		super("My Graph");
		
		Graph g = new Graph();
		setContentPane(g);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize(800, 800);
		
		setVisible(true);
	}
		
	class Graph extends JPanel{
		private final int TOT_POINTS = 500;

		private double x[]=new double[TOT_POINTS];
		private double y[]=new double[TOT_POINTS];
		
		int tlow=0;
		int thigh=50;
		int t_range=thigh-tlow;
		double smallx;
		double smally;
		double largex;
		double largey;
		
		int xrange = (int) Math.round((2*largex)+1);
		int yrange = (int) Math.round((2*largey)+1);
		
		public void paintComponent(Graphics g){
			Graph graph=new Graph();
			graph.getPoints();
			Graphics2D g2=(Graphics2D) g;
			GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, t_range);
			polyline.moveTo(graph.x[0]+Math.abs(graph.smallx), graph.y[0]+Math.abs(graph.smally));
			for(int i=1;i<t_range;++i){
				polyline.lineTo(graph.x[i]+Math.abs(graph.smallx), graph.y[i]+Math.abs(graph.smally));
			}
			g2.draw(polyline);
		}
		
		public void getPoints(){
			for(int i=0;i<t_range;++i){
				x[i]=200*Math.cos(i);
				y[i]=200*Math.sin(i);
			}
			smallx=findSmallest(x);
			smally=findSmallest(y);
			largex=findLargest(x);
			largey=findLargest(y);
		}
		
		public double findLargest(double a[]){
			double largest=0;
			for(int i=0;i<t_range;++i){
				if(a[i]>largest){largest=a[i];}
			}
			return largest;
		}
		public double findSmallest(double a[]){
			double smallest=0;
			for(int i=0;i<t_range;++i){
				if(a[i]<smallest){smallest=a[i];}
			}
			return smallest;
		}
		
	}
	
}