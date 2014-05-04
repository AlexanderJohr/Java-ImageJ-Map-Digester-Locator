package craj.mapDigesterLocator;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import craj.CircleFinding;
import craj.mapDigesterLocator.MapDigesterCircleFinder.MapDigesterCircleFinderParameter;

/**
 * This abstract class implements the functionality of creating as much as
 * {@link CircleFinderThread} objects as processor cores are available. It is
 * possible to wait for the completion of the calculation of all files with the
 * <code>wait</code>-method. If the calculation of an image completes, the
 * observing objects are notified with the circle findings.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 * @param <CircleFindingsPairType>
 *            the type defines what argument type is delivered by the update
 *            method of the {@link GenericObservable}
 */
public abstract class MapDigesterLocator<CircleFindingsPairType extends CircleFindingsPair>
		extends GenericObservable<CircleFindingsPairType> implements
		GenericObserver<List<CircleFinding>> {

	/**
	 * This class holds the parameter data for the <tt>MapDigesterLocator</tt>
	 */
	public static class MapDigesterLocatorParameter {
		protected final File imgDir;
		private final MapDigesterCircleFinderParameter mapDigesterCircleFinderParameters;
		protected final boolean notifyEvenWithoutFinding;
		protected final boolean verbose, verbosePrint;

		/**
		 * Construct a <tt>MapDigesterLocatorParameters</tt> parameter object.
		 * 
		 * @param notifyEvenWithoutFinding
		 *            a boolean which indicates if the searched image should be
		 *            shown even if there was no circle finding
		 * @param verbose
		 *            a boolean which indicates if the methods of this class
		 *            should show it's output
		 * @param verbosePrint
		 *            a boolean which indicates if the methods of this class
		 *            should print it's output
		 * @param imgDir
		 *            the directory in which the image files can be found
		 * @param mapDigesterCircleFinderParameters
		 *            the parameter object for the calculation in the
		 *            <tt>MapDigesterCircleFinderThread</tt>
		 */
		public MapDigesterLocatorParameter(
				final boolean notifyEvenWithoutFinding,
				final boolean verbose,
				final boolean verbosePrint,
				final File imgDir,
				final MapDigesterCircleFinderParameter mapDigesterCircleFinderParameters) {
			this.notifyEvenWithoutFinding = notifyEvenWithoutFinding;
			this.verbose = verbose;
			this.verbosePrint = verbosePrint;
			this.imgDir = imgDir;
			this.mapDigesterCircleFinderParameters = mapDigesterCircleFinderParameters;
		}
	}

	private Object callerThread;

	protected Point[] circlePositionsInImage;
	private final int cores;
	private int currentThreadIndex;

	protected final MapDigesterLocatorParameter params;
	private int runningThreadCount = 0;

	private final ArrayList<Thread> runningThreads = new ArrayList<>();

	protected HashMap<CircleFinderThread, File> threadFileMap = new HashMap<>();

	protected int threadsRunning = 0;

	/**
	 * Create a {@link MapDigesterLocator} object with the given
	 * {@link MapDigesterLocatorParameter} object.
	 * 
	 * @param mapDigesterLocatorParameters
	 *            the parameter object for the {@link MapDigesterLocator}
	 */
	public MapDigesterLocator(
			final MapDigesterLocatorParameter mapDigesterLocatorParameters) {
		super();

		this.params = mapDigesterLocatorParameters;

		final File imgDir = mapDigesterLocatorParameters.imgDir;

		makeSureImgDirIsCreated(imgDir);

		cores = getAvailableProcessorCount();
	}

	protected CircleFinderThread addImageToCalculationPipe(final File imageFile)
			throws IOException {
		CircleFinderThread runnable = null;

		final MapDigesterCircleFinderParameter mapDigesterCircleFinderParameters;
		mapDigesterCircleFinderParameters = params.mapDigesterCircleFinderParameters;

		runnable = new MapDigesterCircleFinderThread(imageFile,
				mapDigesterCircleFinderParameters);
		runnable.addObserver(this);

		final Thread thread = new Thread(runnable, "CircleFinderThread "
				+ runningThreads.size());
		runningThreads.add(thread);

		increaseRunningThreadCount();

		return runnable;
	}

	private boolean allInnerThreadsAreDone() {
		final boolean allInnerThreadsAreDone = runningThreadCount == 0;
		return allInnerThreadsAreDone;
	}

	private synchronized void decreaseRunningThreadCount() {
		runningThreadCount--;

	}

	private int getAvailableProcessorCount() {
		return Runtime.getRuntime().availableProcessors();
	}

	protected synchronized void handleFinishedFinderThread(
			final CircleFinderThread finishedFinderThread,
			final List<CircleFinding> circleCoordinates) {

		threadsRunning--;

		final boolean verbosePrint = params.verbosePrint;

		if (verbosePrint) {
			final String doneNotification = String.format(
					"Done Calculating image, %d images to go.",
					runningThreads.size());
			System.out.println(doneNotification);
		}

		final boolean stillImagesToCalculate = !runningThreads.isEmpty();
		if (stillImagesToCalculate) {
			runningThreads.remove(0).start();
		}

		System.out.println();
	}

	private synchronized void increaseRunningThreadCount() {
		runningThreadCount++;
	}

	/**
	 * Waits for all calculation threads to complete.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void join() throws InterruptedException {
		callerThread = this;
		callerThread.wait();
	}

	private void makeSureImgDirIsCreated(final File imgDir) {
		if (!imgDir.exists()) {
			imgDir.mkdirs();
		}
	}

	private synchronized void notifyCallingThread() {
		callerThread.notify();
	}

	/**
	 * Starts the calculation threads.
	 */
	public synchronized void start() {
		startCalculating();
	}

	private synchronized void startCalculating() {
		for (currentThreadIndex = 0; currentThreadIndex < cores
				&& !runningThreads.isEmpty(); currentThreadIndex++) {
			runningThreads.remove(0).start();
		}
	}

	@Override
	public void update(
			final GenericObservable<List<CircleFinding>> oberservable,
			final List<CircleFinding> circleCoordinates) {

		final CircleFinderThread finishedFinderThread = (CircleFinderThread) oberservable;
		handleFinishedFinderThread(finishedFinderThread, circleCoordinates);

		decreaseRunningThreadCount();

		if (allInnerThreadsAreDone()) {
			notifyCallingThread();
		}
	}
}
