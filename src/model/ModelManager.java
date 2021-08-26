package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A singleton class used to retrieve and add model objects
 *
 */
public class ModelManager {

	public static final ModelManager instance = new ModelManager();
	public static final String saveLocation = ".ser/save.ser";

	// List of Images Objects
	private TreeSet<DirectoryModel> roots;
	private Accesor accesor;
	private TreeSet<String> tags;

	/**
	 * Creates a ModelManager
	 * 
	 */
	private ModelManager() {
		roots = new TreeSet<>();
		tags = new TreeSet<>();
	}

	public void saveToDisk() throws IOException {
		Path loc = Paths.get(saveLocation);

		if (loc.getParent() != null && !getAccesor().exists(loc.getParent())) {
			getAccesor().createDirectory(loc.getParent());
		}

		OutputStream fout = getAccesor().newOutputStream(loc);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(instance.roots);
		oos.writeObject(instance.tags);
		oos.close();

	}

	public void loadFromDisk() throws ClassNotFoundException, IOException {
		Path loc = Paths.get(saveLocation);
		loadFromLocation(loc);
	}

	@SuppressWarnings("unchecked")
	public void loadFromLocation(Path location) throws IOException, ClassNotFoundException {
		InputStream streamIn = getAccesor().newInputStream(location);
		ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
		roots = (TreeSet<DirectoryModel>) objectinputstream.readObject();
		tags = (TreeSet<String>) objectinputstream.readObject();
		objectinputstream.close();

	}

	/**
	 * Adds the given directory to this ModelManager
	 * 
	 * @param dir
	 *            the given directory
	 * @throws IOException
	 *             on an error accessing the given directory
	 */
	protected void addDir(DirectoryModel dir) throws IOException {
		boolean add = true;
		for (Iterator<DirectoryModel> i = roots.iterator(); i.hasNext();) {
			DirectoryModel root = i.next();
			if (dir.contains(root) && !dir.equals(root)) {
				i.remove();
				root.setParent(getDir(root.getPath().getParent()));
			}
			if (root.contains(dir)) {
				add = false;
			}
		}
		if (add) {
			roots.add(dir);
		}

	}

	/**
	 * Adds the directory at the given path to this manager and any subdirectories
	 * and images contained in it
	 * 
	 * @param dir
	 *            the given path
	 * @return the created DirectoryModel object
	 * @throws IOException
	 *             on any problems accessing the given directory
	 */
	public DirectoryModel addDir(Path dir) throws IOException {
		if (!getAccesor().isDirectory(dir))
			throw new NotDirectoryException(dir.toString());

		if (getDir(dir) != null)
			return null;

		DirectoryModel dirModel = new DirectoryModel(dir);
		addDir(dirModel);

		List<Path> paths = getAccesor().getChildPaths(dir);
		for (Path path : paths) {
			try {
				if (getAccesor().isDirectory(path)) {
					addDir(path);
				} else if (ImageModel.isImage(path)) {
					new ImageModel(path, dirModel);
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}

		return dirModel;

	}

	/**
	 * Gets the directory at the given path
	 * 
	 * @param dir
	 *            the given path
	 * @return the directory at the given path
	 * @throws IOException
	 *             on any problems accessing the given path
	 */
	public DirectoryModel getDir(Path dir) throws IOException {
		if (!getAccesor().isDirectory(dir))
			throw new NotDirectoryException(dir.toString());
		return (DirectoryModel) getModel(dir);
	}

	/**
	 * Return the model object representing the given path
	 * 
	 * @param path
	 *            the given path
	 * @return the model object
	 * @throws IOException
	 *             on any problems accessing the given path
	 */
	public Model<?, ?> getModel(Path path) throws IOException {
		path = getAccesor().toRealPath(path);
		for (DirectoryModel root : roots) {
			if (root.contains(path)) {
				Path rootPath = root.getPath();
				if (rootPath.equals(path)) {
					return root;
				}
				return root.getDescendant(path);
			}
		}

		return null;
	}

	/** Get the accessor for this modelmanger
	 * @return the accessor for this modelmanager
	 */
	public Accesor getAccesor() {
		return accesor;
	}

	/** Set the accessor for this ModelManager
	 * @param accesor the accessor
	 */
	public void setAccesor(Accesor accesor) {
		this.accesor = accesor;
	}

	/** Clear this ModelManager
	 * 
	 */
	public void clear() {
		roots.clear();
	}
	
	
	/** Add a tag to all the added tags
	 * @param tag the tag to add
	 */
	public void addTag(String tag) {
		tags.add(tag);
	}
	
	/** Return all the tags ever added
	 * @return all the tags ever added
	 */
	public Set<String> getTags() {
		return (TreeSet<String>)tags.clone();
	}
}
