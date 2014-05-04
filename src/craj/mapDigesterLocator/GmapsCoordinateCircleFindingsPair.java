package craj.mapDigesterLocator;

import java.io.File;
import java.util.List;

import processing.core.PVector;
import craj.CircleFinding;

/**
 * A pair of an image and a list of circles which where found in the image
 * extended with the coordinate of the image.
 * 
 * @author Alexander Johr u26865 m18927
 */
public class GmapsCoordinateCircleFindingsPair extends CircleFindingsPair {
	/**
	 * the coordinate of the file in which circles where found
	 */
	public final PVector coordinate;

	/**
	 * Construct an GmapsCoordinateCircleFindingsPair with the given file,
	 * coordinate and circle-findings.
	 * 
	 * @param file
	 *            the file in which circles where found
	 * @param coordinate
	 *            the coordinate of the file in which circles where found
	 * @param circleFindings
	 *            the circle findings in the file
	 */
	public GmapsCoordinateCircleFindingsPair(final File file,
			final PVector coordinate, final List<CircleFinding> circleFindings) {
		super(file, circleFindings);
		this.coordinate = coordinate;
	}
}