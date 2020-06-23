/**
* Contains the class Hangman
* has various methods that work together 
* to create an AI hangman
* Author: Sam Boshar
* Course: CSC500, Period 1, Ms. Bednarcik
* Due: 5/31/18
*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.io.File;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;

public class Hangman 
{
	private static Scanner scan = new Scanner(System.in);
	private static int wordLength;
	private static int scale = 1000;
	private static char computerGuess;
	private static ArrayList<String> dictionary;
	private static ArrayList<String> frequencyDictionary;
	private static ArrayList<Integer> positionsOfGuesses = new ArrayList<Integer>();
	private static String response;
	private static int occurences;
	private static int numGuesses = 0;
	private static ArrayList<Character> word = new ArrayList<Character>();
	private static ArrayList<Character> previousGuesses = new ArrayList<Character>();
	private static ArrayList<Character> correctGuesses = new ArrayList<Character>();
	private static HangmanGraphics h;

	// this method finds the most common character in the dictionary without the remove duplicates method. After test,
	// I have determined this method to be much faster than the alternative two methods by a factor of almost 60
	private static char mostCommonCharacter()
	{
		int maxIndex = 0;
		int[] tempArray = new int[26];
		for(int i = 0; i < dictionary.size(); i++)
		{
			for(int j = 97; j < 123; j++)
			{
				if(dictionary.get(i).indexOf((char)(j)) >= 0)
					tempArray[j - 97]++;
			}
		}

		while(previousGuesses.contains((char)(maxIndex + 97)))
		{
			maxIndex++;
		}

		for(int i = maxIndex + 1; i < tempArray.length; i++)
		{
			if(tempArray[i] > tempArray[maxIndex] && !previousGuesses.contains((char)(i + 97)))
				maxIndex = i;		
		}
		return (char)(maxIndex + 97);
	}

	//find the most common character taking into account word frequency
	private static char mostCommonFreqCharacter()
	{
		int maxIndex = 0;
		int[] tempArray = new int[26];
		int[] tempArray2 = new int[26];
		for(int i = 0; i < dictionary.size(); i++)
		{
			for(int j = 97; j < 123; j++)
			{
				if(dictionary.get(i).indexOf((char)(j)) >= 0)
					tempArray[j - 97] += scaleFunction(dictionary.get(i));
			}
		}
		while(previousGuesses.contains((char)(maxIndex + 97)))
		{
			maxIndex++;
		}
		for(int i = maxIndex + 1; i < tempArray.length; i++)
		{
			if(tempArray[i] > tempArray[maxIndex] && !previousGuesses.contains((char)(i + 97)))
				maxIndex = i;		
		}
		return (char)(maxIndex + 97);
	}

	// this function takes in a string and scales it using the frequency dictionary so that the letters
	// of more commonly used words are weighed as lightly higher
	private static int scaleFunction(String word)
	{
		int n = frequencyDictionary.indexOf(word);
		if(n >= 0)
			return (int)(scale - scale * (double)n/frequencyDictionary.size());
		return 1;
	}
	// counts the occurences of char c in String word
	private static int characterCount(String word, char c)
	{
		int count = 0;
		for(int i = 0; i < word.length(); i++)
		{
			if(word.charAt(i) == c)
				count++;
		}
		return count;
	}

	// removes all words that are not 'lengthOfWord' charactes long 
	private static void removeOtherLengthWords()
	{
		ArrayList<String> tempArrayList = new ArrayList<String>();
		for(int i = 0; i < dictionary.size(); i++)
		{
			if (dictionary.get(i).length() == wordLength)
				tempArrayList.add(dictionary.get(i));
		}
		dictionary = tempArrayList;
	}

	//this method takes in a char and an array of the positions of that char and removes all words that dont fit that
	// description-including words that have extra of that char-it is exclusive
	private static void correctRemoveFromDictionary()
	{
		ArrayList<String> tempArrayList = new ArrayList<String>();
		for(int i = 0; i < dictionary.size(); i++)
		{
			if(containsLettersInPos(dictionary.get(i)))
			{
				tempArrayList.add(dictionary.get(i));
			}
		}
		dictionary = tempArrayList;
	}

	//this is a helper method for correctRemoveFromDictionary()-takes a string and returns whether ir is a match
	private static boolean containsLettersInPos(String w)
	{
		for(int i = 0; i < positionsOfGuesses.size(); i++)
		{
			if(w.charAt(positionsOfGuesses.get(i)) != computerGuess)
				return false;
		}
		return characterCount(w, computerGuess) == positionsOfGuesses.size();
	}

	// if the computer guesses the letter wrong this method will remove all words with that letter from the dictionary
	private static void incorrectRemoveFromDictionary()
	{
		ArrayList<String> tempArrayList = new ArrayList<String>();
		for(int i = 0; i < dictionary.size(); i++)
		{
			if(dictionary.get(i).indexOf(computerGuess) < 0)
				tempArrayList.add(dictionary.get(i));
		}
		dictionary = tempArrayList;
	}

	// This method takes in a filename for a textfile and returns a String ArrayList of all of the words in that
	// text file. It utilizes BufferedReader.
	private static ArrayList<String> createArrayListFromTextFile(String fileName) throws IOException
	{
		ArrayList<String> tempArrayList = new ArrayList<String>();
		File file = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str;
		while ((str = br.readLine()) != null)
			if(str.length() > 1)
				tempArrayList.add(str.toLowerCase());
		return tempArrayList;
	}

	// This method will provide a sort of short cut but skipping the first leveling of processing for the computer.
	// the guesses for an length word between 2 and 15 have already been calculated because they consume the most
	// processing time.
	private static void setFirstGuess()
	{
		if(wordLength < 4)
			computerGuess = 'a';
		else if(wordLength >= 4 && wordLength < 12)
			computerGuess = 'e';
		else
			computerGuess ='i';
	}

	// this method may be combined into another method or even the main method. It asks for the user's input, and only
	// accpets values in the correct rangw. Then it sets the field wordLength equal to this value.
	private static void getStartInfo()
	{
		do
		{
			System.out.println("Enter the length of your word (2-15 characters): ");
			wordLength = scan.nextInt();
			scan.nextLine();
		} while(wordLength < 2 || wordLength > 15);
		h.setWordLength(wordLength);
		for (int i = 0; i < wordLength; i++) {
  			word.add(' ');
		}
		removeOtherLengthWords();
		setFirstGuess();
	}

	// when it only appears once you don't want the first
	private static String positionalWord(int n)
	{
		switch(n)
		{
			case 1:
				return "first";
			case 2:
				return "second";
			case 3:
				return "third";
			case 4:
				return "fourth";
			case 5:
				return "fifth";
			case 6:
				return "sixth";
			case 7:
				return "seventh";
			case 8:
				return "eigth";
			case 9:
				return "ninth";
			default:
				return ""; 
		}
	}

	//this is a helper method that is basically a compare method that I made to help the orderByFrequency() method
	private static boolean compareStrings(String s1, String s2)
	{
		int freqS1 = frequencyDictionary.indexOf(s1);
		int freqS2 = frequencyDictionary.indexOf(s2);
		if (freqS1 >= 0 && freqS2 >= 0)
			return freqS1 < freqS2;
		else if(freqS1 < 0 && freqS2 >= 0)
			return false;
		else
			return true;
	}

	//orders words by frequency
	private static void orderByFrequency()
	{
		ArrayList<String> a = new ArrayList<String>();
		for(int n = 1; n < dictionary.size(); n++)
		{
			String aTemp = dictionary.get(n);
			int i = n;
			while(i > 0 && compareStrings(aTemp, dictionary.get(i-1)))
			{
				dictionary.set(i, dictionary.get(i-1));
				i--;
			}
			dictionary.set(i, aTemp);
		}
	}

	//main guessing method,
	private static void guessing()
	{
		do
		{
			System.out.println("Is " + computerGuess + " in your word? (enter yes or no) ");
			response = scan.nextLine();
		} while(!"yes".equals(response) && !"no".equals(response) && !"print words".equals(response));

		if("no".equals(response))
		{
			previousGuesses.add(computerGuess); 	
			numGuesses++;
			h.setNumber(numGuesses);
			h.repaint();
			incorrectRemoveFromDictionary();
			computerGuess = mostCommonFreqCharacter();

			if(dictionary.size() == 0)
			{
				System.out.println("Sorry, I did not recognize your word.");
				do
				{
					System.out.println("Would you like close?");
					response = scan.nextLine();
				} while(!"yes".equals(response));
					
				if("yes".equals(response))
					System.exit(0);
			}
			else if(dictionary.size() == 1)
			{
				do
				{
					System.out.println("Is " + dictionary.get(0) + " your word?");
					response = scan.nextLine();
				} while(!"yes".equals(response) && !"no".equals(response));
				
				if("yes".equals(response))
				{
					for(int i = 0; i < dictionary.get(0).length(); i++)
					{
						word.set(i, dictionary.get(0).charAt(i));
					}
					
					h.setPositions(word);
					h.repaint();
					System.out.println("I got your word in " + numGuesses + " wrong guesses and " + previousGuesses.size() + " total guesses! Good game!");
					
					do
					{
						System.out.println("Would you like close?");
						response = scan.nextLine();
					} while(!"yes".equals(response));
					
					if("yes".equals(response))
						System.exit(0);
				}
				else
				{
					System.out.println("I didn't find your word, but good game!");
					
					do
					{
						System.out.println("Would you like close?");
						response = scan.nextLine();
					} while(!"yes".equals(response));
					
					if("yes".equals(response))
						System.exit(0);
				}	
			}
			guessing();
		}

		else if("yes".equals(response))
		{
			correctGuesses.add(computerGuess);
			do
			{
				System.out.println("How many times does the letter " + computerGuess + " appear in your word? ");
				occurences = scan.nextInt();
				scan.nextLine();
			} while(occurences < 0 || occurences > wordLength);	

			for(int i = 0; i < occurences; i++)
			{
				int position;
				do
				{
					System.out.println("what is the position of the " + positionalWord(i + 1) + " occurence? ");
					position = scan.nextInt();
					scan.nextLine();
					word.set(position - 1, computerGuess);
					positionsOfGuesses.add(position - 1);
				} while(position < 1 || position > wordLength);
			}
			
			h.setPositions(word);
			h.repaint();
			correctRemoveFromDictionary();
			previousGuesses.add(computerGuess);
			positionsOfGuesses.clear();
			computerGuess = mostCommonFreqCharacter();
			
			if(dictionary.size() == 1)
			{
				do
				{
					System.out.println("Is " + dictionary.get(0) + " your word?");
					response = scan.nextLine();
				} while(!"yes".equals(response) && !"no".equals(response));
				
				if("yes".equals(response))
				{
					for(int i = 0; i < dictionary.get(0).length(); i++)
					{
						word.set(i, dictionary.get(0).charAt(i));
					}

					h.setPositions(word);
					h.repaint();
					System.out.println("I got your word in " + numGuesses + " wrong guesses and " + previousGuesses.size() + " total guesses! Good game!");
					do
					{
						System.out.println("Would you like close?");
						response = scan.nextLine();
					} while(!"yes".equals(response));
					
					if("yes".equals(response))
						System.exit(0);
				}

				else
				{
					System.out.println("I didn't find your word, but good game!");
					do
					{
						System.out.println("Would you like close?");
						response = scan.nextLine();
					} while(!"yes".equals(response));
					
					if("yes".equals(response))
						System.exit(0);
				}	
			}
			guessing();
		}
		else
		{
			do
			{
				System.out.println("Would you like to print the words?");
				response = scan.nextLine();
			} while(!"yes".equals(response) && !"no".equals(response));
			
			if("yes".equals(response))
			{
				do
				{
					System.out.println("Would you like to print the words by frequency? ");
					response = scan.nextLine();
				} while(!"yes".equals(response) && !"no".equals(response));
				
				if("yes".equals(response))
				{
					orderByFrequency();
					System.out.println(dictionary);
				}
				else
				{
					System.out.println(dictionary);
				}
			}
			guessing();
		}
	}

	public static void main(String[] args) throws IOException
	{
		frequencyDictionary = createArrayListFromTextFile("SmallFreqDict.txt");
		dictionary = createArrayListFromTextFile("Sowpods.txt");
		h = new HangmanGraphics(400, numGuesses);
		getStartInfo();
		guessing();
	}
}
