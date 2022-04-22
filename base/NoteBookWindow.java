package base;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * NoteBook GUI with JAVAFX
 *
 * COMP 3021
 *
 * @author valerio
 */
public class NoteBookWindow extends Application {
	/**
	 * TextArea containing the note
	 */
	final TextArea textAreaNote = new TextArea("");
	/**
	 * list view showing the titles of the current folder
	 */
	final ListView<String> titleslistView = new ListView<String>();
	/**
	 * Combobox for selecting the folder
	 */
	final ComboBox<String> foldersComboBox = new ComboBox<String>();
	/**
	 * Stage
	 */
	Stage stage;
	/**
	 * This is our Notebook object
	 */
	NoteBook noteBook = null;
	/**
	 * current folder selected by the user
	 */
	String currentFolder = "";
	/**
	 * current note selected by the user
	 */
	String currentNote = "";
	/**
	 * current search string
	 */
	String currentSearch = "";

	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		loadNoteBook();
		// Use a border pane as the root for scene
		BorderPane border = new BorderPane();
		// add top, left and center
		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addGridPane());

		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("NoteBook COMP 3021");
		stage.show();
	}

	/**
	 * This create the top section
	 *
	 * @return The top HBox
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		Button buttonLoad = new Button("Load from File");
		buttonLoad.setPrefSize(100, 20);
		buttonLoad.setOnAction(e -> {
			FileChooser chooser = new FileChooser();
			chooser.setInitialDirectory(new File("."));
			chooser.setTitle("Please choose a file which contains the notebook object");
			chooser.getExtensionFilters()
				   .add(new FileChooser.ExtensionFilter("Serialised Object File (*.ser)", "*.ser"));

			File f = chooser.showOpenDialog(stage);

			if (f != null) {
				loadNoteBook(f);
				updateComboBox();
			}
		});
		Button buttonSave = new Button("Save to File");
		buttonSave.setPrefSize(100, 20);
		buttonSave.setOnAction(e -> {
			FileChooser chooser = new FileChooser();
			chooser.setInitialDirectory(new File("."));
			chooser.setTitle("Save current notebook to file");
			chooser.getExtensionFilters()
				   .add(new FileChooser.ExtensionFilter("Serialised Object File (*.ser)", "*.ser"));

			File f = chooser.showSaveDialog(stage);

			if (f != null) {
				noteBook.save(f.getName());

				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your file has been saved to " + f.getName());
				alert.setTitle("Successfully saved");
				alert.showAndWait().ifPresent(rs -> {
					if (rs == ButtonType.OK)
						System.out.println("Pressed ok");
				});
			}
		});

		Label searchLabel = new Label("Search: ");
		TextField searchField = new TextField();
		Button searchButton = new Button("Search");
		Button clearSearchButton = new Button("Clear Search");
		searchButton.setOnAction(e -> {
			currentSearch = searchField.getText();
			updateListView();
		});
		clearSearchButton.setOnAction(e -> {
			currentSearch = "";
			searchField.clear();
			updateListView();
		});

		hbox.getChildren().addAll(buttonLoad, buttonSave, searchLabel, searchField, searchButton, clearSearchButton);

		return hbox;
	}

	/**
	 * this create the section on the left
	 *
	 * @return The left VBox
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes

		updateComboBox();
		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 != null) {
					currentFolder = t1.toString();
					updateListView();
				}
			}

		});

		Button buttonAddFolder = new Button("Add a Folder");
		buttonAddFolder.setOnAction(e -> {
			TextInputDialog input = new TextInputDialog("New Folder");
			input.setTitle("Input");
			input.setHeaderText("Add a new folder for your notebook:");
			input.setContentText("Please enter the name you want to create:");

			input.showAndWait().ifPresent(rs -> {
				if (rs.isEmpty()) {
					Alert alert = new Alert(Alert.AlertType.WARNING, "Please input a valid folder name");
					alert.showAndWait().ifPresent(type -> {
						if (type == ButtonType.OK)
							System.out.println("Pressed ok");
					});
				} else if (noteBook.addFolder(rs)) {
					updateComboBox();
					foldersComboBox.getSelectionModel().select(rs);
				} else {
					Alert alert = new Alert(Alert.AlertType.WARNING, "You already have a folder named " + rs);
					alert.showAndWait().ifPresent(type -> {
						if (type == ButtonType.OK)
							System.out.println("Pressed ok");
					});
				}
			});
		});

		HBox hbox = new HBox(foldersComboBox, buttonAddFolder);

		titleslistView.setPrefHeight(100);
		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null)
					return;
				String title = t1.toString();
				// This is the selected title

				String content = ((TextNote) noteBook.getFolders()
													 .get(foldersComboBox.getSelectionModel().getSelectedIndex())
													 .getNotes().get(titleslistView.getSelectionModel()
																				   .getSelectedIndex())).content;
				textAreaNote.setText(content);
				currentNote = title;
			}
		});
		updateListView();

		Button buttonAddNote = new Button("Add a Note");
		buttonAddNote.setOnAction(e -> {
			if (foldersComboBox.getSelectionModel().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a folder first!");
				alert.showAndWait().ifPresent(type -> {
					if (type == ButtonType.OK)
						System.out.println("Pressed ok");
				});
			} else {
				TextInputDialog input = new TextInputDialog("New Note");
				input.setTitle("Input");
				input.setHeaderText("Add a new note for current folder:");
				input.setContentText("Please enter the name of the note:");

				input.showAndWait().ifPresent(rs -> {
					if (rs.isEmpty()) {
						Alert alert = new Alert(Alert.AlertType.WARNING, "Please input a valid note name");
						alert.showAndWait().ifPresent(type -> {
							if (type == ButtonType.OK)
								System.out.println("Pressed ok");
						});
					} else if (noteBook.createTextNote(currentFolder, rs)) {
						Alert alert = new Alert(Alert.AlertType.INFORMATION,
												"Inserted " + rs + " to folder " + currentFolder + " successfully!");
						alert.showAndWait().ifPresent(type -> {
							if (type == ButtonType.OK)
								System.out.println("Pressed ok");
						});
						updateListView();
					} else {
						Alert alert = new Alert(Alert.AlertType.WARNING, "You already have a folder named " + rs);
						alert.showAndWait().ifPresent(type -> {
							if (type == ButtonType.OK)
								System.out.println("Pressed ok");
						});
					}
				});
			}
		});

		vbox.getChildren().add(new Label("Choose folder: "));
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().addAll(titleslistView, buttonAddNote);

		return vbox;
	}

	private void updateComboBox() {
		foldersComboBox.getItems()
					   .setAll(noteBook.getFolders().stream().map(Folder::getName).collect(Collectors.toList()));
		foldersComboBox.getSelectionModel().select(0);
	}

	private void updateListView() {
		ArrayList<String> list = new ArrayList<String>();

		Folder f = noteBook.getFolders().get(foldersComboBox.getSelectionModel().getSelectedIndex());
		for (Note n : currentSearch.isEmpty() ? f.getNotes() : f.searchNotes(currentSearch)) {
			if (n instanceof TextNote)
				list.add(n.getTitle());
		}

		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		titleslistView.setItems(combox2);
		textAreaNote.setText("");
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */
	private GridPane addGridPane() {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));

		ImageView saveView = new ImageView(new Image(new File("save.png").toURI().toString()));
		saveView.setFitHeight(18);
		saveView.setFitWidth(18);
		saveView.setPreserveRatio(true);
		grid.add(saveView, 0, 0);

		Button buttonSaveNote = new Button("Save Note");
		buttonSaveNote.setOnAction(e -> {
			if (!foldersComboBox.getSelectionModel().isEmpty() && !titleslistView.getSelectionModel().isEmpty())
				((TextNote) noteBook.getFolders().get(foldersComboBox.getSelectionModel().getSelectedIndex()).getNotes()
									.get(titleslistView.getSelectionModel()
													   .getSelectedIndex())).content = textAreaNote.getText();
			else {
				Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a folder and a note");
				alert.showAndWait().ifPresent(type -> {
					if (type == ButtonType.OK)
						System.out.println("Pressed ok");
				});
			}
		});
		grid.add(buttonSaveNote, 1, 0);

		ImageView deleteView = new ImageView(new Image(new File("delete.png").toURI().toString()));
		deleteView.setFitHeight(18);
		deleteView.setFitWidth(18);
		deleteView.setPreserveRatio(true);
		grid.add(deleteView, 2, 0);

		Button buttonDeleteNote = new Button("Delete Note");
		buttonDeleteNote.setOnAction(e -> {
			if (!foldersComboBox.getSelectionModel().isEmpty() && !titleslistView.getSelectionModel().isEmpty()) {
				if (noteBook.getFolders().get(foldersComboBox.getSelectionModel().getSelectedIndex())
							.deleteNote(currentNote)) {
					// What's the point of this being confirmation???
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your note have been successfully removed");
					alert.showAndWait();
					updateListView();
				}
			}
		});
		grid.add(buttonDeleteNote, 3, 0);

		textAreaNote.setEditable(true);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		grid.add(textAreaNote, 0, 1, 4, 1);

		return grid;
	}

	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
						  "Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
						  "Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
						  "Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called “the most shocking play in NFL history” and the Washington Redskins dubbed the “Throwback Special”: the November 1985 play in which the Redskins’ Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
						  "The acclaimed New York Times bestselling and National Book Award–winning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everything—until it wasn’t. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliant—a part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwether’s Daddy Was a Number Runner and Dorothy Allison’s Bastard Out of Carolina, Jacqueline Woodson’s Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthood—the promise and peril of growing up—and exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
						  "What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;

	}

	private void loadNoteBook(File f) {
		noteBook = new NoteBook(f.getName());
	}
}
