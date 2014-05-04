package craj.mapDigesterLocator;

import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.util.ArrayList;
import java.util.List;

import craj.CircleCalculationResultTable;
import craj.CircleCrossCalculator;
import craj.CircleFinding;
import craj.ContrastEnhancer;
import craj.EdgeDetector;
import craj.ReferenceCircle;

public class MapDigesterCircleFinder {
	/**
	 * An Class for holding parameter-values for the
	 * <tt>MapDigesterCircleFinderThread</tt>
	 */
	public static class MapDigesterCircleFinderParameter {
		public final List<ContrastEnhancer> contrastEnhancerList;
		public final EdgeDetector edgeDetectorForBlackValueCalculation;
		public final EdgeDetector edgeDetectorForCrossValueCalculation;
		public final List<ReferenceCircle> referenceCircles;
		public final boolean verbose;

		/**
		 * Construct an MapDigesterCircleFinderParameter object
		 * 
		 * @param edgeDetectorForBlackValueCalculation
		 *            an edge detector for the calculation of the inner areas of
		 *            the circle-candidates
		 * @param edgeDetectorForCrossValueCalculation
		 *            an edge detector for the calculation of the crosses of the
		 *            circles
		 * @param contrastEnhancerList
		 *            a list of contrast-enhancer which are all used to generate
		 *            sharper edges for various images
		 * @param verbose
		 *            a boolean which determines if the calculated images should
		 *            be shown
		 * @param referenceCircles
		 *            a list of reference-circles which are searched in the
		 *            given image
		 */
		public MapDigesterCircleFinderParameter(
				final EdgeDetector edgeDetectorForBlackValueCalculation,
				final EdgeDetector edgeDetectorForCrossValueCalculation,
				final List<ContrastEnhancer> contrastEnhancerList,
				final boolean verbose,
				final List<ReferenceCircle> referenceCircles) {
			this.edgeDetectorForBlackValueCalculation = edgeDetectorForBlackValueCalculation;
			this.edgeDetectorForCrossValueCalculation = edgeDetectorForCrossValueCalculation;
			this.contrastEnhancerList = contrastEnhancerList;
			this.verbose = verbose;
			this.referenceCircles = referenceCircles;
		}
	}

	private final MapDigesterCircleFinderParameter params;

	public MapDigesterCircleFinder(final MapDigesterCircleFinderParameter params) {
		this.params = params;
	}

	public List<CircleFinding> getCircleCoordinates(
			final ImageProcessor processor) {
		final List<ContrastEnhancer> contrastEnhancerList = params.contrastEnhancerList;

		final EdgeDetector edgeDetectorForCrossValueCalculation = params.edgeDetectorForCrossValueCalculation;
		final EdgeDetector edgeDetectorForBlackValueCalculation = params.edgeDetectorForBlackValueCalculation;
		final List<ReferenceCircle> referenceCircles = params.referenceCircles;
		final boolean verbose = params.verbose;

		final List<CircleFinding> circleCoordinates = new ArrayList<>();

		final int width = processor.getWidth();
		final int height = processor.getHeight();

		for (final ContrastEnhancer contrastEnhancer : contrastEnhancerList) {

			final ImageProcessor duplicateProcessor = processor.duplicate();
			final int[][] imageData = duplicateProcessor.getIntArray();
			contrastEnhancer.enhanceContrast(imageData);

			final ColorProcessor colorProcessor = new ColorProcessor(width,
					height);
			colorProcessor.setIntArray(imageData);

			if (verbose) {
				new ImagePlus("After ContrastEnhancer", colorProcessor).show();
			}

			final ShortProcessor shortProcessor = colorProcessor
					.convertToShortProcessor(true);

			final int[][] crossValueEdgesData = edgeDetectorForCrossValueCalculation
					.getEdgesData(shortProcessor);
			final int[][] blackValueEdgesData = edgeDetectorForBlackValueCalculation
					.getEdgesData(shortProcessor);

			for (final ReferenceCircle referenceCircle : referenceCircles) {

				final CircleCrossCalculator circleCrossCalculator = new CircleCrossCalculator(
						referenceCircle, crossValueEdgesData,
						referenceCircle.getPerfectCircleCoordinates());

				final CircleCalculationResultTable resultTable;
				resultTable = circleCrossCalculator.getResultTable();

				if (verbose) {
					final ImagePlus outputImage = resultTable
							.getCalculationOutputImage();
					outputImage.show();
				}

				final List<CircleFinding> circleFindingsForReferenceCircle = resultTable
						.getCircleFindings(blackValueEdgesData);
				circleCoordinates.addAll(circleFindingsForReferenceCircle);

			}
		}

		return circleCoordinates;
	}
}
