package utils;

import java.util.ArrayList;

public class DisplayText
{
	public static ArrayList<ArrayList<String>> splitText(String text)
	{
		//Split text into lines
		String[] lineSplit = text.split("\n");
		ArrayList<String> lines = new ArrayList<String>();
		for (int i = 0; i < lineSplit.length; i ++)
		{
			String[] words = lineSplit[i].split(" ");
			int pixelCount = 0;
			String line = "";
			for (int j = 0; j < words.length; j ++)
			{
				int wordPixelCount = DisplayText.getPixelCount(words[j]);
				if (pixelCount + wordPixelCount > 622)
				{
					lines.add(line);
					line = words[j];
					pixelCount = wordPixelCount;
				}
				else
				{
					line += words[j];
					pixelCount += wordPixelCount;
				}
				pixelCount += DisplayText.getPixelCount(' ');
				if (pixelCount > 622)
				{
					lines.add(line);
					line = "";
					pixelCount = 0;
				}
				else
				{
					line += " ";
				}
			}
			if (line.length() > 0)
			{
				lines.add(line);
			}
		}
		
		//split up lines into text boxes, 6 lines per box
		ArrayList<ArrayList<String>> boxes = new ArrayList<ArrayList<String>>();
		for (int i = 5; (i - 5) < lines.size(); i += 6)
		{
			ArrayList<String> tempList = new ArrayList<String>();
			int end;
			if (i >= lines.size())
			{
				end = lines.size();
			}
			else
			{
				end = i + 1;
			}
			for (int j = i - 5; j < end; j ++)
			{
				tempList.add(lines.get(j));
			}
			boxes.add(tempList);
		}
		
		return boxes;
	}
	
	private static int getPixelCount(String str)
	{
		int pixelCount = 0;
		for (int i = 0; i < str.length(); i ++)
		{
			pixelCount += DisplayText.getPixelCount(str.charAt(i));
		}
		return pixelCount;
	}
	
	private static int getPixelCount(char c)
	{
		//capital letters
		if (c >= 65 && c <= 90)
		{
			return 18;
		}
		
		//lower case letters
		if (c >= 97 && c <= 122)
		{
			switch(c)
			{
			case 'i':	return 11;
			case 'j':	return 10;
			case 'k':	return 13;
			case 'l':	return 11;
			case 't':	return 12;
			default:	return 14;
			}
		}
		
		//punctuation and spaces
		switch (c)
		{
		case ' ':	return 8;
		case '?':	return 14;
		case '-':	return 10;
		default:	return 8;
		}
	}
	
	public static void main(String[] args)
	{
		String testString = "There were plotters, there was no doubt about it. Some had been ordinary people who'd had enough. Some were young people with no money who objected to the fact that the world was run by old people who were rich. Some were in it to get girls. And some had been idiots as mad as Swing, with a view of the world just as rigid and unreal, who were on the side of what they called 'the people'. Vimes had spent his life on the streets, and had met decent men and fools and people who'd steal a penny from a blind beggar and people who performed silent miracles or desperate crimes every day behind the grubby windows of little houses, but he'd never met The People.\n\nPeople on the side of The People always ended up dissapointed, in any case. They found that The People tended not to be grateful or appreciative or forward-thinking or obedient. The People tended to be small-minded and conservative and not very clever and were even distrustful of cleverness. And so the children of the revolution were faced with the age-old problem: it wasn't that you had the wrong kind of government, which was obvious, but that you had the wrong kind of people.\n\nAs soon as you saw people as things to be measured, they didn't measure up. What would run through the streets soon enough wouldn't be a revolution or a riot. It'd be people who were frightened and panicking. It was what happened when the machinery of city life faltered, the wheels stopped turning and all the little rules broke down. And when that happened, humans were worse than sheep. Sheep just ran; they didn't try to bite the sheep next to them.";
		ArrayList<ArrayList<String>> output = DisplayText.splitText(testString);
		System.out.println("Organized text:");
		System.out.println();
		for (int i = 0; i < output.size(); i ++)
		{
			System.out.println("Text box " + (i + 1) + ":");
			for (int j = 0; j < output.get(i).size(); j ++)
			{
				System.out.println("\t" + output.get(i).get(j));
			}
		}
	}
}
