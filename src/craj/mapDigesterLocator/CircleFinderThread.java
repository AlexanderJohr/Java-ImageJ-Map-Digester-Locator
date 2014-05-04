package craj.mapDigesterLocator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import craj.CircleFinding;

/**
 * Searches in a given image-file for circles. This abstract class implements no
 * search algorithm but defines the interface for the calculation-method
 * <tt>findCircleCoordinates</tt> and the functionality to notify all observers
 * observing this object with the found circle-data. After an implementation of
 * the <tt>findCircleCoordinates</tt> completed, the returning circle findings
 * are available as an argument in the observers <tt>update</tt>-method.
 * 
 * @author Alexander Johr u26865 m18927
 * 
 */
public abstract class CircleFinderThread extends
		GenericObservable<List<CircleFinding>> implements Runnable {

	protected List<CircleFinding> circleCoordinates = new ArrayList<>();
	protected final File imageFile;

	public CircleFinderThread(final File imageFile) {
		super();
		this.imageFile = imageFile;
	}

	protected abstract List<CircleFinding> findCircleCoordinates()
			throws IOException;

	/**
	 * Returns the circle findings found in that image.
	 * 
	 * @return the circle findings found in that image.
	 */
	public List<CircleFinding> getCirclePositionsInImage() {
		return circleCoordinates;
	}

	@Override
	public void run() {

		try {
			circleCoordinates = findCircleCoordinates();
		} catch (final IOException e) {
			System.err.println("Error reding Imagefile.");
		}

		notifyObservers(circleCoordinates);

	}
}