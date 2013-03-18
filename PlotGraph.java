import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.geom.*;
import java.awt.Dimension;

import java.lang.Math;
import java.util.*;

import javax.swing.*;

/*
	Plotting
	All numbers are in radians
	Negative numbers should be input as (0 - 1)
	The bounds are inclusive

	Supported operators: + - * / ^
	Supported functions: sin() cos() tan() 
						 asin() acos() atan()
						 sinh() cosh() tanh()
						 e() log() sqrt() abs()

	Other functions can very easily be added in getValue()

	Work Done: Travis Olbrich:  Evaluator Class
			   Brooke Griffith: Plotting 
*/

public class PlotGraph extends JFrame{

	public static void main(String args[]){
		ArrayList<Point2D.Double> pts = new ArrayList<Point2D.Double>();

		Evaluator eval = new Evaluator(args);

		// Min and max bounds for plotting
		int xMinBound = eval.getMinBound(args[1]);
		int xMaxBound = eval.getMaxBound(args[1]);

		int yMinBound = eval.getMinBound(args[2]);
		int yMaxBound = eval.getMaxBound(args[2]);

		//x and y coordinates corresponding with the tVals
		ArrayList<Double> xVals = eval.getXVals();
		ArrayList<Double> yVals = eval.getYVals();

		//System.out.println("Points to Plot");

		for(int i=0; i<xVals.size() ;i++){
			Point2D.Double point = new Point2D.Double();

			point.setLocation(xVals.get(i), yVals.get(i));
			
			//System.out.println(point.toString());
			pts.add(point);
		}


		new PlotGraph(pts, xMinBound, xMaxBound, yMinBound, yMaxBound, "Function Plot.");
	}

	/*
		Evaluator Class
		Travis Olbrich

		Everything needed to evaluate given input. The Shunting Yard algorithm is used to convert to postfix, and 
		then the postfix is evaluated. A ArrayList of Point2D.Double points is produced.
	*/
	static class Evaluator {
		String xPostfix;
		String yPostfix;
		String tBnd;
		ArrayList<Integer> tVals;

		public Evaluator(String args[]){
			String expression = args[0];
			tBnd = args[3];

			// Remove brackets and spaces
			expression = expression.substring(1, expression.length() - 1);
			expression = expression.replaceAll("\\s", "");

			// Split input expression on comma and save in class
			String[] expressionSplit = expression.split(",");

			// Get postfix values strings
			xPostfix = getPostfix(expressionSplit[0]);
			yPostfix = getPostfix(expressionSplit[1]);

			// Get an array of t values to compute against
			tVals = getTVals(tBnd);
		}

		public ArrayList<Double> getXVals(){
			ArrayList<Double> vals = new ArrayList<Double>();

			for(int i = 0; i < tVals.size(); i++){
				vals.add(getValue(xPostfix, tVals.get(i)));
			}

			return vals;
		}

		public ArrayList<Double> getYVals(){
			ArrayList<Double> vals = new ArrayList<Double>();

			for(int i = 0; i < tVals.size(); i++){
				vals.add(getValue(yPostfix, tVals.get(i)));
			}

			return vals;
		}

		// The Shunting Yard algorithm is used to convert to postfix
		// The algorithm on Wikipedia is followed to the letter.
		private String getPostfix(String infix){
			//Replace lone instances of the 't' variable with a '~'. It's easier than anything else
			//This way we ignore it when looking for functions, but it's still inserted as 't' into the postfix
			//I'm bad at regex, but I'm trying to only find 't' when it is not surrounded by letters.
			infix = infix.replaceAll("(^a-aA-A|\\b)t(^a-aA-A|\\b)|(?<=\\d)t", "~");
			//System.out.println(infix);

			char[] chars = infix.toCharArray();
			String postfix = "";
			Stack<String> opstack = new Stack<String>();


			for (char c : chars){
				//Append numerics directly to output, treat 
				if(Character.isDigit(c) || c == '.'){
					postfix += c;				
				}

				else if(c == '~'){
					postfix += " t ";
				}

				//Add a space if needed if we're not adding more numbers to the postfix string
				else {
					postfix += " ";
				}

				//Push functions to the stack
				if(Character.isAlphabetic(c)){
					//Pop the last element, append if a character
					try{
						String pop = opstack.pop();
						if(isAlphaOnly(pop)){
							pop += c;
							opstack.push(pop);
						}
						else {
							opstack.push(pop);

							opstack.push(Character.toString(c));
						}
					} 
					catch (EmptyStackException e) {
						opstack.push(Character.toString(c));
					}					
				}

				//Operators
				if(isOperator(Character.toString(c))){
					try{
						while(isOperator(opstack.peek()) && !opstack.isEmpty()){
							if((isLeftAssociative(Character.toString(c)) && getPrecedence(Character.toString(c)) <= getPrecedence(opstack.peek())) ||
								getPrecedence(Character.toString(c)) < getPrecedence(opstack.peek())){
								String popped = opstack.pop();
								postfix += " " + popped + " ";		
							}
							else{
								break;
							}
						}
					}
					catch (EmptyStackException e) {}

					opstack.push(Character.toString(c));
				}

				//Left Parenth
				if(c == '('){
					opstack.push(Character.toString(c));
				}

				//Right Parenth
				if(c == ')'){
					try{
						while(!opstack.peek().equals("(")){
							postfix += " " + opstack.pop();
						}

					}
					catch (EmptyStackException e) {}
					
					try{
						String leftParen = opstack.pop();
						
						//Pop function to output queue
						if(isAlphaOnly(opstack.peek())){
							postfix += " " + opstack.pop();
						}
					}
					catch (EmptyStackException e) {}
				}

			}

			//Pop anything extra from opstack
			try{
				while(!opstack.isEmpty()){
					postfix += " " + opstack.pop();
				}

			}
			catch (EmptyStackException e) {}

			//Fix double spaces that may have occured
			postfix = postfix.trim().replaceAll(" +", " ");
			return postfix;
		}

