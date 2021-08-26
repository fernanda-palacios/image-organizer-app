package model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * A model of a location in the filesystem
 *
 * @param <T>
 *            the type of this model
 * @param <S>
 *            the type of snapshots associated with this model
 */
public interface Model<T extends Model<T, S>, S extends SnapShot<T, S>> extends Comparable<T>, Serializable {

	public static boolean isLegalFilename(String str) {
		return str.matches("[a-zA-Z0-9](?:[a-zA-Z]|[0-9]|[!#%&\\(\\);=\\[\\]^_+{}~,\\\\.@\\- ])*");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public default int compareTo(T other) {
		return this.getPath().compareTo(other.getPath());
	}

	/**
	 * Return the directory this location is contained in
	 * 
	 * @return the directory this location is contained in
	 */
	public DirectoryModel getDirectory();

	/**
	 * Return the path to the directory this location is contained in
	 * 
	 * @return the path to the directory this location is contained in
	 */
	public Path getDirectoryPath();

	/**
	 * Return the history for this model
	 * 
	 * @return the history for this model
	 */
	public History<T, S>.HistoryViewer getHistory();

	/**
	 * Return the name of this model
	 * 
	 * @return the name of this model
	 */
	public String getName();

	/**
	 * Return the path to this model
	 * 
	 * @return the path to this model
	 */
	public Path getPath();

	/**
	 * Move this model to a given directory
	 * 
	 * @param dir
	 *            the model of the given directory
	 * @throws IOException
	 *             on any problems moving the directory
	 */
	public void moveToDir(DirectoryModel dir) throws IOException;

	/**
	 * Move this model to a given directory
	 * 
	 * @param path
	 *            the path to the given directory
	 * @throws IOException
	 *             on any problems moving the directory
	 */
	public void moveToDir(Path path) throws IOException;

	/**
	 * Rename this model to the given name
	 * 
	 * @param name
	 *            the given name
	 * @throws IOException
	 *             on any problems updating the associated file
	 */
	public void rename(String name) throws IOException;

	/**
	 * Revert this model to the given previous state
	 * 
	 * @param snap
	 *            the given previous state
	 * @throws IOException
	 *             on any problems updating the file
	 */
	public void revertTo(S snap) throws IOException;

}