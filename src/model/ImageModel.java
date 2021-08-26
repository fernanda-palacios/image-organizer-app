package model;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 * Represents an Image in the filesystem
 *
 */
public class ImageModel implements Model<ImageModel, ImageSnapShot> {

	private static final long serialVersionUID = 3832645612902527342L;
	private static final Pattern extPattern = Pattern.compile("(.*)(\\..*)");
	private static final Pattern namePattern = Pattern.compile("([^\\s\\-@][^\\-@]*)(?: -(?: @[^@\\-\\s]+)+)?");
	private static final Pattern tagPattern = Pattern.compile("@([^@\\-\\s]+)");
	private static final Logger LOGGER = Logger.getLogger(ImageModel.class.getName());

	static {
		LOGGER.setLevel(Level.OFF);
	}

	/**
	 * Gets all the tags encoded in the given string
	 * 
	 * @param taggedName
	 *            the given string
	 * @return all the tags encoded in the given string
	 */
	public static LinkedList<String> getTags(String taggedName) {
		Matcher m = tagPattern.matcher(taggedName);
		LinkedList<String> tags = new LinkedList<>();
		while (m.find()) {
			tags.add(m.group(1));
		}
		return tags;
	}

	/**
	 * Return the un-tagged name encoded in the given string
	 * 
	 * @param taggedName
	 *            the given string
	 * @return the un-tagged name encoded in the given string
	 */
	public static String getUntaggedName(String taggedName) {
		Matcher m = namePattern.matcher(taggedName);
		if (!Model.isLegalFilename(taggedName) || !m.matches())
			throw new IllegalArgumentException(taggedName);
		return m.group(1);
	}

	private String untaggedName;
	private DirectoryModel dir;
	private String extension;
	private TreeSet<String> tags;
	private History<ImageModel, ImageSnapShot> history;

	/**
	 * Creates a new ImageModel to represent the given path
	 * 
	 * @param path
	 *            the given path
	 * @throws IOException
	 *             on problems interacting with the given path
	 */
	protected ImageModel(Path path, DirectoryModel parent) throws IOException {
		LOGGER.log(Level.FINE, "Create object at {0}", path.getFileName());
		if (!ModelManager.instance.getAccesor().exists(path)) {
			throw new NoSuchFileException(path.toString());
		}

		if (parent == null) {
			throw new NullPointerException();
		}

		path = ModelManager.instance.getAccesor().toRealPath(path);

		String fullName = path.getFileName().toString();

		if (!isImage(path)) {
			throw new NotImageException(path.toString());
		}

		Matcher matcher = extPattern.matcher(fullName);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(fullName);
		}

		String taggedName = matcher.group(1);
		extension = matcher.group(2);

		untaggedName = getUntaggedName(taggedName);
		tags = new TreeSet<>();
		tags.addAll(getTags(taggedName));
		dir = parent;

		ModelManager.instance.getAccesor().move(path, getPath());

