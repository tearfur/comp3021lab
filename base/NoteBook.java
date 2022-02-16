package base;

import java.util.ArrayList;

public class NoteBook {
	private ArrayList<Folder> folders;

	public NoteBook() {
		folders = new ArrayList<>();
	}

	public boolean createTextNote(String folderName, String title) {
		return insertNote(folderName, new TextNote(title));
	}

	public boolean createImageNote(String folderName, String title) {
		return insertNote(folderName, new ImageNote(title));
	}

	public ArrayList<Folder> getFolders() {
		return folders;
	}

	public boolean insertNote(String folderName, Note note) {
		Folder f = null;

		for (Folder folder : folders) {
			if (folder.getName().equals(folderName)) {
				f = folder;
				break;
			}
		}

		if (f == null) {
			f = new Folder(folderName);
			folders.add(f);
		} else {
			for (Note n : f.getNotes()) {
				if (n.equals(note)) {
					System.out.println("Creating note " + note.getTitle() + " under folder " + folderName + " failed");
					return false;
				}
			}
		}

		f.addNote(note);
		return true;
	}
}