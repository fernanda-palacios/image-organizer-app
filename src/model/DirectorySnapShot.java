package model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * A SnapShot of a DirectoryModel at a given time
 *
 */
public class DirectorySnapShot extends SnapShot<DirectoryModel, DirectorySnapShot> {

	/**
	 * Makes DirectorySnapShots
	 *
	 */
	static class DirectorySnapShotFactory implements Factory<DirectoryModel, DirectorySnapShot> {
		private static final long serialVersionUID = 2144674609334325697L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see model.Factory#makeSnap(java.lang.Object, model.SnapShot)
		 */
		@Override
		public DirectorySnapShot makeSnap(DirectoryModel subject, DirectorySnapShot lastSnap) {
			return new DirectorySnapShot(subject, lastSnap);
		}

	}

	private static final long serialVersionUID = 2699004234873560168L;

	private final String path;
	private final String name;
	private final DirectoryModel parent;
	private final DirectoryModel[] subdirectories;
	private final ImageModel[] images;

	/**
	 * Create a new DirectorySnapShot of the given subject compared to the given
	 * last snapshot
	 * 
	 * @param subject
	 *            the given subject
	 * @param lastSnap
	 *            the given last snapshot
	 */
	private DirectorySnapShot(DirectoryModel subject, DirectorySnapShot lastSnap) {
		super(subject, lastSnap);
		path = subject.getPath().toString();
		parent = subject.getDirectory();
		name = subject.getName();
		subdirectories = subject.getSubdirectories().toArray(new DirectoryModel[0]);
		images = subject.getImages().toArray(new ImageModel[0]);
	}

	/**
	 * Return all images under that were under this snapshot's subject when it was
	 * taken
	 * 
	 * @return all images under that were under this snapshot's subject when it was
	 *         taken
	 */
	public TreeSet<ImageModel> getImages() {
		return new TreeSet<ImageModel>(Arrays.asList(images.clone()));
	}

	/**
	 * Return the name of the subject when this snapshot was taken
	 * 
	 * @return the name of the subject when this snapshot was taken
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the parent of the subject when this snapshot was taken
	 * 
	 * @return the parent of the subject when this snapshot was taken
	 */
	public DirectoryModel getParent() {
		return parent;
	}

	/**
	 * Return the path of the subject when this snapshot was taken
	 * 
	 * @return the path of the subject when this snapshot was taken
	 */
	public Path getPath() {
		if (parent != null) {
			return parent.getPath().resolve(name);
		}
		return Paths.get(path);
	}

	/**
	 * Return the subdirectories of the subject when this snapshot was taken
	 * 
	 * @return the subdirectories of the subject when this snapshot was taken
	 */
	public TreeSet<DirectoryModel> getSubdirectories() {
		return new TreeSet<DirectoryModel>(Arrays.asList(subdirectories.clone()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		DirectorySnapShot lastSnap = getLastSnap();
		if (lastSnap == null) {
			return String.format("%2$tc create \"%1$s\"", this.name, getDate());
		}

		String result = String.format("%2$tc edit \"%1$s\"", lastSnap.name, getDate());

		if (lastSnap.parent != this.parent) {
			result += String.format("%n\tMove from:\t%1$s %n\tto:\t\t%2$s", lastSnap.parent, this.parent);
		}

		if (!Arrays.equals(lastSnap.subdirectories, this.subdirectories)) {
			result += String.format("%n\tChange subdirectories from %1$s to %2$s",
					Arrays.toString(lastSnap.subdirectories), Arrays.toString(this.subdirectories));
		}

		if (!Arrays.equals(lastSnap.images, this.images)) {
			result += String.format("%n\tChange images from %1$s to %2$s", Arrays.toString(lastSnap.images),
					Arrays.toString(this.images));
		}
		return result;
	}

}
