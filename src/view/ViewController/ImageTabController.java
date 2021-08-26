package view.ViewController;

import controller.Controller;
import controller.Listener;
import controller.Observable;
import controller.Reference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import model.ImageModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/** Tab3 to view and make changes to an image in GUI */
public class ImageTabController extends TabController implements Listener<Reference> {

	/**
	 * List of all tags that can be added.
	 */
	@FXML
	private ListView<Item> listViewAddTags;

	/**
	 * The absolute path of the reference.
	 */
	@FXML
	private Label labelAbsolutePath;

	/**
	 * The name of the image referenced.
	 */
	@FXML
	Label lblName;

	/**
	 * A text field for the user to enter a tag they want to add.
	 */
	@FXML
	TextField textCurrTag;

	/**
	 * A button to add a tag.
	 */
	@FXML
	Button btnAddTag;

	/**
	 * A button to remove a tag.
	 */
	@FXML
	Button btnRemoveTag;

	/**
	 * A button to move the reference to a new directory.
	 */
	@FXML
	Button btnMove;

	/**
	 * A list of all current tags assigned to this reference.
	 */
	@FXML
	private ListView<String> listTags;

	/**
	 * A JavaFX Image field for the reference image.
	 */
	@FXML
	ImageView currImage;

	/**
	 * A directory chooser to access file system.
	 */
	private DirectoryChooser directoryChooser;

	/**
	 * Initialize an ImageTabController.
	 */
	public ImageTabController() {
		super();
		directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(Paths.get(".").toFile());
	}

	/**
	 * Add a tag that has either been entered into the tag text field by the user or selected from currently existing
	 * tags.
	 *
	 * @throws IOException
	 */
	@FXML
	private void btnAddTagClicked() throws IOException {

		if (textCurrTag.getText() != null && !textCurrTag.getText().trim().isEmpty()) {
			controller.addTag(textCurrTag.getText());
			textCurrTag.setText("");
		} else {

			for (Object itemObj : listViewAddTags.getItems()) {
				Item item = (Item) itemObj;
				if (item.onProperty().getValue()) {
					controller.addTag(item.toString());
				}
			}

		}
	}

	/**
	 * Moves this reference to whatever directory is selected.
	 *
	 * @throws IOException
	 */
	@FXML
	private void btnMoveClicked() throws IOException {
		File selectedDirectory = directoryChooser.showDialog(btnMove.getScene().getWindow());
		if (selectedDirectory != null) {
			controller.moveImage(selectedDirectory.toPath());

		}
	}

	/**
	 * Removes the tag that has been selected from the list.
	 *
	 * @throws IOException
	 */
	@FXML
	private void btnRemoveTagClicked() throws IOException {
		if (!listTags.getSelectionModel().isEmpty()) {
			String selected_tag = listTags.getSelectionModel().getSelectedItem();
			controller.deleteTag(selected_tag);
			listTags.getItems().remove(selected_tag);
			textCurrTag.setText("");
		}
	}

	/**
	 * Initializes the controller to be used in this TabController and registers this ImageTabController to be a
	 * listener for the reference.
	 *
	 * @param controller the controller to be used in this TabController
	 */
	@Override
	public void init(Controller controller) {
		super.init(controller);
		this.controller.registerReferenceListener(this);
		labelAbsolutePath.setWrapText(true);
	}


	/**
	 * Update the 'View Image' tab when the reference is changed and/or updated.
	 * @param obsReference is being observed
	 */
	@Override
	public void notify(Observable<Reference> obsReference) {
		Reference reference = obsReference.getValue();
		ImageModel model = reference == null ? null : reference.getImage();
		String name = model == null ? "" : model.toString();
		lblName.setText("Name: " + name);

		Image image = null;
		try {
			image = model == null ? null : model.toJavaFXImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		currImage.setImage(image);
		if (reference != null && reference.getImage() != null) {
			labelAbsolutePath.setText(controller.getAbsolutePath());
		}
		ObservableList<String> tags = model == null ? FXCollections.observableArrayList() : model.getObservableTags();
		listTags.setItems(tags);

		// populate the choice box with all the current tags in every image in the
		// active directory
		List<String> allCurrentTags = controller.getAllCurrentTags();

		for (String tag : allCurrentTags) {
			Item item = new Item(tag, false);
			Boolean added = false;

			for (Object obj : listViewAddTags.getItems()) {
				if (obj.toString().equals(tag)) {
					added = true;
				}
			}

			if (!added) {
				listViewAddTags.getItems().add(item);
			}
		}
		listViewAddTags.setCellFactory(CheckBoxListCell.forListView(Item::onProperty));
	}

	/**
	 * An Item class.
	 */
	// taken from
	// https://stackoverflow.com/questions/28843858/javafx-8-listview-with-checkboxes
	public static class Item {
		private final StringProperty name = new SimpleStringProperty();
		private final BooleanProperty on = new SimpleBooleanProperty();

		Item(String name, boolean on) {
			setName(name);
			setOn(on);
		}

		final StringProperty nameProperty() {
			return this.name;
		}

		public final String getName() {
			return this.nameProperty().get();
		}

		public final void setName(final String name) {
			this.nameProperty().set(name);
		}

		final BooleanProperty onProperty() {
			return this.on;
		}

		public final boolean isOn() {
			return this.onProperty().get();
		}

		public final void setOn(final boolean on) {
			this.onProperty().set(on);
		}

		@Override
		public String toString() {
			return getName();
		}

	}

}
