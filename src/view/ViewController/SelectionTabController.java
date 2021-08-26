package view.ViewController;

import controller.Controller;
import controller.Listener;
import controller.Observable;
import controller.Reference;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import model.DirectoryModel;
import model.ImageModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** Tab2 to list images under given directory in GUI */
public class SelectionTabController extends TabController implements Listener<Reference> {

	/** List of the images to be displayed. */
	private List<ImageModel> imageReferences;

	/** The Main Controller that manages the tab controllers. */
	private MainViewController mainController;

	/** Allows the user to select a directory to find images under. */
	private DirectoryChooser directoryChooser;

	/** List of the tags to filter the images with. */
	@FXML
	ChoiceBox<String> choiceBoxFilterTag;

	/** The name of the directory that is selected. */
	@FXML
	Label lblCurrDir;

	/** ListView with the names of all the images under the selected directory. */
	@FXML
	ListView<String> listViewImages;

	/** Button that allows the user to select a directory. */
	@FXML
	Button btnBrowse;

	/**
	 * Constructor for a SelectionTabController.
	 */
	public SelectionTabController() {
		super();
		directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(Paths.get(".").toFile());
		imageReferences = new LinkedList<>();
	}

	/**
	 * Opens a directoryChooser when the Browse button is clicked.
	 */
	@FXML
	private void btnBrowseClicked() throws IOException {
		File selectedDirectory = directoryChooser.showDialog(btnBrowse.getScene().getWindow());
		if (selectedDirectory != null) {
			controller.setActiveDirectory(selectedDirectory.toPath());
		}
	}

	/**
	 * Initializes a SelectionTabController with a mainController and controller.
	 * @param controller its controller
	 * @param mainController its mainViewController
	 */
	public void init(Controller controller, MainViewController mainController) {
		super.init(controller);
		this.controller.registerReferenceListener(this);
		this.mainController = mainController;
	}

	/**
	 * When an image from the listview is clicked display the next tab on the TabPane and display the selected Image.
	 */
	@FXML
	private void listViewImagesClicked() {
		if (!listViewImages.getSelectionModel().isEmpty()) {
			ImageModel image = imageReferences.get(listViewImages.getSelectionModel().getSelectedIndex());
			controller.setImageReference(image);
			mainController.nextTab();
		}

	}

	/**
	 * Updates the SelectionTab when there is a change. It produced the list of the images under the selected directory
	 * and also populates the tags being used in the directory to use them as filters.
	 * @param reference
	 */
	@Override
	public void notify(Observable<Reference> reference) {
		DirectoryModel directory = reference.getValue().getDirectory();
		if (directory != null) {
			directoryChooser.setInitialDirectory(directory.getPath().toFile());

			List<String> imageNames = new ArrayList<>();
			imageReferences = new LinkedList<>();
			for (ImageModel img : directory.getImagesRecursive()) {
				imageNames.add(img.toString());
				imageReferences.add(img);
			}

			listViewImages.setItems(FXCollections.observableList(imageNames));
			lblCurrDir.setText("Images under directory: " + directory.toString());

			// populate the choice box with all the tags in all the images in the active
			// directory
			List<String> allCurrentTags = controller.getAllCurrentTags();
			allCurrentTags.add(0, "No tag selected");

			choiceBoxFilterTag.setItems(FXCollections.observableList(allCurrentTags));
			choiceBoxFilterTag.getSelectionModel().selectFirst();

			// update the listView depending on if an item is selected from the choice box
			choiceBoxFilterTag.setOnAction(event -> {
				String filterTag = null;
				List<String> filteredNames = new ArrayList<>();
				imageReferences = new LinkedList<>();

				// if an item is selected then set imageReferences to be only those images that
				// contain the tag selected
				if (!choiceBoxFilterTag.getSelectionModel().isEmpty()) {
					filterTag = choiceBoxFilterTag.getSelectionModel().getSelectedItem();
				}

				if (Objects.equals(filterTag, "No tag selected")) {
					for (ImageModel image : directory.getImagesRecursive()) {
						filteredNames.add(image.toString());
						imageReferences.add(image);
					}
				} else if (filterTag != null && !filterTag.trim().isEmpty()) {
					for (ImageModel image : directory.getImagesRecursive()) {
						if (controller.getCurrentTags(image).contains(filterTag)) {
							filteredNames.add(image.toString());
							imageReferences.add(image);
						}
					}
				}
				listViewImages.setItems(FXCollections.observableList(filteredNames));
			});

		}
	}
}