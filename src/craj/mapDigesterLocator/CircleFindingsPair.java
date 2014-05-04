package craj.mapDigesterLocator;

import java.io.File;
import java.util.List;

import craj.CircleFinding;

/**
 * A pair of an image and a list of circles which where found in the image.
 * 
 * @author Alexander Johr u26865 m18927
 */
abstract class CircleFindingsPair {
	/**
	 * the file in which circles where found
	 */
	public final List<CircleFinding> circleFindings;
	/**
	 * the file in which circles where found
	 */
	public final File file;

	/**
	 * Construct an CircleFindingsPair with the given file and circle-findings.
	 * 
	 * @param file
	 *            the file in which circles where found
	 * @param circleFindings
	 *            the circle findings in the file
	 */
	public CircleFindingsPair(final File file,
			final List<CircleFinding> circleFindings) {
		this.file = file;
		this.circleFindings = circleFindings;
	}
}