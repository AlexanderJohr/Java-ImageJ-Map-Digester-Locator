package craj.mapDigesterLocator;

import java.util.ArrayList;

public class GenericObservable<ArgumentType> {
	private final ArrayList<GenericObserver<ArgumentType>> observerList;

	/** Construct an Observable with zero Observers. */
	public GenericObservable() {
		observerList = new ArrayList<>();
	}

	/**
	 * Adds an observer to the list of observers for this object, provided that
	 * it is not already in the list. The order in which notifications will be
	 * delivered to multiple observers is not specified.
	 * 
	 * @param observer
	 *            an observer to be added.
	 * @throws NullPointerException
	 *             if the parameter <CODE>observer</CODE> is null.
	 */
	public synchronized void addObserver(
			final GenericObserver<ArgumentType> observer) {
		if (observer == null) {
			throw new NullPointerException();
		} else {
			final boolean observerIsNotInList = !observerList
					.contains(observer);
			if (observerIsNotInList) {
				observerList.add(observer);
			}
		}
	}

	/**
	 * Returns the number of observers of this <tt>GenericObservable</tt>
	 * object.
	 * 
	 * @return the number of observers of this object.
	 */
	public synchronized int countObservers() {
		return observerList.size();
	}

	/**
	 * Deletes an observer from the list of observers of this object. Passing
	 * <CODE>null</CODE> to this method will have no effect.
	 * 
	 * @param observer
	 *            the observer to be deleted.
	 */
	public synchronized void deleteObserver(
			final GenericObserver<ArgumentType> observer) {
		observerList.remove(observer);
	}

	/**
	 * Clears the observer list so that this object no longer has any observers.
	 */
	public synchronized void deleteObservers() {
		observerList.clear();
	}

	/**
	 * Notify to this observable all added observers of its observers.
	 * <p>
	 * Each observer has its <code>update</code> method called with two
	 * arguments: this observable object and <code>null</code>. In other words,
	 * this method is equivalent to: <blockquote><tt>
	 * notifyObservers(null)</tt></blockquote>
	 * 
	 * @see craj.mapDigesterLocator.GenericObserver#update(ObservableType,
	 *      ArgumentType)
	 */
	public void notifyObservers() {
		notifyObservers(null);
	}

	/**
	 * Notify all to this observable added observers.
	 * <p>
	 * Each observer has its <code>update</code> method called with two
	 * arguments: this observable object and the <code>arg</code> argument.
	 * 
	 * @param argument
	 *            any object.
	 * @see craj.mapDigesterLocator.GenericObserver#update(ObservableType,
	 *      ArgumentType)
	 */
	public synchronized void notifyObservers(final ArgumentType argument) {
		final int oberserverCount = observerList.size();
		for (int i = oberserverCount - 1; i >= 0; i--) {

			final GenericObserver<ArgumentType> currentObserver = observerList
					.get(i);
			currentObserver.update(this, argument);
		}
	}
}
