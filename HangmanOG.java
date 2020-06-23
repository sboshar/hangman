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

		//to insure that the first guess a hasnt already been guessed
		while(previousGuesses.contains((char)(maxIndex + 97)))
		{
			maxIndex++;
		}
		// System.out.println(Arrays.toString(tempArray));
		//what if the first one,a, is the largest and has already been guessed...
		for(int i = maxIndex + 1; i < tempArray.length; i++)
		{
			if(tempArray[i] > tempArray[maxIndex] && !previousGuesses.contains((char)(i + 97)))
				maxIndex = i;		
		}
		return (char)(maxIndex + 97);
	}

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

		// System.out.println(Arrays.toString(tempArray));

		//to insure that the first guess a hasnt already been guessed
		while(previousGuesses.contains((char)(maxIndex + 97)))
		{
			maxIndex++;
		}
		// System.out.println(Arrays.toString(tempArray));
		//what if the first one,a, is the largest and has already been guessed...
		for(int i = maxIndex + 1; i < tempArray.length; i++)
		{
			if(tempArray[i] > tempArray[maxIndex] && !previousGuesses.contains((char)(i + 97)))
				maxIndex = i;		
		}
		return (char)(maxIndex + 97);
	}

	private static int scaleFunction(String word)
	{
		int n = frequencyDictionary.indexOf(word);
		// System.out.println(n);
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

	// if the cmoputer guesses the letter wrong this method will remove all words with that letter from the dictionary
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
	// private static void setFirstGuess()
	// {
	// 	if(wordLength == 2)
	// 		computerGuess = 'o';
	// 	else if(wordLength > 2 && wordLength < 5)
	// 		computerGuess = 'a';
	// 	else if(wordLength == 5)
	// 		computerGuess = 's';
	// 	else if(wordLength > 5 && wordLength < 12)
	// 		computerGuess = 'e';
	// 	else
	// 		computerGuess ='i';
	// }
	private static void setFirstGuess()
	{
		if(wordLength < 4)
			computerGuess = 'a';
		else if(wordLength > 4 && wordLength < 12)
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

	//this is a hepler method that is basically a compare method that I made to help the orderByFrequency() method
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

	//create a function to test if the words are only one letter different (question what if one is two letters dif when is it optimal to change to frequency)
	
	//works but is horribly ineffcicient
	private static void orderByFrequency()
	{
		ArrayList<String> a = new ArrayList<String>();
		for(int n = 1; n < dictionary.size(); n++)
		{
			String aTemp = dictionary.get(n);
			int i = n;
			while(i > 0 && compareStrings(aTemp, dictionary.get(i-1)))
			{
				System.out.println(dictionary);
				dictionary.set(i, dictionary.get(i-1));
				i--;
			}
			dictionary.set(i, aTemp);
		}
	}
	// private static int guessing(String word)
	// {
	// 	// System.out.println(computerGuess);
	// 	// System.out.println(dictionary);
	// 	// System.out.println("before: " + numGuesses);
	// 	// System.out.println(computerGuess);
	// 	// System.out.println(previousGuesses);
	// 	if(!word.contains("" + computerGuess))
	// 	{
	// 		//do i need this?
	// 		previousGuesses.add(computerGuess);
	// 		numGuesses++;
	// 		incorrectRemoveFromDictionary();
	// 		// System.out.println(dictionary);
	// 		computerGuess = mostCommonFreqCharacter();
	// 		if(dictionary.size() == 0)
	// 		{
	// 			//System.out.println("Sorry, I did not recognize your word.");
	// 			return -1;
	// 		}
	// 		else if(dictionary.size() == 1)
	// 		{
	// 			if(dictionary.get(0).equals(word))
	// 			{
	// 				return numGuesses;
	// 			}
	// 			return -1;
	// 		}
	// 		guessing(word);
	// 	}

	// 	else
	// 	{
	// 		for(int i = 0; i < word.length(); i++)
	// 		{
	// 			if(word.charAt(i) == computerGuess)
	// 			{
	// 				positionsOfGuesses.add(i);
	// 			}
	// 		}
	// 		// System.out.println("POG" + positionsOfGuesses.toString());

	// 		// System.out.println(positionsOfGuesses);
	// 		correctRemoveFromDictionary();
	// 		// System.out.println(dictionary);
	// 		previousGuesses.add(computerGuess);
	// 		// System.out.println(previousGuesses);
	// 		positionsOfGuesses.clear();
	// 		// System.out.println(positionsOfGuesses);
	// 		computerGuess = mostCommonFreqCharacter();
	// 		if(dictionary.size() == 1)
	// 		{
	// 			if (dictionary.get(0).equals(word))
	// 			{
	// 				return numGuesses;
	// 			}
	// 			return -1;
	// 		}
	// 		guessing(word);
	// 	}
	// 	return numGuesses;

	// }

	//if only one word left still draw the word
	private static void guessing()
	{
		//why does it repeat the statement twice?
		do
		{
			System.out.println("Is " + computerGuess + " in your word? (enter yes or no) ");
			response = scan.nextLine();
		} while(!"yes".equals(response) && !"no".equals(response) && !"p".equals(response));

		if("no".equals(response))
		{
			previousGuesses.add(computerGuess); 	
			numGuesses++;

			h.setNumber(numGuesses);
			h.repaint();
			incorrectRemoveFromDictionary();
			// System.out.println(dictionary);
			computerGuess = mostCommonFreqCharacter();
			if(dictionary.size() == 0)
			{
				System.out.println("Sorry, I did not recognize your word.");
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
					System.out.println("I got your word in " + numGuesses + " guesses! Good game!");
					System.exit(0);
				}
				System.out.println("I didn't find your word, but good game!");
				System.exit(0);
			}
			guessing();
		}
		else if("p".equals(response))
		{
			// orderByFrequency();
			System.out.println(dictionary);
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
					// if(position >= 1 && position <= wordLength)
					word.set(position - 1, computerGuess);
					positionsOfGuesses.add(position - 1);
				} while(position < 1 || position > wordLength);
			}
			h.setPositions(word);
			h.repaint();
			// System.out.println(positionsOfGuesses);
			correctRemoveFromDictionary();
			// System.out.println(dictionary);
			previousGuesses.add(computerGuess);
			// System.out.println(previousGuesses);
			positionsOfGuesses.clear();
			// System.out.println(positionsOfGuesses);
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
					System.out.println("I got your word in " + numGuesses + " guesses! Good game!");
					System.exit(0);
				}
				System.out.println("I didn't find your word, but good game!");
				System.exit(0);
			}
			guessing();

		}

	}

	// if top choice is the same
	// 100- 100 * index(freqarry)/ length(freqarry)
	//as soon as i use get lengthOfWord, another do while the guess statement repeats twice.still happens
	// with a regular while
	public static void main(String[] args) throws IOException
	{
		// int total = 0;
		frequencyDictionary = createArrayListFromTextFile("SmallFreqDict.txt");
		dictionary = createArrayListFromTextFile("Sowpods.txt");
		// ArrayList<Integer> a = new ArrayList<Integer>();
		// // orderByFrequency();
		// for(int i = 0; i < 100; i++)
		// {
		// 	dictionary = createArrayListFromTextFile("Sowpods.txt");
		// 	previousGuesses.clear();
		// 	numGuesses = 0;
		// 	String word = frequencyDictionary.get(i);
		// 	// System.out.println(word);
		// 	wordLength = word.length();
		// 	removeOtherLengthWords();
		// 	setFirstGuess();
		// 	int num = guessing(word);
		// 	if(num > 6)
		// 	{
		// 		total++;
		// 	}
		// 	a.add(num);
		// }

		// // System.out.println(a.toString());
		// int sum = 0;
		// for(int i = 0; i < a.size(); i++)
		// {
		// 	sum += a.get(i);
		// }
		// System.out.println(total);
		// System.out.println(sum);
		// System.out.println(1.0 * sum / a.size());

		
		h = new HangmanGraphics(400, numGuesses);
		getStartInfo();
		guessing();
		// char c = mostCommonCharacter();
		// System.out.println(c);
		// char d = mostCommonFreqCharacter();
		// System.out.println(d);

		// for(int i = 2; i<=15;i++)
		// {
	
		// 	dictionary = createArrayListFromTextFile("Sowpods.txt");
		// 	getStartInfo();
		// 	char d = mostCommonFreqCharacter();
		// 	System.out.println(d);
		// }

		// for(int i = 0; i < 10; i++)
		// {
		// 	int num = (int)(Math.random() * dictionary.size());

		// 	System.out.println(dictionary.get(num) + ", score: " + scaleFunction(dictionary.get(num)));
		// }
		// System.out.println(scaleFunction("resort"));
		// System.out.println(mostCommonCharacter());
		
		// guessing();
	}
	//may need to keep an array of previous guesses


	// this method removes duplicate letters from a word passed into it
	// private static String removeDuplicates(String str)
	// {
	// 	for(int i = 0; i < str.length(); i++)
	// 	{
	// 		for(int j = i + 1; j < str.length(); j++)
	// 		{
	// 			if(str.charAt(i) == str.charAt(j))
	// 			{
	// 				str = str.substring(0, j) + str.substring(j + 1);
    //                 j--;
	// 			}
	// 		}
	// 	}
	// 	return str;
	// }

	// this method takes the dictionary, takes each word, removes repeated letters using removeDuplicates method and 
	// // concatenates them into one long string, which it returns
	// public static String returnStringNoDuplicates()
	// {
	// 	String tempString = "";
	// 	for(String word: dictionary)
	// 		tempString += removeDuplicates(word);
	// 	return tempString;
	// }

	// given the string this method finds the most common letter in the string—it is possible that these two methods
	// // should be conbined into one as shown below in the commented area
	// public static char findMostCommonChar(String str)
	// {
	// 	int[] tempArray = new int[26];
	// 	for(int i = 0; i < str.length(); i++)
	// 	{
	// 		if(Character.isLetter(str.charAt(i)))
	// 		{
	// 			// System.out.println(str.charAt(i));
	// 			tempArray[(int)(Character.toLowerCase(str.charAt(i))) - 97]++;
	// 		}
			
	// 	}
	// 	int maxIndex = 0;
	// 	for(int i = 1; i < tempArray.length; i++)
	// 	{
	// 		if(tempArray[i] > tempArray[maxIndex])
	// 			maxIndex = i;
				
	// 	}
	// 	return (char)(maxIndex + 97);
	// }

	// public static char findMostCommonChar()
	// {
	// long time = System.currentTimeMillis();
	// 	String str = "";

	// 	for(String word: dictionary)
	// 		str += removeDuplicates(word);

	// 	int[] tempArray = new int[26];

	// 	for(int i = 0; i < str.length(); i++)
	// 	{
	// 		tempArray[(int)(str.charAt(i)) - 97]++;
	// 	}
	// 	int maxIndex = 0;
	// 	for(int i = 1; i < tempArray.length; i++)
	// 	{
	// 		if(tempArray[i] > tempArray[maxIndex])
	// 			maxIndex = i;
				
	// 	}
	// System.out.println(System.currentTimeMillis() - time);
	// 	return (char)(maxIndex + 97);
	// }
	// the same method as the one above with a while loop
	// public static void removeFromDictionary(char c, ArrayList<Integer> positions)
	// {
	// 	int count = 0;
	// 	ArrayList<String> tempArrayList = new ArrayList<String>();
	// 	for(int i = 0; i < dictionary.size(); i++)
	// 	{
	// 		while(count < positions.size())
	// 		{
	// 			if (dictionary.get(i).charAt(positions.get(count)) == c)
	// 				count++;
	// 			else
	// 				count = positions.size();	
	// 		}
	// 		if (positions.size() == characterCount(dictionary.get(i), c))
	// 				tempArrayList.add(dictionary.get(i));	
	// 		count = 0;	
	// 	}
	// 	dictionary = tempArrayList;
	// }

	//selection sort attmpt

	// private static void orderByFrequency()
	// {
	// 	for(int n = dictionary.size(); n > 1; n--)
	// 	{
	// 		int iMax = 0;
	// 		for(int i = 1; i < n; i++)
	// 		{
	// 			if(frequencyDictionary.indexOf(dictionary.get(i)) > frequencyDictionary.indexOf(dictionary.get(iMax)) || frequencyDictionary.indexOf(dictionary.get(i)) < 0)
	// 			{
	// 				iMax = i;
	// 			}
	// 		}
	// 		String aTemp = dictionary.get(iMax);
	// 		dictionary.set(iMax, dictionary.get(n-1));
	// 		dictionary.set(n-1, aTemp);
	// 		System.out.println(dictionary);

	// 	}
	// }

}