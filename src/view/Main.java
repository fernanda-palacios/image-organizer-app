package view;

import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.FileAccesor;
import model.ImageSnapShot;
import model.ModelManager;

/** A Main class to execute application. */
public class Main extends Application {

	/**
	 * Method to load previous data if available.
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		ModelManager.instance.setAccesor(new FileAccesor());

		try {
			ModelManager.instance.loadFromDisk();
		} catch (ClassNotFoundException | IOException e) {
			//e.printStackTrace();
		}

		// Add a shutdownhook to save ModelManager's state and the log
		Thread t0 = new Thread() {
			@Override
			public void run() {
				try {
					ModelManager.instance.saveToDisk();
					PrintWriter out = new PrintWriter("log.txt");
					StringBuilder logs = new StringBuilder();
					boolean first = true;
					for (ImageSnapShot snap : ImageSnapShot.getAllHistory()) {
						if (first) {
							first = false;
						} else {
							logs.append(System.lineSeparator());
						}
						logs.append(snap.toString());
					}
					out.print(logs.toString());
					out.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(t0);

		launch(args);
	}

	/**
	 * Launch the GUI.
	 *
	 * @param primaryStage the primaryStage of the GUI
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/view/MainView/MainView.fxml"));
		primaryStage.setTitle("Viewer");
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