		history = new History<>(this, new ImageSnapShot.ImageSnapShotFactory());
		dir.add(this);
	}

	/**
	 * Add the given tag to this image
	 * 
	 * @param tag
	 *            the given tag
	 * @throws IOException
	 *             on problems updating the associated file
	 */
	public void addTag(String tag) throws IOException {
		LOGGER.log(Level.INFO, "Add tag {0} to {1}", new Object[] { tag, this });
		if (tag == null || tag.matches(".*[\\\\/:\\*\\?\"<>|\\-@].*"))
			throw new IllegalArgumentException(tag);

		tags.add(tag);
		updateFile();

	}

	public final static boolean isImage(String name) {
		String guessedType = URLConnection.guessContentTypeFromName(name);
		return guessedType != null && guessedType.startsWith("image");
	}

	public final static boolean isImage(Path path) {
		return isImage(path.getFileName().toString());
	}

	/**
	 * Delete the given tag from this image
	 * 
	 * @param tag
	 *            the given tag
	 * @throws IOException
	 *             on problems updating the associated file
	 */
	public void deleteTag(String tag) throws IOException {
		LOGGER.log(Level.INFO, "Removed tag {0} from {1}", new Object[] { tag, this });
		tags.remove(tag);
		updateFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof ImageModel) {
			return this.getPath().equals(((ImageModel) other).getPath());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getDirectory()
	 */
	@Override
	public DirectoryModel getDirectory() {
		return dir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getDirectoryPath()
	 */
	@Override
	public Path getDirectoryPath() {
		return dir.getPath();
	}

	/**
	 * Return the extension for this image
	 * 
	 * @return the extension for this image
	 */
	public String getExtension() {
		return extension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getHistory()
	 */
	@Override
	public History<ImageModel, ImageSnapShot>.HistoryViewer getHistory() {
		return history.getViewer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getName()
	 */
	@Override
	public String getName() {
		String taggedName = untaggedName;
		if (tags.size() > 0) {
			taggedName += " -";
		}
		for (String tag : tags) {
			taggedName += " @" + tag;
		}
		return taggedName;
	}

	/**
	 * Return the tags of this image NOTE: the returned ObservableList is NOT in
	 * sync with this image
	 * 
	 * @return the tags of this image
	 */
	public ObservableList<String> getObservableTags() {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(tags);
		return FXCollections.observableList(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#getPath()
	 */
	@Override
	public Path getPath() {
		Path name = Paths.get(getName() + getExtension());
		Path dirPath = getDirectoryPath();
		return (dirPath == null) ? name : dirPath.resolve(name);
	}

	/**
	 * Return this image's tags
	 * 
	 * @return this image's tags
	 */
	@SuppressWarnings("unchecked")
	public TreeSet<String> getTags() {
		// Remember to use a defensive copy!
		return (TreeSet<String>) tags.clone();
	}

	/**
	 * Return this image's unttaged name
	 * 
	 * @return this image's untagged name
	 */
	public String getUntaggedName() {
		return untaggedName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#moveToDir(model.DirectoryModel)
	 */
	@Override
	public void moveToDir(DirectoryModel dir) throws IOException {
		LOGGER.log(Level.INFO, "Move {0} to directory {1}", new Object[] { this, dir });
		if (dir == null)
			throw new IllegalArgumentException();
		this.dir = dir;
		updateFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#moveToDir(java.nio.file.Path)
	 */
	@Override
	public void moveToDir(Path dir) throws IOException {
		DirectoryModel target = ModelManager.instance.getDir(dir);
		moveToDir(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#rename(java.lang.String)
	 */
	@Override
	public void rename(String taggedName) throws IOException {
		if (taggedName == null)
			throw new IllegalArgumentException(taggedName);

		LOGGER.log(Level.INFO, "Renamed {0} to {1}", new Object[] { this, taggedName });
		untaggedName = getUntaggedName(taggedName);
		tags.clear();
		tags.addAll(getTags(taggedName));
		updateFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Model#revertTo(model.SnapShot)
	 */
	@Override
	public void revertTo(ImageSnapShot snap) throws IOException {
		LOGGER.log(Level.INFO, "Reverted {0}", new Object[] { this });

		if (snap == null || snap.getSubject() != this) {
			throw new IllegalArgumentException();
		}
		toState(snap);
		updateFile();
	}

	/**
	 * Set the unTaggedName for this image to the given string
	 * 
	 * @param untaggedName
	 *            the given string
	 * @throws IOException
	 *             on errors updating the associated file
	 */
	public void setUntaggedName(String untaggedName) throws IOException {
		if (untaggedName == null || untaggedName.matches(".*[\\\\/:\\*\\?\"<>|\\-].*"))
			throw new IllegalArgumentException(untaggedName);
		this.untaggedName = untaggedName;
		updateFile();

	}

	/**
	 * Return a javaFXImage with the contents of this image
	 * 
	 * @return a javaFXImage with the contents of this image
	 * @throws IOException
	 *             on errors reading the contents of this image
	 */
	public Image toJavaFXImage() throws IOException {
		return new Image(ModelManager.instance.getAccesor().newInputStream(getPath()));
	}

	/**
	 * Moves this ImageModel to a previous state
	 * 
	 * @param snap
	 *            a previous state
	 */
	private void toState(ImageSnapShot snap) {
		this.dir = snap.getDir();
		untaggedName = snap.getUntaggedName();
		tags.clear();
		LOGGER.log(Level.FINE, "{0} jumped to some state", this);
		tags.addAll(snap.getTags());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDirectory().getName() + "/" + getName() + getExtension();
	}

	/**
	 * Updates the file associated with this ImageModel.
	 * 
	 * @throws IOException
	 *             on any issues updating the file. Note that if an exception is
	 *             thrown, the ImageModel reverts to it's state before executing an
	 *             update
	 */
	private void updateFile() throws IOException {
		LOGGER.log(Level.FINE, "Move {0} to {1}", new Object[] { history.getLast().getPath(), getPath() });
		try {
			ModelManager.instance.getAccesor().move(history.getLast().getPath(), getPath());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			toState(history.getLast());
			throw e;
		}
		DirectoryModel lastDir = history.getLast().getDir();
		if (!lastDir.equals(dir)) {
			lastDir.remove(this);
			dir.add(this);
		}
		history.log();
	}
}
