package text;

// A Text is an array of characters to be rendered in a message box.
public class Text {
	private char[] chars; // the characters to be rendered
	
	// constructor
	public Text(String words) {
		// copy each char of the input string into this.chars
		words.getChars(0, (words.length()-1), this.chars, 0);
	}
	
	// get the entire char array
	public char[] getChars() { return chars; }
	
	// get the specified letter from the char array
	public char getLetter(int index) { return chars[index];	}
	
	
}