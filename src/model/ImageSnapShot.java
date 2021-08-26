package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of this Image at a particular instance in time
 *
 */
public class ImageSnapShot extends SnapShot<ImageModel, ImageSnapShot> {

	/**
	 * Make ImageSnapShots
	 *
	 */
	static class ImageSnapShotFactory implements Factory<ImageModel, ImageSnapShot> {
		private static final long serialVersionUID = -3832624070441648265L;

		@Override
		public ImageSnapShot makeSnap(ImageModel subject, ImageSnapShot lastSnap) {
			return new ImageSnapShot(subject, lastSnap);
		}

	}

	private static final long serialVersionUID = 283169502800702477L;

	private static LinkedList<ImageSnapShot> allHistory = new LinkedList<ImageSnapShot>();

	/**
	 * Return a list containing every ImageSnapShot ever taken in chrnological order
	 * 
	 * @return a list containing every ImageSnapShot ever taken in chrnological
	 *         order
	 */
	@SuppressWarnings("unchecked")
	public static List<ImageSnapShot> getAllHistory() {
		return (List<ImageSnapShot>) (allHistory.clone());
	}

	private final String untaggedName, taggedName;
	private final DirectoryModel dir;

	private final String[] tags;

	/**
	 * Create a new snapshot of the given image
	 * 
	 * @param subject
	 *            the given image
	 */
	private ImageSnapShot(ImageModel subject) {
		this(subject, null);
	}

	/**
	 * Create a new snapshot of the given image
	 * 
	 * @param subject
	 *            the given image
	 * @param lastSnap
	 *            the last snapshot taken of the given image
	 */
	private ImageSnapShot(ImageModel subject, ImageSnapShot lastSnap) {
		super(subject, lastSnap);
		this.dir = subject.getDirectory();
		this.untaggedName = subject.getUntaggedName();
		this.taggedName = subject.getName();
		this.tags = subject.getTags().toArray(new String[0]);
		allHistory.add(this);
	}

	/**
	 * Return the DirectoryModel the subject of this snapshot was in when it was
	 * taken
	 * 
	 * @return the DirectoryModel the subject of this snapshot was in when it was
	 *         taken
	 */
	public DirectoryModel getDir() {
		return dir;
	}

	/**
	 * Return the path of the subject of this snapshot when it was taken
	 * 
	 * @return the path of the subject of this snapshot when it was taken
	 */
	public Path getPath() {
		return dir.getPath().resolve(taggedName + getSubject().getExtension());
	}

	/**
	 * Return the name of the subject of this snapshot when it was taken
	 * 
	 * @return the name of the subject of this snapshot when it was taken
	 */
	public String getTaggedName() {
		return taggedName;
	}

	/**
	 * Return the tags of the subject of this snapshot when it was taken
	 * 
	 * @return the tags of the subject of this snapshot when it was taken
	 */
	public LinkedList<String> getTags() {
		return new LinkedList<String>(Arrays.asList(tags.clone()));
	}

	/**
	 * Return the untagged name of the subject of this snapshot when it was taken
	 * 
	 * @return the untagged name of the subject of this snapshot when it was taken
	 */
	public String getUntaggedName() {
		return untaggedName;
	}

	/*
	 * For serialization
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		allHistory = (LinkedList<ImageSnapShot>) ois.readObject(); // Read the global history
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ImageSnapShot lastSnap = getLastSnap();
		if (lastSnap == null) {
			return String.format("%2$tc create \"%1$s\"", this.untaggedName, getDate());
		}

		String result = String.format("%2$tc edit \"%1$s\"", lastSnap.untaggedName, getDate());
		boolean change = false;

		if (!lastSnap.dir.equals(this.dir)) {
			result += String.format("%n\tMove from:\t%1$s %n\tto:\t\t%2$s", lastSnap.dir, this.dir);
			change = true;
		}

		if (!lastSnap.untaggedName.equals(this.untaggedName)) {
			result += String.format("%n\tRe-name from \"%1$s\" to \"%2$s\"", lastSnap.untaggedName, this.untaggedName);
			change = true;
		}
		
		if (!Arrays.equals(lastSnap.tags, this.tags)) {
			change = true;
			LinkedList<String> addedTags = new LinkedList<>();
			LinkedList<String> removedTags = new LinkedList<>();
			
			for(int i = 0; i < lastSnap.tags.length; i++) {
				String tag = lastSnap.tags[i];
				if(!Arrays.asList(this.tags).contains(tag)) {
					removedTags.add(tag);
				}
			}
			
			for(int i = 0; i < this.tags.length; i++) {
				String tag = this.tags[i];
				if(!Arrays.asList(lastSnap.tags).contains(tag)) {
					addedTags.add(tag);
				}
			}
			
			if (addedTags.size() == 1) {
				result += String.format("%n\tAdd tag \"%1$s\"", addedTags.getFirst());
			} else if (addedTags.size() > 1) {
				result += String.format("%n\tAdd tags %1$s", addedTags);
			}
			
			if (removedTags.size() == 1) {
				result += String.format("%n\tRemove tag \"%1$s\"", removedTags.getFirst());
			} else if (removedTags.size() > 1) {
				result += String.format("%n\tRemove tags %1$s", removedTags);
			}
		}

		if (!lastSnap.taggedName.equals(this.taggedName)) {
			result += String.format("%n\t\tFinal tagged name \"%1$s\"", this.taggedName);
			change = true;
		}
		
		if(change) {
			return result;
		}
		return String.format("%2$tc no change to \"%1$s\"", lastSnap.untaggedName, getDate());
	}

	/*
	 * For serialization
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeObject(allHistory); // write the global history down
	}

}