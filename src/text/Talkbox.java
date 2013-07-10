package text;

// A Talkbox is a rectangle rendered at the bottom of the screen to display
// dialogue.  It contains a Portrait and Text.
public class Talkbox {
	private int x; // the x position
	private int y; // the y position
	private int height; // in pixels
	private int width; // in pixels
	private Text text; // the array of characters to be rendered in the Talkbox
	private Font font; // the font to be used to render the text in this Talkbox
	/* TODO: implement portraits 
	 * Portrait portrait; // the image shown in the Talkbox
	 */

	
	// constructor
	public Talkbox(int x, int y, int height, int width, String text) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.text = new Text(text);
	}
	
}
