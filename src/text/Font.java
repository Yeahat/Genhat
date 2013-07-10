package text;

import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

// A Font is an image containing symbols, used to render a Text.
public class Font {
	private String name; // the proper name of the Font (for reference)
	private String filename; // The filename of the image used for this Font, "foo.png"
					 // Should be inside "/graphics/fonts/"
	private int charWidth; // the width of each symbol
	private int charHeight; // the height of each symbol
	private Texture fontTexture; // the texture to be used for this font

	// constructor
	public Font(String name, String filename, int charWidth, int charHeight) {
		this.name = name;
		this.filename = filename;
		this.charWidth = charWidth;
		this.charHeight = charHeight;
		this.loadTexture();
	}
	
	// getters
	public String getName() { return name; }
	public String getFilename() { return filename; }
	public int getWidth() { return charWidth; }
	public int getHeight() { return charHeight; }
	
	// load font texture, or throw exception if unable to do so
	public void loadTexture()
	{
		String filepath = "graphics/fonts/".concat(filename);
		try {
			fontTexture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(filepath));
		} catch (IOException e) {e.printStackTrace();}
	}
	
}
