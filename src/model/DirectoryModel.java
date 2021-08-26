package model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A model to represent a directory in the filesystem
 *
 */
public class DirectoryModel implements Model<DirectoryModel, DirectorySnapShot>, Serializable {

	private static final long serialVersionUID = 2033330810027917029L;
	private static final Logger LOGGER = Logger.getLogger(DirectoryModel.class.getName());
	static {
		LOGGER.setLevel(Level.OFF);
	}
	private String path;
	private String name;
	private DirectoryModel parent;
	private TreeSet<DirectoryModel> subdirectories;
	private TreeSet<ImageModel> images;

	private History<DirectoryModel, DirectorySnapShot> history;

	/**
	 * Create a DirectoryModel for an existing directory
	 * 
	 * @param path
	 *            the path to the existing directory
	 * @throws IOException
	 *             thrown when an invalid path is provided or there are issues
	 *             accessing the provided path
	 */
	protected DirectoryModel(Path path) throws IOException {
		LOGGER.log(Level.FINE, "Create object {0}", path.getFileName());

		if (!ModelManager.instance.getAccesor().exists(path))
			throw new NoSuchFileException(path.toString());

		if (!ModelManager.instance.getAccesor().isDirectory(path))
			throw new NotDirectoryException(path.toString());

		path = ModelManager.instance.getAccesor().toRealPath(path);

		this.path = path.toString();
		this.name = path.getFileName().toString();
		this.parent = ModelManager.instance.getDir(path.getParent());
		this.subdirectories = new TreeSet<DirectoryModel>();
		this.images = new TreeSet<ImageModel>();
		this.history = new History<DirectoryModel, DirectorySnapShot>(this,
				new DirectorySnapShot.DirectorySnapShotFactory());
		if (parent != null) {
			parent.add(this);
		}

	}

	/**
	 * Add the given DirectoryModel as a subdirectory in this DirectoryModel
	 * 
	 * @param directoryModel
	 *            the subdirectory to add to this
	 */
	protected void add(DirectoryModel directoryModel) {
		LOGGER.log(Level.FINE, "Add subdir {0} to {1}", new Object[] { directoryModel.getName(), this.getName() });
		this.subdirectories.add(directoryModel);
		if (this.parent != null) {
			this.parent.add(this);
		}
		history.log();
	}

	/**
	 * Add the given ImageModel to this DirectoryModel
	 * 
	 * @param imageModel
	 *            the image to add to this
	 */
	protected void add(ImageModel imageModel) {
		LOGGER.log(Level.FINE, "Add image {0} to {1}", new Object[] { imageModel.getName(), this.getName() });
		this.images.add(imageModel);
		if (this.parent != null) {
			this.parent.add(this);
		}
		history.log();
	}

	/**
	 * Return true iff the given model is an ancestor of this DirectoryModel,
	 * or it is this DirectoryModel object
	 * 
	 * @param model
	 *            the given model
	 * @return true iff the given mode is an ancestor of this DirectoryModel
	 */
	public boolean contains(Model<?, ?> model) {
		return model.getPath().startsWith(getPath());
	}

	/**
	 * Return true iff the given path is contained in this DirectoryModel's path,
	 * or it is this DirectoryModel's path
	 * 
	 * @param path
	 *            the given path
	 * @return true iff the given path is contained in this DirectoryModel's path
	 */
	public boolean contains(Path path) {
		return path.startsWith(getPath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof DirectoryModel) {
			return this.getPath().equals(((DirectoryModel) other).getPath());
		}
		return false;
	}

	/**
	 * Return a Set of the contents directly under this DirectoryModel
	 * 
	 * @return a Set of the contents directly under this DirectoryModel
	 */
	public Set<Model<?, ?>> getContents() {
		Set<Model<?, ?>> result = new TreeSet<>();
		result.addAll(images);
		result.addAll(subdirectories);
		return result;
	}

