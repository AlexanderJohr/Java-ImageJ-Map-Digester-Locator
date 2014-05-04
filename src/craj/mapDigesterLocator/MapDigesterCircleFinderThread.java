package craj.mapDigesterLocator;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import craj.CircleFinding;
import craj.mapDigesterLocator.MapDigesterCircleFinder.MapDigesterCircleFinderParameter;

/**
 * Finds circles in a given image. This finder is used to find digesters in
 * satellite-maps.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class MapDigesterCircleFinderThread extends CircleFinderThread {

	private final MapDigesterCircleFinderParameter params;

	/**
	 * Constructs an MapDigesterCircleFinderThread for the given image-file and
	 * parameters for the calculation
	 * 
	 * @param imageFile
	 *            the image-file in which circles should be found
	 * @param params
	 *            the constraints which determine how the circles are searched
	 */
	public MapDigesterCircleFinderThread(final File imageFile,
			final MapDigesterCircleFinderParameter params) {
		super(imageFile);
		this.params = params;
	}

	@Override
	protected List<CircleFinding> findCircleCoordinates() throws IOException {

		final BufferedImage imageFromDiscToFindDigesturesIn = ImageIO
				.read(imageFile);

		final ImagePlus imagePlus = new ImagePlus("Image To find Circles In",
				imageFromDiscToFindDigesturesIn);

		final boolean verbose = params.verbose;
		if (verbose) {
			imagePlus.show();
		}

		final ImageProcessor processor = imagePlus.getProcessor();

		final MapDigesterCircleFinder mapDigesterCircleFinder = new MapDigesterCircleFinder(
				params);

		final List<CircleFinding> circleCoordinates = mapDigesterCircleFinder
				.getCircleCoordinates(processor);

		return circleCoordinates;
	}
}
