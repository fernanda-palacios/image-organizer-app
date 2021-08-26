package view.ViewController;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/** MainViewController of TabViewControllers */
public class MainViewController {

	/** TabPane that contains all the tabs of the GUI.*/
	@FXML
	TabPane tabsTabPane;

	/** Controller for the SelectionTabView.fxml*/
	@FXML
	private SelectionTabController SelectionTabViewController;

	/** Controller for the ImageTabView.fxml*/
	@FXML
	private ImageTabController ImageTabViewController;

	/** Controller for the ImageHistoryTabView.fxml*/
	@FXML
	private ImageHistoryTabController ImageHistoryTabViewController;

	/** Controller for the HistoryTabView.fxml*/
	@FXML
	private HistoryTabController HistoryTabViewController;

	/**
	 * Set up Main View.
	 */
	@FXML
	public void initialize() {
		System.out.println("Application started");

		Controller controller = new Controller();

		SelectionTabViewController.init(controller, this);
		ImageTabViewController.init(controller);
		ImageHistoryTabViewController.init(controller);
		HistoryTabViewController.init(controller);
	}

	/**
	 * Displays the next tab on the tabsTabPane.
	 */
	void nextTab() {
		tabsTabPane.getSelectionModel().selectNext();
	}
}
