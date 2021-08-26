package controller;

import java.util.HashSet;
import java.util.Set;

/**
 * A Observable Reference
 *
 */
public class ObservableReference implements Observable<Reference> {
	private Reference reference;
	private Set<Listener<Reference>> listeners;

	/**
	 * Create a null ObservableReference
	 * 
	 */
	public ObservableReference() {
		reference = new Reference();
		listeners = new HashSet<Listener<Reference>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.Observable#addListener(controller.Listener)
	 */
	@Override
	public void addListener(Listener<Reference> listener) {
		listeners.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.Observable#getValue()
	 */
	@Override
	public Reference getValue() {
		return this.reference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.Observable#removeListener(controller.Listener)
	 */
	@Override
	public void removeListener(Listener<Reference> listener) {
		listeners.remove(listener);

	}

	/**
	 * Notifies all listeners of a change
	 * 
	 */
	public void update() {
		for (Listener<Reference> listener : listeners) {
			listener.notify(this);
		}
	}

	/**
	 * Update this reference to a given reference and notifies all listeners
	 * 
	 * @param reference
	 *            the given reference
	 */
	public void update(Reference reference) {
		this.reference = reference;
		update();
	}

}
