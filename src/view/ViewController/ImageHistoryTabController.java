package view.ViewController;

import controller.Controller;
import controller.Listener;
import controller.Observable;
import controller.Reference;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;

/** Tab4 to view history and revert changes of an image in GUI */
public class ImageHistoryTabController extends TabController implements Listener<Reference> {

	/** Revert Name button. */
	@FXML
	Button btnRevertName;
	/** The ListView with all the renaming of the chosen image. */
	@FXML
	ListView<String> listNames;

	/**
	 * Revert to a name of a image before a specific name change.
	 * @throws IOException
	 */
	@FXML
	private void btnRevertNameClicked() throws IOException {
		if (!listNames.getSelectionModel().isEmpty()) {
			int index = listNames.getSelectionModel().getSelectedIndex();
			controller.revert(index);
		}
	}

	/**
	 * Initialize an ImageHistoryTabController.
	 * @param controller Controller instance
	 */
	@Override
	public void init(Controller controller) {
		super.init(controller);
		this.controller.registerReferenceListener(this);
	}

	/**
	 * Update the logs when the selected image is renamed.
	 * @param obsReference is being observed
	 */
	@Override
	public void notify(Observable<Reference> obsReference) {
		listNames.setItems(this.controller.getImageLog());
	}
}