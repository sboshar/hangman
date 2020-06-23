/**
* Contains the class HangmanGraphics
* has various methods that work together 
* to create the graphics for the hangman program
* using JFrame.
* Author: Sam Boshar
* Course: CSC500, Period 1, Ms. Bednarcik
* Due: 5/31/18
*/

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class HangmanGraphics extends JFrame {

	private int width;
	private int height;
	private int radius;
	private int number;
	private int wordLength = 0;
	private double scale;
	private static ArrayList<Character> word = new ArrayList<Character>();

	public HangmanGraphics(int w, int n) 
	{
		width = w;
		height = (int)(width * 1.5);
		number = n;
		scale = 5.0;
		radius = width / 4;
		setSize(width,height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void paint( Graphics g) 
	{
		if(wordLength > 0)
		{
			int fontsize = height / 4 / wordLength;
			double length = (scale * width) / ((scale + 1) * wordLength + 1);
			double space = length / scale;
			g.setFont(new Font("TimesRoman", Font.PLAIN, fontsize)); 

			for(int i = 0; i < wordLength; i++)
			{
				g.drawLine((int)(space + i * (space + length)),(int)( height / 6), (int) (space + length + i * (space + length)), (int)( height / 6));
			}

			for (int i = 0; i < word.size(); i++)
			{
				String s = word.get(i) + "";
				g.drawString(s, (int)(space + length / 3 + i * (space + length)), (int)(height / 6 - 1));
			}
		}

		g.drawLine(width / 2, width/2 - radius/2, width / 2, width/2 - radius/2 - height / 20);
		g.drawLine(width / 2 - width / 20, width/2 - radius/2 - height / 20, width / 2 + width/3, width/2 - radius/2 - height / 20);

		g.drawLine(width / 2 - width / 20, width/2 - radius/2 - height / 25, width / 2 + width/3, width/2 - radius/2 - height / 25);
		g.drawLine(width / 2 - width / 20, width/2 - radius/2 - height / 25, width / 2 - width / 20, width/2 - radius/2 - height / 25 - height / 100);

		g.drawLine(width / 2 + width/3, width/2 - radius/2 - height / 20, width / 2 + width/3, 7 * height / 8);
		g.drawLine(width / 2 + width/3 - height / 100, width/2 - radius/2 - height / 20, width / 2 + width/3 - height / 100, 7 * height / 8);

		g.drawLine(width / 2, 7 * height / 8, 15 * width / 16, 7 * height / 8);
		g.drawLine(width / 2, 7 * height / 8 - height / 100, 15 * width / 16, 7 * height / 8 - height / 100);

		g.drawLine(width / 2, 15 * height / 16, 15 * width / 16, 15 * height / 16);
		g.drawLine(width / 2, 15 * height / 16 - height / 100, 15 * width / 16, 15 * height / 16 - height / 100);

		g.drawLine(width / 2, 15 * height / 16, width / 2, 7 * height / 8 - height/100);
		g.drawLine(width / 2 + height/100, 15 * height / 16, width / 2 + height/100, 7 * height / 8 - height/100);

		g.drawLine(15 * width / 16, 15 * height / 16, 15 * width / 16, 7 * height / 8 - height/100);
		g.drawLine(15 * width / 16 - height/100, 15 * height / 16, 15 * width / 16 - height/100, 7 * height / 8 - height/100);

		g.drawLine(0, 29 * height / 32, width / 2, 29 * height / 32);
		g.drawLine(15 * width / 16, 29 * height / 32, width, 29 * height / 32);

		if(number >= 6)
		{
			g.drawLine(width / 2, width/2 + 4 * radius/2, 2 * width /3, 5 * height/6);
		}
		if(number >= 5)
		{
			g.drawLine(width / 2, width/2 + 4 * radius/2, width /3, 5 * height/6);
		}
		if(number >= 4)
		{
			g.drawLine(width / 2, width/2 + radius/2 + radius / 2, 2 * width /3, 5 * width/9);
		}
		if(number >= 3)
		{
			g.drawLine(width / 2, width/2 + radius/2 + radius / 2, width /3, 5 * width/9);
		}
		if(number >= 2)
		{
			g.drawLine(width / 2, width/2- radius/2 + 2*radius / 2, width /2, width/2 + 4 * radius/2);
		}
		if(number >= 1)
		{
			g.drawOval(width/2  - radius/2, width/2- radius/2, radius, radius);	
		}
	}

	//set the number of guesses
	public void setNumber(int n)
	{
		number = n;
	}

	//sets the word length
	public void setWordLength(int n)
	{
		wordLength = n;
	}

	//sets the positions to a
	public void setPositions(ArrayList<Character> a)
	{
		word = a;
	}
}