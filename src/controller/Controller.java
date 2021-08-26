package controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.DirectoryModel;
import model.ImageModel;
import model.ImageSnapShot;
import model.ModelManager;

/**
 * A Controller to limit interaction with the model
 *
 */
public class Controller {

	private ModelManager imageManager;

	// Most actions are applied to the reference
	// Which is behaves like the currently selected file
	private ObservableReference reference;
	private DirectoryModel activeDirectory;

	/**
	 * Creates a new Controller object
	 *
	 */
	public Controller() {
		this.imageManager = ModelManager.instance;
		this.reference = new ObservableReference();
	}

	/**
	 * Adds a new tag to the currently selected image. If the tag already exists,
	 * nothing is done
	 *
	 * @param tag
	 *            the tag to be added
	 */
	public void addTag(String tag) {
		ImageModel image = reference.getValue().getImage();
		imageManager.addTag(tag);
		if (image != null) {
			try {
				image.addTag(tag);
				reference.update();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Removes a tag to the currently selected image. If the tag does not exist,
	 * nothing is done.
	 *
	 * @param tag
	 *            the tag to be added
	 */
	public void deleteTag(String tag) {
		ImageModel image = reference.getValue().getImage();
		if (image != null) {
			try {
				image.deleteTag(tag);
				reference.update();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Return a ObservableList of all the logs ever logged. NOTE: This is only for
	 * the View methods! The returned list is NOT tied to the model
	 *
	 * @return a ObservableList of all the logs ever logged.
	 */
	public ObservableList<String> getAllLogs() {

		List<String> list = new LinkedList<String>();
		for (ImageSnapShot entry : ImageSnapShot.getAllHistory()) {
			list.add(entry.toString());
		}

		return FXCollections.observableList(list);
	}

	/**
	 * Return a ObservableList of all the tags of the currently selected image. If
	 * none is selected, returns null. NOTE: This is only for the View methods! The
	 * returned list is NOT tied to the model
	 *
	 * @return a ObservableList of all tags for the currently selected image.
	 */
	public ObservableList<String> getCurrentTags() {
		ImageModel image = reference.getValue().getImage();
		if (image != null) {
			return image.getObservableTags();
		}

		return null;
	}

	/**
	 * Return a ObservableList of all the tags of a given image. NOTE: Not
	 * necessarily the referenced image!
	 *
	 * @return a ObservableList of all tags for the given image.
	 */
	public ObservableList<String> getCurrentTags(ImageModel image) {
		if (image != null) {
			return image.getObservableTags();
		}
		return null;
	}

	/**
	 * Return a List of all tags in the active directory.
	 *
	 * @return a List of all tags
	 */
	public List<String> getAllCurrentTags() {
		return new LinkedList<>(imageManager.getTags());
	}

	/**
	 * Returns the absolute path of the currently selected image,
	 *
	 * @return a String representation of the absolute path to the currently
	 *         selected image.
	 */
	public String getAbsolutePath() {
		return reference.getValue().getImage().getPath().toString();
	}

	/**
	 * Return an ImageModel object for the currently selected image
	 *
	 * @return an ImageModel object for the currently selected image
	 */
	public ImageModel getImage() {
		return reference.getValue().getImage();
	}

	/**
	 * Return a ObservableList of all the logs for this image in reverse
	 * chronological order. NOTE: This is only for the View methods! The returned
	 * list is NOT tied to the model
	 *
	 * @return a ObservableList of all the logs for the currently selected image.
	 */
	public ObservableList<String> getImageLog() {
		return FXCollections.observableList(this.getImageLog(reference.getValue().getImage()));
	}

	/**
	 * Return a List of all the logs for this image in reverse chronological order.
	 *
	 * @return a List of all the logs for the currently selected image.
	 */
	private List<String> getImageLog(ImageModel image) {
		if (image != null) {
			List<String> history = new ArrayList<>();
			for (ImageSnapShot snapshot : image.getHistory()) {
				history.add(snapshot.toString());
			}
			return history;
		}

		return Collections.emptyList();
	}

	/**
	 * Move the currently selected image to the directory at the given directory
	 * path
	 *
	 * @param targetDirectory
	 *            the given directory path
	 */
	public void moveImage(Path targetDirectory) {
		ImageModel image = reference.getValue().getImage();
		if (image != null) {
			try {
				image.moveToDir(targetDirectory);
				reference.update();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Registers a reference listener to the reference of this controller
	 *
	 * @param listener
	 *            the reference listener for with this controller's reference
	 */
	public void registerReferenceListener(Listener<Reference> listener) {
		this.reference.addListener(listener);
	}

	/**
	 * Attempts to revert the currently selected image to the {rank}'th latest
	 * revision
	 *
	 * @param rank
	 *            the rank of the revision to revert to
	 */
	public void revert(int rank) {
		ImageModel image = reference.getValue().getImage();
		if (image != null) {
			try {
				image.revertTo(image.getHistory().getSnap(rank));

				reference.update();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Set the active directory of this controller to the given path
	 *
	 * @param path
	 *            the path to set the active directory of this controller to
	 */
	public void setActiveDirectory(Path path) {
		try {
			DirectoryModel dir = imageManager.getDir(path);
			if (dir == null) {
				imageManager.addDir(path);
				dir = imageManager.getDir(path);
			}
			this.activeDirectory = dir;
			reference.update(new Reference(dir));
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	/**
	 * Set the currently selected image to the given image
	 *
	 * @param img
	 *            the new currently selected image
	 */
	public void setImageReference(ImageModel img) {
		this.reference.update(new Reference(reference.getValue().getDirectory(), img));
	}
}
