import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
//import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.*;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Grapher extends JFrame{

	public static void main(String args[]){
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();

		for(int i=0;i<50;i++){
				x.add(200*Math.cos(i));
				y.add(200*Math.sin(i));
		}

		new Grapher(x, y);
	}

	public Grapher(ArrayList<Double> x, ArrayList<Double> y){
		super("Plot of function: <<FUNCTION INPUT>>");
		
		Graph g = new Graph(x, y);

		setContentPane(g);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize(800, 800);
		
		setVisible(true);
	}
		
	class Graph extends JPanel{
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();
				
		int xrange = (int) Math.round((2*arrayMax(x))+1);
		int yrange = (int) Math.round((2*arrayMax(y))+1);

		public Graph(ArrayList<Double> xpts, ArrayList<Double> ypts){
			x = xpts;
			y = ypts;
			System.out.println(x.size());
		}

		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x.size());

			polyline.moveTo(x.get(0) + Math.abs(arrayMin(x)), y.get(0) + Math.abs(arrayMin(y)));

			for(int i=1;i<x.size();++i){
				polyline.lineTo(x.get(i)+Math.abs(arrayMin(x)), y.get(i)+Math.abs(arrayMin(y)));
			}

			g2.draw(polyline);
		}
		
		public double arrayMax(ArrayList<Double> a){
			double largest=0;

			for(int i=0;i<a.size();i++){
				System.out.println(a.get(i));

				if(a.get(i)>largest){largest=a.get(i);}
			}
			return largest;
		}
		public double arrayMin(ArrayList<Double> a){
			double smallest=0;

			for(int i=0;i<a.size();++i){
				if(a.get(i)<smallest){smallest=a.get(i);}
			}

			return smallest;
		}
		
	}
	
}