package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

/**
 * A snapshot of a subject at an instant
 *
 * @param <T>
 *            the type of the subject
 * @param <S>
 *            the type of this snapshot
 */
public abstract class SnapShot<T, S extends SnapShot<T, S>> implements Iterable<S>, Serializable {

	/**
	 * An iterator over this and previous snapshots
	 *
	 * @param <T>
	 *            the type of this snapshot's subject
	 * @param <S>
	 *            the type of this snapshot
	 */
	private static class SnapIterator<T, S extends SnapShot<T, S>> implements Iterator<S>, Serializable {

		private static final long serialVersionUID = -1020361847171498742L;
		private SnapShot<T, S> node;

		private SnapIterator(SnapShot<T, S> node) {
			this.node = node;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return node != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public S next() {
			SnapShot<T, S> toReturn = node;
			node = node.getLastSnap();
			return (S) toReturn;
		}

	}

	private static final long serialVersionUID = -5175946745479413025L;

	private final T subject;
	private final SnapShot<T, S> lastSnap;
	private SnapShot<T, S> nextSnap;

	private final Date date;

	/**
	 * Create a snapshot of the given subject
	 * 
	 * @param subject
	 *            the given subject
	 */
	protected SnapShot(T subject) {
		this(subject, null);
	}

	/**
	 * Create a snapshot of the given subject
	 * 
	 * @param subject
	 *            the given subject
	 * @param lastSnap
	 *            the last snapshot taken of the given subject
	 */
	protected SnapShot(T subject, S lastSnap) {
		this.subject = subject;
		this.lastSnap = lastSnap;
		date = new Date();

		if (lastSnap != null) {
			lastSnap.setNextSnap(this);
		}
	}

	/**
	 * Return when this snapshot was taken
	 * 
	 * @return when this snapshot was taken
	 */
	public final Date getDate() {
		return (Date) date.clone();
	}

	/**
	 * Return the snapshot taken before this one. If there is none, null
	 * 
	 * @return the snapshot taken before this one. If there is none, null
	 */
	@SuppressWarnings("unchecked")
	public final S getLastSnap() {
		return (S) lastSnap;
	}

	/**
	 * Return the snapshot taken after this one. If there is none, null
	 * 
	 * @return the snapshot taken after this one. If there is none, null
	 */
	@SuppressWarnings("unchecked")
	public final S getNextSnap() {
		return (S) lastSnap;

	}

	/**
	 * Return the subject of this snapshot
	 * 
	 * @return the subject of this snapshot
	 */
	public final T getSubject() {
		return subject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<S> iterator() {
		return new SnapIterator<T, S>(this);
	}

	/**
	 * Set the snapshot the comes after this one. Can only be done once
	 * 
	 * @param snap
	 *            the snapshot that comes after this one
	 */
	protected void setNextSnap(SnapShot<T, S> snap) {
		if (nextSnap != null) {
			throw new ChangeHistoryException();
		}
		this.nextSnap = snap;
	}

}
