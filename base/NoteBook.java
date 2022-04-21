package base;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NoteBook implements Serializable {
	private ArrayList<Folder> folders;

	private static final long serialVersionUID = 1;

	public NoteBook() {
		folders = new ArrayList<>();
	}

	/**
	 * Constructor of an object
	 * NoteBook from an object serialization on disk
	 *
	 * @param file the path of the file for loading the object serialization
	 */
	public NoteBook(String file) {
		try (
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(fis)
		) {
			NoteBook n = (NoteBook) in.readObject();
			folders = n.folders;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean createTextNote(String folderName, String title) {
		return insertNote(folderName, new TextNote(title));
	}

	public boolean createTextNote(String folderName, String title, String content) {
		return insertNote(folderName, new TextNote(title, content));
	}

	public boolean createImageNote(String folderName, String title) {
		return insertNote(folderName, new ImageNote(title));
	}

	public ArrayList<Folder> getFolders() {
		return folders;
	}

	public boolean addFolder(String name) {
		for (Folder folder : folders) {
			if (folder.getName().equals(name))
				return false;
		}

		folders.add(new Folder(name));
		return true;
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

	public void sortFolders() {
		folders.sort(null);
	}

	public List<Note> searchNotes(String keyword) {
		List<Note> ret = new ArrayList<>();

		for (Folder folder : folders)
			ret.addAll(folder.searchNotes(keyword));

		return ret;
	}

	/**
	 * method to save the NoteBook instance to file
	 *
	 * @param file the path of the file where to save the object serialization
	 * @return true if save on file is successful, false otherwise
	 */
	public boolean save(String file) {
		try (
				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream out = new ObjectOutputStream(fos)
		) {
			out.writeObject(this);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
