package controller;

/**
 * Listens for a change in an Observable of a given type
 *
 * @param <T>
 *            the type of observers this can listen to
 */
public interface Listener<T> {

	/**
	 * Notify this listener of a change in the an Observable
	 * 
	 * @param changed
	 *            the Observable that changed
	 */
	public void notify(Observable<T> changed);
}
