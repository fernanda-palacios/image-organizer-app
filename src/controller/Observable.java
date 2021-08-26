package controller;

/**
 * A of the given type that can be observed
 *
 * @param <T>
 *            The type of the observable object
 */
public interface Observable<T> {

	/**
	 * Adds a listener to this observer
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addListener(Listener<T> listener);

	/**
	 * Return the value of this Observable
	 * 
	 * @return the value of this Observable
	 */
	public T getValue();

	/**
	 * Removes a listener to this observer
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(Listener<T> listener);

}
