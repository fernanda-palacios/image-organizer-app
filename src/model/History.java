package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

/**
 * Stores the history of a Model object
 *
 * @param <T>
 *            the type of Model object
 * @param <S>
 *            the type of snapshots used to document the model object's history
 */
public class History<T extends Model<T, S>, S extends SnapShot<T, S>> implements Serializable {

	/**
	 * A read-only object representing this history object
	 *
	 */
	public class HistoryViewer implements Iterable<S>, Serializable {

		private static final long serialVersionUID = 2033150035561186499L;

		/**
		 * Private!
		 * 
		 */
		private HistoryViewer() {

		}

		/**
		 * Return the number of snapshots taken in this history
		 * 
		 * @return the number of snapshots taken in this history
		 */
		public int getCount() {
			return count;
		}

		/**
		 * Gets a SnapShot of a given rank. The rank of a SnapShot is the number of
		 * SnapShots between it and the latest SnapShot, not including it or the latest
		 * SnapShot.
		 * 
		 * @param rank
		 *            the rank of the target SnapShot
		 * @return a SnapShot with the given rank
		 */
		public S getSnap(int rank) {
			// TODO: Implement
			int curRank = 0;
			for (S snap : lastSnap) {
				if (curRank == rank) {
					return snap;
				}
				curRank++;
			}
			return null;
		}

		/**
		 * Return the first snap that does not come after the given date
		 * 
		 * @param date
		 *            the given date
		 * @return the first snap that does not come after the given date
		 */
		public S getSnapBefore(Date date) {
			for (S snap : lastSnap) {
				if (!snap.getDate().after(date))
					return snap;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<S> iterator() {
			return lastSnap.iterator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String result = lastSnap.toString();
			for (S snap : lastSnap.getLastSnap()) {
				result = snap + System.lineSeparator() + result;
			}
			return result;
		}
	}

	private static final long serialVersionUID = -7587766823643659310L;

	private S lastSnap;
	private HistoryViewer viewer;
	private T subject;
	private Factory<T, S> snapFactory;
	private int count;

	/**
	 * Create a new History for the given subject, using the given factory to take
	 * the snapshots
	 * 
	 * @param subject
	 *            the given subject
	 * @param snapFactory
	 *            the given factory
	 */
	public History(T subject, Factory<T, S> snapFactory) {
		lastSnap = null;
		viewer = new HistoryViewer();
		this.subject = subject;
		this.snapFactory = snapFactory;
		lastSnap = snapFactory.makeSnap(subject, null);
		count = 1;
	}

	/**
	 * Return the last snapshot taken in this history
	 * 
	 * @return the last snapshot taken in this history
	 */
	public S getLast() {
		return lastSnap;
	}

	/**
	 * Return the HistoryViewer for this History
	 * 
	 * @return the HistoryViewer for this History
	 */
	public HistoryViewer getViewer() {
		return viewer;
	}

	/**
	 * Take a snapshot of this history's subject and save it
	 * 
	 */
	public void log() {
		S snap = snapFactory.makeSnap(subject, getLast());
		if (snap.getDate().before(lastSnap.getDate())) {
			throw new ChangeHistoryException();
		} else {
			lastSnap = snap;
			count++;
		}
	}

}
