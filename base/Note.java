package base;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Note implements Comparable<Note>, Serializable {
	private Date date;
	private String title;

	private static final long serialVersionUID = 1;

	public Note(String title) {
		this.title = title;
		date = new Date(System.currentTimeMillis());
	}

	public String getTitle() {
		return title;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Note))
			return false;
		Note note = (Note)o;
		return title.equals(note.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title);
	}

	@Override
	public int compareTo(Note o) {
		return date.compareTo(o.date);
	}

	@Override
	public String toString() {
		return date.toString() + '\t' + title;
	}
}
