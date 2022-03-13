package base;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextNote extends Note {
	String content;

	public TextNote(String title) {
		super(title);
	}

	public TextNote(String title, String content) {
		this(title);
		this.content = content;
	}

	/**
	 * load a TextNote from File f
	 *
	 * the tile of the TextNote is the name of the file
	 * the content of the TextNote is the content of the file
	 *
	 * @param f File
	 */
	public TextNote(File f) {
		super(f.getName());
		this.content = getTextFromFile(f.getAbsolutePath());
	}

	/**
	 * get the content of a file
	 *
	 * @param absolutePath absolute path of the file
	 * @return the content of the file
	 */
	public String getTextFromFile(String absolutePath) {
		try {
			return new String(Files.readAllBytes(Paths.get(absolutePath)), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * export text note to file
	 *
	 * @param pathFolder path of the folder where to export the note
	 *                   the file has to be named as the title of the note with extension ".txt"
	 *
	 *                   if the tile contains white spaces " " they have to be replaced with underscores "_"
	 */
	public void exportTextToFile(String pathFolder) {
		try {
			Files.write(Paths.get((pathFolder.isEmpty() ? "." : pathFolder) + File.separator
								  + getTitle().replaceAll(" ", "_") + ".txt"),
						content.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