		//Using the algorithm on Wikipedia, solve postfix
		private double getValue(String postfix, double tVal){
			// Replace every variable t with the number we want
			postfix = postfix.replaceAll("(^a-aA-A|\\b)t(^a-aA-A|\\b)|(?<=\\d)t", Double.toString(tVal));

			// Convert the postfix to a stack
			Stack<String> inputStack = new Stack<String>();
			String [] postfixArray = postfix.split(" ");

			for(int i = postfixArray.length - 1; i >= 0; i--){
				inputStack.push(postfixArray[i]);
			}

			// Create the output stack
			Stack<Double> outputStack = new Stack<Double>();
			while(!inputStack.isEmpty()){
				String current = inputStack.pop();
				
				// Push values to the stack
				if(isValue(current)){
					outputStack.push(Double.parseDouble(current));
				}

				// Value is a function or operator
				else {

					//Operators take 2 arguments, functions take 1
					if(isOperator(current) && outputStack.size() < 2){
						System.out.println("Error: the '" + current + "' operator needs 2 values.");
						return 0;
					}
					else if (outputStack.size() < 1){
						System.out.println("Error: the '" + current + "' function needs 1 value.");
						return 0;
					}

					if(current.equals("*")){
						double a = outputStack.pop();
						double b = outputStack.pop();
						double result = a * b;
						outputStack.push(result);
					}
					if(current.equals("/")){
						double a = outputStack.pop();
						double b = outputStack.pop();
						double result = b / a;
						outputStack.push(result);
					}
					if(current.equals("+")){
						double a = outputStack.pop();
						double b = outputStack.pop();
						double result = a + b;
						outputStack.push(result);
					}
					if(current.equals("-")){
						double a = outputStack.pop();
						double b = outputStack.pop();
						double result = b - a;
						outputStack.push(result);
					}
					if(current.equals("^")){
						double a = outputStack.pop();
						double b = outputStack.pop();
						double result = Math.pow(b, a);
						outputStack.push(result);
					}

					// Functions
					if(current.equals("sin")){
						double a = outputStack.pop();
						double result = Math.sin(a);
						outputStack.push(result);
					}
					if(current.equals("cos")){
						double a = outputStack.pop();
						double result = Math.cos(a);
						outputStack.push(result);
					}
					if(current.equals("tan")){
						double a = outputStack.pop();
						double result = Math.tan(a);
						outputStack.push(result);
					}
					if(current.equals("acos")){
						double a = outputStack.pop();
						double result = Math.acos(a);
						outputStack.push(result);
					}
					if(current.equals("asin")){
						double a = outputStack.pop();
						double result = Math.asin(a);
						outputStack.push(result);
					}
					if(current.equals("atan")){
						double a = outputStack.pop();
						double result = Math.atan(a);
						outputStack.push(result);
					}
					if(current.equals("cosh")){
						double a = outputStack.pop();
						double result = Math.cosh(a);
						outputStack.push(result);
					}
					if(current.equals("sinh")){
						double a = outputStack.pop();
						double result = Math.sinh(a);
						outputStack.push(result);
					}
					if(current.equals("tanh")){
						double a = outputStack.pop();
						double result = Math.tanh(a);
						outputStack.push(result);
					}
					if(current.equals("e")){
						double a = outputStack.pop();
						double result = Math.tanh(a);
						outputStack.push(result);
					}
					if(current.equals("log")){
						double a = outputStack.pop();
						double result = Math.log(a);
						outputStack.push(result);
					}
					if(current.equals("sqrt")){
						double a = outputStack.pop();
						double result = Math.sqrt(a);
						outputStack.push(result);
					}
					if(current.equals("abs")){
						double a = outputStack.pop();
						double result = Math.abs(a);
						outputStack.push(result);
					}

				}

				if(inputStack.size() == 0){
					return outputStack.pop();
				}

			}

			return 0;
		}

		private boolean isValue(String test){
			if(test.equals("t")){
				return true;
			}

			try{
				Double.parseDouble(test);
				return true;
			}
			catch (Exception e){
				return false;
			}
		}

		private boolean isOperator(String test){
			return test.equals("*") || test.equals("/") || test.equals("^") || test.equals("+") || test.equals("-");
		}

		private boolean isLeftAssociative(String test){
			return test.equals("*") || test.equals("/") || test.equals("+") || test.equals("-");
		}

		private int getPrecedence(String operator){
			if(operator.equals("-") || operator.equals("+"))
				return 2;
			if(operator.equals("/") || operator.equals("*"))
				return 3;

			return 4;
		}

		private boolean isAlphaOnly(String test){
			return test.matches("[a-zA-Z]+");
		}

		private ArrayList<Integer> getTVals(String bounds){
			ArrayList<Integer> tVals = new ArrayList<Integer>();

			for(int i = getMinBound(bounds); i <= getMaxBound(bounds); i++){
				tVals.add(i);
			}

			return tVals;
		}

		public int getMinBound(String bounds){
			return Integer.parseInt(getBounds(bounds)[0]);
		}

		public int getMaxBound(String bounds){
			return Integer.parseInt(getBounds(bounds)[1]);
		}

		private String[] getBounds(String bounds){
			return bounds.split("\\.\\.");
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