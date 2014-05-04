package craj.mapDigesterLocator;

import java.io.File;
import java.util.List;

import craj.CircleFinding;

/**
 * A pair of an image and a list of circles which where found in the image.
 * 
 * @author Alexander Johr u26865 m18927
 */
public class FileCircleFindingsPair extends CircleFindingsPair {

	/**
	 * Construct an FileCircleFindingsPair with the given file and
	 * circle-findings.
	 * 
	 * @param file
	 *            the file in which circles where found
	 * @param circleFindings
	 *            the circle findings in the file
	 */
	public FileCircleFindingsPair(final File file,
			final List<CircleFinding> circleFindings) {
		super(file, circleFindings);
	}
}