package utils.display;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class DisplayText
{
	boolean waitingForInput = false;
	boolean textBoxActive = false;
	float[] textureCoordX = new float[64];
	ArrayList<ArrayList<String>> text;
	private String name; //name of speaking character
	int currentRow = 0;
	int currentPos = 0;
	private int speed = 1;
	boolean pause = false;
	int pauseCount = 0;
	
	/**
	 * Constructor
	 */
	public DisplayText()
	{
		text = new ArrayList<ArrayList<String>>();
		this.calculateTextureCoords();
		setName("...");
	}
	
	/**
	 * Constructor, initializes name
	 * @param n name of speaking character to display in text box
	 */
	public DisplayText(String n)
	{
		text = new ArrayList<ArrayList<String>>();
		this.calculateTextureCoords();
		setName(n);
	}
	
	/**
	 * Set the text that will be displayed in a series of textboxes
	 * @param str the full string that should be displayed in the textboxes
	 */
	public void setText(String str)
	{
		text = splitText(str);
		textBoxActive = true;
		currentRow = 0;
		currentPos = 0;
	}
	
	/**
	 * Get input from the main game loop
	 * @param key key input
	 * @return true if the text box should be closed
	 */
	public boolean sendInput(int key)
	{
		if (waitingForInput)
		{
			if (textBoxActive)
			{
				if (key == Keyboard.KEY_Z || key == Keyboard.KEY_X)
				{
					return this.advanceTextBox();
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Render the background textbox and any text over it
	 */
	public void renderText()
	{
		//render text box
		GL11.glPushMatrix();
			GL11.glTranslatef(64, 28, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 192f/1024f);
				GL11.glVertex2f(0, 0);
				GL11.glTexCoord2f(672f/1024f, 192f/1024f);
				GL11.glVertex2f(672, 0);
				GL11.glTexCoord2f(672f/1024f, 0);
				GL11.glVertex2f(672, 192);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(0, 192);
			GL11.glEnd();
			
			for (int i = 0; i <= currentRow; i ++)
			{
				String renderStr;
				if (i < currentRow)
				{
					renderStr = text.get(0).get(i);
					GL11.glPushMatrix();
						renderString(renderStr, i);
					GL11.glPopMatrix();
				}
				else
				{
					if (speed == 1)
					{
						if (!pause)
						{
							char currentChar = text.get(0).get(i).charAt(currentPos);
							checkForPause(currentChar);
						}
						else if (pauseCount <= 0)
						{
							pause = false;
							pauseCount = 0;
						}
					}
					renderStr = text.get(0).get(i).substring(0, currentPos + 1);
					GL11.glPushMatrix();
						renderString(renderStr, i);
					GL11.glPopMatrix();
					if (speed == 1 && pause)
					{
						pauseCount --;
					}
					else
					{
						if (currentPos == text.get(0).get(i).length() - 1)
						{
							if (currentRow == text.get(0).size() - 1)
							{
								//reached end of text box
								waitingForInput = true;
								break;
							}
							currentRow ++;
							currentPos = 0;
						}
						else
						{
							currentPos += getSpeed();
							if (currentPos >= text.get(0).get(i).length())
								currentPos = text.get(0).get(i).length() - 1;
						}
					}
				}
			}
		GL11.glPopMatrix();
		
		//render name box
		GL11.glPushMatrix();
			GL11.glTranslatef(64+40, 192, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(672f/1024f, 36f/1024f);
				GL11.glVertex2f(0, 0);
				GL11.glTexCoord2f(684f/1024f, 36f/1024f);
				GL11.glVertex2f(12, 0);
				GL11.glTexCoord2f(684f/1024f, 0);
				GL11.glVertex2f(12, 36);
				GL11.glTexCoord2f(672f/1024f, 0);
				GL11.glVertex2f(0, 36);
			GL11.glEnd();
			
			int length = getPixelCount(getName());
			GL11.glTranslatef(12, 0, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(684f/1024f, 36f/1024f);
				GL11.glVertex2f(0, 0);
				GL11.glTexCoord2f(714f/1024f, 36f/1024f);
				GL11.glVertex2f(length, 0);
				GL11.glTexCoord2f(714f/1024f, 0);
				GL11.glVertex2f(length, 36);
				GL11.glTexCoord2f(684f/1024f, 0);
				GL11.glVertex2f(0, 36);
			GL11.glEnd();
			
			GL11.glTranslatef(length, 0, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(714f/1024f, 36f/1024f);
				GL11.glVertex2f(0, 0);
				GL11.glTexCoord2f(726f/1024f, 36f/1024f);
				GL11.glVertex2f(12, 0);
				GL11.glTexCoord2f(726f/1024f, 0);
				GL11.glVertex2f(12, 36);
				GL11.glTexCoord2f(714f/1024f, 0);
				GL11.glVertex2f(0, 36);
			GL11.glEnd();
		GL11.glEnd();
		
		GL11.glTranslatef(-length, 8, 0);
		this.renderString(getName());
		GL11.glPopMatrix();
		
	}

	/**
	 * Advance to the next text box, or close the text box if it is finished
	 * @return true if the text box should be closed
	 */
	private boolean advanceTextBox()
	{
		if (text.size() <= 1)
		{
			text.clear();
			waitingForInput = false;
			textBoxActive = false;
			return true;
		}
		else
		{
			text.remove(0);
			waitingForInput = false;
			currentPos = 0;
			currentRow = 0;
		}
		
		return false;
	}
	
	/**
	 * Determine whether the currently displayed character should cause the text display to pause.
	 * This check only happens if the text is being advanced one character at a time (speed == 1).
	 * @param c the character to check for initiating a pause
	 */
	private void checkForPause(char c)
	{
		//long pause
		if (c == '.' || c == '!' || c == '?')
		{
			pause = true;
			pauseCount = 20;
			return;
		}
		
		//short pause
		if (c == ',' | c == ';' || c == ':')
		{
			pause = true;
			pauseCount = 10;
		}
	}
	
	/**
	 * Render a string representing one line of text
	 * @param str the string to be rendered
	 * @param row the row to render it on
	 */
	private void renderString(String str, int row)
	{
		GL11.glTranslatef(25, 141 - 22*row, 0);
		renderString(str);
	}
	
	/**
	 * Render a string at the current location
	 */
	private void renderString(String str)
	{
		for (int i = 0; i < str.length() - 1; i ++)
		{
			char c = str.charAt(i);
			renderChar(c);
			GL11.glTranslatef(getPixelCount(c), 0, 0);
		}
		renderChar(str.charAt(str.length() - 1));
	}

	/**
	 * Render a single character; assumes the rendering window has already been moved to the
	 * appropriate spot for the rendered character.
	 * @param c the character to render
	 */
	private void renderChar(char c)
	{
		float texXBegin = textureCoordX[getTextureIndex(c)];
		float texXEnd = texXBegin + (getPixelCount(c) - 1)/1024f;
		float texYBegin;
		if (c >= 65 && c <= 90)
		{
			texYBegin = 192/1024f;
		}
		else if (c >= 97 && c <= 122)
		{
			texYBegin = 212/1024f;
		}
		else
		{
			texYBegin = 232/1024f;
		}
		float texYEnd = texYBegin + 20/1024f;
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(texXBegin, texYEnd);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(texXEnd, texYEnd);
			GL11.glVertex2f(getPixelCount(c) - 1, 0);
			GL11.glTexCoord2f(texXEnd, texYBegin);
			GL11.glVertex2f(getPixelCount(c) - 1, 20);
			GL11.glTexCoord2f(texXBegin, texYBegin);
			GL11.glVertex2f(0, 20);
		GL11.glEnd();
	}

	/**
	 * Convert a character into its associated index in the texture coordinate list
	 * @param c the character in question
	 * @return the associated index for use with the textureCoordX list
	 */
	private int getTextureIndex(char c)
	{
		//capital letters
		if (c >= 65 && c <= 90)
			return c - 65;
		
		//lower case letters
		if (c >= 97 && c <= 122)
			return c - 97 + 26;
		
		//punctuation
		switch (c)
		{
		case ('.'): return 52;
		case (','): return 53;
		case ('?'): return 54;
		case ('!'): return 55;
		case ('-'): return 56;
		case (':'): return 57;
		case (';'): return 58;
		case ('‘'): return 59;
		case ('\''): return 60;
		case ('’'): return 60;
		case ('“'): return 61;
		case ('”'): return 62;
		case ('\"'): return 62;
		case (' '): return 63;
		}
		
		//if unrecognized, return the index for ' '
		return 63;
	}
	
	/**
	 * Split text up into lines and textboxes, accounting for the maximum pixel length of each lines and maximum
	 * lines of each text box.
	 * @param text a single String of input text to be split up
	 * @return the split text within size constraints
	 */
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
	
	/**
	 * Calculate the beginning x coordinates for each character
	 */
	private void calculateTextureCoords()
	{
		//upper case letters
		char c = 'A';
		for (int i = 0; i < 26; i ++)
		{
			int offset = (18 - getPixelCount(c)) / 2;
			textureCoordX[i] = (i*18 + offset) / 1024f;
		}
		
		//lower case letters
		c = 'a';
		for (int i = 26; i < 52; i ++)
		{
			int offset = (18 - getPixelCount(c)) / 2;
			textureCoordX[i] = ((i - 26)*18 + offset) / 1024f;
		}
		
		//punctuation/other
		int offset;
		c = '.';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[52] = (18*0 + offset)/1024f;
		c = ',';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[53] = (18*1 + offset)/1024f;
		c = '?';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[54] = (18*2 + offset)/1024f;
		c = '!';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[55] = (18*3 + offset)/1024f;
		c = '-';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[56] = (18*4 + offset)/1024f;
		c = ':';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[57] = (18*5 + offset)/1024f;
		c = ';';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[58] = (18*6 + offset)/1024f;
		c = '‘';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[59] = (18*7 + offset)/1024f;
		c = '’';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[60] = (18*8 + offset)/1024f;
		c = '\'';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[60] = (18*8 + offset)/1024f;
		c = '“';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[61] = (18*9 + offset)/1024f;
		c = '”';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[62] = (18*10 + offset)/1024f;
		c = '"';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[62] = (18*10 + offset)/1024f;
		c = ' ';
		offset = (18 - getPixelCount(c)) / 2;
		textureCoordX[63] = (18*11 + offset)/1024f;
	}

	/**
	 * Calculate the length in pixels of a string
	 * @param str the string for which to determine the pixel length
	 * @return the pixel length of the given string
	 */
	private static int getPixelCount(String str)
	{
		int pixelCount = 0;
		for (int i = 0; i < str.length(); i ++)
		{
			pixelCount += DisplayText.getPixelCount(str.charAt(i));
		}
		return pixelCount;
	}

	/**
	 * Get the pixel length of a single character
	 * @param c the character for which to get the pixel length
	 * @return the pixel length of the given character
	 */
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
		case '\"':	return 14;
		case '“':	return 14;
		case '”':	return 14;
		default:	return 8;
		}
	}

	/**
	 * Setter for text speed
	 * @param speed value to which speed should be set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * Getter for text speed
	 * @return speed member
	 */
	public int getSpeed() {
		return speed;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
