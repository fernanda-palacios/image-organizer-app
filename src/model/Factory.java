package model;

import java.io.Serializable;

/**
 * Creates SnapShots of the given subject
 * 
 * @param <T>
 *            the type of the subject
 * @param <S>
 *            the type of the snapshot
 */
interface Factory<T, S extends SnapShot<T, S>> extends Serializable {

	/**
	 * Return a SnapShot of the given subject
	 * 
	 * @param subject
	 *            the given subject
	 * @param lastSnap
	 *            the last snapshot taken of the subject
	 * @return a SnapShot of the given subject
	 */
	S makeSnap(T subject, S lastSnap);
}
