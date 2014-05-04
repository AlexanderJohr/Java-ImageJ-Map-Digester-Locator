package craj.mapDigesterLocator;

/**
 * This class represents an observable object. It can be subclassed to represent
 * an object that the application wants to have observed.
 * <p>
 * An observable object can have one or more observers. An observer may be any
 * object that implements interface <tt>GenericObserver</tt> with the same
 * <tt>ArgumentType</tt>. After an application calling the
 * <code>Observable</code>'s <code>notifyObservers</code> method causes all of
 * its observers to be notified of the change by a call to their
 * <code>update</code> method.
 * <p>
 * The order in which notifications will be delivered is unspecified. The
 * default implementation provided in the Observable class will notify Observers
 * in the order in which they registered interest, but subclasses may change
 * this order, use no guaranteed order, deliver notifications on separate
 * threads, or may guarantee that their subclass follows this order, as they
 * choose.
 * <p>
 * When an observable object is newly created, its set of observers is empty.
 * Two observers are considered the same if and only if the <tt>equals</tt>
 * method returns true for them.
 * 
 * @author Alexander Johr u26865 m18927
 */
public interface GenericObserver<ArgumentType> {

	/**
	 * This method is called whenever the <tt>Observable</tt> object calls his
	 * <code>notifyObservers</code> method to have all the object's observers
	 * notified.
	 * 
	 * @param oberservable
	 *            the observable object.
	 * @param argument
	 *            an argument passed to the <code>notifyObservers</code> method.
	 */
	void update(GenericObservable<ArgumentType> oberservable,
			ArgumentType argument);
}
