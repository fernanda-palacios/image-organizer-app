package view.ViewController;

import controller.Controller;
import controller.Listener;
import controller.Observable;
import controller.Reference;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/** Tab5 to view all history in GUI */
public class HistoryTabController extends TabController implements Listener<Reference> {

	/** The logs of all the renaming done. */
	@FXML
	ListView<String> totalHistory;

	/**
	 * Initializes a HistoryTabController.
	 * @param controller
	 */
	@Override
	public void init(Controller controller) {
		super.init(controller);
		this.controller.registerReferenceListener(this);
		totalHistory.setItems(controller.getAllLogs());
	}

	/**
	 * Update the logs when an image is renamed.
	 * @param obsReference is being observed
	 */
	@Override
	public void notify(Observable<Reference> obsReference) {
		totalHistory.setItems(this.controller.getAllLogs());
	}
}
