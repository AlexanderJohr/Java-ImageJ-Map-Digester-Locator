package craj.mapDigesterLocator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import processing.core.PVector;
import craj.CircleFinding;

/**
 * This class extends the MapDigesterLocator for the functionality to get all
 * images from Google Maps in the specified range between the given longitude
 * and latitude.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class MapDigesterLocatorForGMaps extends
		MapDigesterLocator<GmapsCoordinateCircleFindingsPair> {

	/**
	 * This class holds the parameter data for the
	 * {@link MapDigesterLocatorForGMaps}
	 */
	public static class MapDigesterLocatorForGMapsParameter {
		private final float latitudeStart;
		private final float latitudeStop;
		private final float longitudeStart;
		private final float longitudeStop;
		private final String mapsImageUrlPattern;
		private final MapDigesterLocatorParameter params;
		private final float stepSize;

		/**
		 * 
		 * @param params
		 *            the parameter object for the {@link MapDigesterLocator}
		 * @param longitudeStart
		 *            the start longitude
		 * @param latitudeStart
		 *            the start latitude
		 * @param longitudeStop
		 *            the stop longitude
		 * @param latitudeStop
		 *            the stop latitude
		 * @param stepSize
		 *            the step size in which between the start and stop of the
		 *            longitude and latitude should be iterated
		 * @param mapsImageUrlPattern
		 *            the url pattern which is used with the
		 *            <code>String.format</code>-method to generate a
		 *            request-url for Google Maps with the specified longitude
		 *            and latitude.
		 */
		public MapDigesterLocatorForGMapsParameter(
				final MapDigesterLocatorParameter params,
				final float longitudeStart, final float latitudeStart,
				final float longitudeStop, final float latitudeStop,
				final float stepSize, final String mapsImageUrlPattern) {
			this.params = params;
			this.longitudeStart = longitudeStart;
			this.latitudeStart = latitudeStart;
			this.longitudeStop = longitudeStop;
			this.latitudeStop = latitudeStop;
			this.stepSize = stepSize;
			this.mapsImageUrlPattern = mapsImageUrlPattern;
		}
	}

	private final float longitudeStart, latitudeStart;

	private final float longitudeStop, latitudeStop;
	private final String mapsImageUrlPattern;

	private final float stepSize;

	private final HashMap<CircleFinderThread, PVector> threadCoordinateMap = new HashMap<>();

	/**
	 * Create a {@link MapDigesterLocatorForGMaps} object with the given
	 * {@link MapDigesterLocatorForGMapsParameter} parameter object.
	 * 
	 * @param params
	 *            the {@link MapDigesterLocatorForGMapsParameter} parameter
	 *            object for the {@link MapDigesterLocatorForGMaps}
	 */
	public MapDigesterLocatorForGMaps(
			final MapDigesterLocatorForGMapsParameter params) {
		super(params.params);

		if (params.longitudeStart <= params.longitudeStop) {
			longitudeStart = params.longitudeStart;
			longitudeStop = params.longitudeStop;
		} else {
			throw new IllegalArgumentException(
					"Start longitude must be lower then end longitude.");
		}

		if (params.latitudeStart >= params.latitudeStop) {
			latitudeStart = params.latitudeStart;
			latitudeStop = params.latitudeStop;
		} else {
			throw new IllegalArgumentException(
					"Start latitude must be higher then end latitude.");
		}

		mapsImageUrlPattern = params.mapsImageUrlPattern;

		stepSize = params.stepSize;

		addAllImagesToCalculationPipe();
	}

	private void addAllImagesToCalculationPipe() {
		for (float lon = longitudeStart; lon < longitudeStop; lon += stepSize) {
			for (float lat = latitudeStart; lat > latitudeStop; lat -= stepSize) {

				try {
					final File imageFile = getMapImage(lat, lon);
					final CircleFinderThread thread = addImageToCalculationPipe(imageFile);
					// lon = x, lat = y
					threadCoordinateMap.put(thread, new PVector(lon, lat));
					threadFileMap.put(thread, imageFile);

				} catch (final IOException e) {
					System.err.println("No Image provided from Google!");
				}

			}
		}
	}

	private File getMapImage(final float x, final float y) throws IOException {

		final File imgDir = params.imgDir;
		final boolean verbosePrint = params.verbosePrint;

		final File fileOnDisc = new File(imgDir, "X_" + Float.toHexString(x)
				+ "Y_" + Float.toHexString(y) + ".jpg");

		// If it exists on disc, load it, request it from google otherwise
		if (fileOnDisc.exists()) {
			if (verbosePrint) {
				System.out.println("Get from Disc: " + fileOnDisc.toPath());
			}
		} else {
			final String requestUrl = String.format(mapsImageUrlPattern,
					Float.toString(x), Float.toString(y));

			if (verbosePrint) {
				System.out.println("Get from Web: " + requestUrl);
			}

			// Request from Google
			BufferedImage responseImage;

			responseImage = ImageIO.read(new URL(requestUrl));

			// Save on disc for further use
			ImageIO.write(responseImage, "jpg", fileOnDisc);
		}

		return fileOnDisc;
	}

	@Override
	protected void handleFinishedFinderThread(
			final CircleFinderThread finishedFinderThread,
			final List<CircleFinding> circleCoordinates) {
		super.handleFinishedFinderThread(finishedFinderThread,
				circleCoordinates);

		final PVector foundedCoordinate = threadCoordinateMap
				.remove(finishedFinderThread);

		final boolean foundSomething = circleCoordinates.size() != 0;
		final boolean notifyEvenWithoutFinding = params.notifyEvenWithoutFinding;

		if (foundSomething || notifyEvenWithoutFinding) {
			final File file = threadFileMap.remove(finishedFinderThread);
			final GmapsCoordinateCircleFindingsPair coordinateCircleFindingsPair = new GmapsCoordinateCircleFindingsPair(
					file, foundedCoordinate, circleCoordinates);

			notifyObservers(coordinateCircleFindingsPair);
		}
	}

}
