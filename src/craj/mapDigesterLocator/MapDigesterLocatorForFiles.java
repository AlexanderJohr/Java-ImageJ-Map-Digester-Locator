package craj.mapDigesterLocator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import craj.CircleFinding;

/**
 * This class extends the MapDigesterLocator for the functionality to get all
 * files from the given directory and starts the search for circles of these
 * files.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class MapDigesterLocatorForFiles extends
		MapDigesterLocator<FileCircleFindingsPair> {

	/**
	 * Create a {@link MapDigesterLocatorForFiles} object with the given
	 * {@link MapDigesterLocatorParameter} object.
	 * 
	 * @param params
	 *            the parameter object for the
	 *            {@link MapDigesterLocatorForFiles}
	 */
	public MapDigesterLocatorForFiles(final MapDigesterLocatorParameter params) {
		super(params);

		final File imgDir = params.imgDir;

		addAllFilesToCalculationPipe(imgDir);
	}

	private void addAllFilesToCalculationPipe(final File imgDir) {

		final boolean verbosePrint = params.verbosePrint;

		final File[] files = imgDir.listFiles();

		for (final File imageFile : files) {
			if (imageFile.isFile() && imageFile.canRead()) {
				if (verbosePrint) {
					System.out.println("Search in: " + imageFile.getName());
				}

				try {
					final CircleFinderThread thread = addImageToCalculationPipe(imageFile);
					threadFileMap.put(thread, imageFile);
				} catch (final IOException e) {
					System.err.println("Error reading File from Disc!");
				}

			}
		}
	}

	@Override
	protected void handleFinishedFinderThread(
			final CircleFinderThread finishedFinderThread,
			final List<CircleFinding> circleCoordinates) {
		super.handleFinishedFinderThread(finishedFinderThread,
				circleCoordinates);

		final boolean notifyEvenWithoutFinding = params.notifyEvenWithoutFinding;

		final boolean foundSomething = circleCoordinates.size() != 0;
		if (foundSomething || notifyEvenWithoutFinding) {
			final File file = threadFileMap.remove(finishedFinderThread);
			final FileCircleFindingsPair fileCircleFindingsPair = new FileCircleFindingsPair(
					file, circleCoordinates);

			notifyObservers(fileCircleFindingsPair);
		}
	}

}
