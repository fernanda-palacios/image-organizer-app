package controller;

import model.DirectoryModel;
import model.ImageModel;

/**
 * A Reference to a directory and optionally an image in that directory
 *
 */
public class Reference {

	private DirectoryModel directory;
	private ImageModel image;

	/**
	 * Creates a null reference
	 * 
	 */
	public Reference() {
		this(null, null);
	}

	/**
	 * Create a reference pointing to a directory
	 * 
	 * @param directory
	 *            the directory to point to
	 */
	public Reference(DirectoryModel directory) {
		this(directory, null);
	}

	/**
	 * Create a reference pointing to an Image and a Directory
	 * 
	 * @param directory
	 *            the directory to point to
	 * @param image
	 *            the image to point to
	 */
	public Reference(DirectoryModel directory, ImageModel image) {
		this.directory = directory;
		this.image = image;
	}

	/**
	 * Create a reference to an image and it's parent directory
	 * 
	 * @param image
	 *            the image to point to
	 */
	public Reference(ImageModel image) {
		this(image.getDirectory(), image);
	}

	/**
	 * Return the directory this Reference points to
	 * 
	 * @return the directory this Reference points to
	 */
	public DirectoryModel getDirectory() {
		return directory;
	}

	/**
	 * Return the directory this Reference points to. May be null
	 * 
	 * @return the directory this Reference points to. May be null
	 */
	public ImageModel getImage() {
		return image;
	}

}
