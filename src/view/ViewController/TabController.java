package view.ViewController;

import controller.Controller;

/** Abstract TabController class. */
public abstract class TabController {

	/** a Controller object. */
	protected Controller controller;

	/**
	 * Initializes a TabController.
	 *
	 * @param controller the controller to be used in this TabController
	 */
	public void init(Controller controller) {
		this.controller = controller;
	}
}
