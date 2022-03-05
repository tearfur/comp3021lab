package base;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Folder implements Comparable<Folder> {
	private ArrayList<Note> notes;
	private String name;

	public Folder(String name) {
		this.name = name;
		notes = new ArrayList<>();
	}

	public void addNote(Note note) {
		notes.add(note);
	}

	public String getName() {
		return name;
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}

	public void sortNotes() {
		notes.sort(null);
	}

	public List<Note> searchNotes(String keywords) {
		ArrayList<Note> ret = new ArrayList<>();

		for (Note note : notes) {
 			boolean add = false;
			for (String and : keywords.toLowerCase(Locale.ENGLISH).split("(?<!or)\\s+(?!or)")) {
				add = false;
				for (String or : and.split("\\s+or\\s+")) {
					if (note.getTitle().toLowerCase(Locale.ENGLISH).contains(or) ||
							note instanceof TextNote && ((TextNote) note).content.toLowerCase(Locale.ENGLISH).contains(or)) {
						add = true;
						break;
					}
				}
				if (!add)
					break;
			}

			if (add)
				ret.add(note);
		}

		return ret;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Folder)) return false;
		Folder folder = (Folder) o;
		return name.equals(folder.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		int nText = 0, nImage = 0;

		for (Note note : notes) {
			if (note instanceof TextNote)
				++nText;
			else if (note instanceof ImageNote)
				++nImage;
		}

		return name + ":" + nText + ":" + nImage;
	}

	@Override
	public int compareTo(Folder o) {
		return name.compareTo(o.name);
	}
}