	/**
	 * Return a descendant of this directory at the given path
	 * 
	 * @param path
	 *            the path to the descendant of this directory
	 * @return a descendant of this directory at the given path
	 */
	public Model<?, ?> getDescendant(Path path) {
		for (ImageModel image : images) {
			if (image.getPath().equals(path)) {
				return image;
			}
		}

		for (DirectoryModel subDir : subdirectories) {
			if (subDir.getPath().equals(path)) {
				return subDir;
			} else if (subDir.contains(path)) {
				return subDir.getDescendant(path);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getDirectory()
	 */
	@Override
	public DirectoryModel getDirectory() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getDirectoryPath()
	 */
	@Override
	public Path getDirectoryPath() {
		return getPath().getParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getHistory()
	 */
	@Override
	public History<DirectoryModel, DirectorySnapShot>.HistoryViewer getHistory() {
		return history.getViewer();
	}

	/**
	 * Return the Image with the same tagged or untagged name as the given reference
	 * that is directly under this directory
	 * 
	 * @param reference
	 *            the image's reference
	 * @return the Image with the same tagged or untagged name as the given
	 *         reference that is directly under this directory
	 */
	public ImageModel getImage(String reference) {
		for (ImageModel img : getImages()) {
			if (img.getName().equals(reference) || img.getUntaggedName().equals(reference)) {
				return img;
			}
		}
		return null;
	}

	/**
	 * Return a set containing every image directly under this directory
	 * 
	 * @return a set containing every image directly under this directory
	 */
	public Set<ImageModel> getImages() {
		Set<ImageModel> result = new TreeSet<ImageModel>();
		result.addAll(images);
		return result;
	}

	/**
	 * Return a set containing every image under this directory and it's
	 * subdirectories
	 * 
	 * @return a set containing every image under this directory and it's
	 *         subdirectories
	 */
	public Set<ImageModel> getImagesRecursive() {
		Set<ImageModel> result = new TreeSet<ImageModel>();
		result.addAll(images);
		for (DirectoryModel directory : subdirectories) {
			result.addAll(directory.getImagesRecursive());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getName()
	 */
	@Override
	public String getName() {
		return name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getPath()
	 */
	@Override
	public Path getPath() {
		return Paths.get(path);
	}

	/**
	 * Return a set of all subdirectories that are directly under this one
	 * 
	 * @return a set of all subdirectories that are directly under this one
	 */
	public Set<DirectoryModel> getSubdirectories() {
		Set<DirectoryModel> result = new TreeSet<DirectoryModel>();
		result.addAll(subdirectories);
		return result;
	}

	/**
	 * Return the subdirectory with the same name as the given reference that is
	 * directly under this directory
	 * 
	 * @param reference
	 *            the subdirectory's reference
	 * @return the subdirectory with the same name as the given reference that is
	 *         directly under this directory
	 */
	public DirectoryModel getSubDirectory(String reference) {
		for (DirectoryModel subDir : getSubdirectories()) {
			if (subDir.getName().equals(reference)) {
				return subDir;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#moveToDir(model.DirectoryModel)
	 */
	@Override
	public void moveToDir(DirectoryModel dir) throws IOException {
		LOGGER.log(Level.INFO, "Move dir {0} to {1}", new Object[] { this.getName(), dir.getName() });
		if (contains(dir))
			throw new IllegalArgumentException(dir.toString());
		this.parent = dir;
		this.path = dir.getPath().resolve(name).toString();
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#moveToDir(java.nio.file.Path)
	 */
	@Override
	public void moveToDir(Path dir) throws IOException {
		LOGGER.log(Level.INFO, "Move dir {0} to {1}", new Object[] { this.getName(), dir.getFileName() });
		if (contains(dir))
			throw new IllegalArgumentException(dir.toString());
		this.parent = ModelManager.instance.getDir(dir);
		this.path = dir.resolve(name).toString();
		update();
	}

	/**
	 * Remove the given directoryModel from this one
	 * 
	 * @param directoryModel
	 *            the given directoryModel
	 */
	protected void remove(DirectoryModel directoryModel) {
		LOGGER.log(Level.FINE, "Remove subdir {0} from {1}", new Object[] { directoryModel.getName(), this.getName() });
		this.subdirectories.remove(directoryModel);
		if (this.parent != null) {
			this.parent.add(this);
		}
		history.log();
	}

	/**
	 * Remove the given imageModel from this one
	 * 
	 * @param imageModel
	 *            the given ImageModel
	 */
	protected void remove(ImageModel imageModel) {
		LOGGER.log(Level.FINE, "Remove image {0} from {1}", new Object[] { imageModel.getName(), this.getName() });
		this.images.remove(imageModel);
		if (this.parent != null) {
			this.parent.add(this);
		}
		history.log();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#rename(java.lang.String)
	 */
	@Override
	public void rename(String name) throws IOException {
		LOGGER.log(Level.FINE, "Rename dir {0} from {1}", new Object[] { this.getName(), name });
		if (!Model.isLegalFilename(name))
			throw new IllegalArgumentException(name);
		this.name = name;
		this.path = getDirectoryPath().resolve(name).toString();
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#revertTo(model.SnapShot)
	 */
	@Override
	public void revertTo(DirectorySnapShot snap) throws IOException {
		// TODO: fix
		LOGGER.log(Level.INFO, "Revert dir {0}", this.getName());
		if (snap.getSubject() != this)
			throw new IllegalArgumentException();

		for (ImageModel img : getImages()) {
			img.revertTo(img.getHistory().getSnapBefore(snap.getDate()));
		}
		for (DirectoryModel dir : getSubdirectories()) {
			dir.revertTo(dir.getHistory().getSnapBefore(snap.getDate()));
		}
		toState(snap);
		update();

	}

	/**
	 * Sets the parent of this directory if it is null
	 * 
	 * @param parent
	 *            what to set the parent of this directory to
	 */
	protected void setParent(DirectoryModel parent) {
		LOGGER.log(Level.FINE, "Set parent of dir {0} to {1}", new Object[] { this.getName(), parent.getName() });
		if (this.parent == null && parent != null && parent.getPath().endsWith(getPath())) {
			this.parent = parent;
			history.log();
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Moves this directory to a previous state but does not update the filesystem
	 * 
	 * @param snap
	 *            the previous state to revert to
	 */
	private void toState(DirectorySnapShot snap) {
		this.parent = snap.getParent();
		this.name = snap.getName();
		this.path = snap.getPath().toString();
		this.images = snap.getImages();
		this.subdirectories = snap.getSubdirectories();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getPath().toString();
	}

	/**
	 * Updates the directory associated with this DirectoryModel to match the state
	 * of this DirectoryModel
	 * 
	 * @throws IOException
	 *             if there was an error performing the change in the filesystem.
	 *             Note that this DirectoryModel is automatically rolled back to
	 *             it's state before the move
	 */
	private void update() throws IOException {
		LOGGER.log(Level.FINE, "Update {0}", this.getName());
		try {
			ModelManager.instance.getAccesor().move(history.getLast().getPath(), getPath());
		} catch (IOException e) {
			toState(history.getLast());
			throw e;
		}
		DirectoryModel lastDir = history.getLast().getParent();
		if (lastDir != parent) {
			if (lastDir != null) {
				lastDir.remove(this);
			}
			if (parent != null) {
				parent.add(this);
			}
		}
		history.log();
	}

}
