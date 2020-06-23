public class CharacterTest
{
	private static char mostCommonCharacter(ArrayList<Integer> dictionary)
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
		System.out.println(Arrays.toString(tempArray));
		//what if the first one,a, is the largest and has already been guessed...
		for(int i = maxIndex + 1; i < tempArray.length; i++)
		{
			if(tempArray[i] > tempArray[maxIndex] && !previousGuesses.contains((char)(i + 97)))
				maxIndex = i;		
		}
		return (char)(maxIndex + 97);
	}

	public stativ void main(String[] args)
	{

	}
}