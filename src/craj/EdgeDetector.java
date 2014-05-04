package craj;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import processing.core.PApplet;

/**
 * This class provides methods to detect the edges using a given Sobel-operator.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class EdgeDetector {

	/**
	 * This class holds the parameter data for the <tt>EdgeDetector</tt>
	 */
	public static class EdgeDetectorParameter {
		private final int edgeDetectionTolerance;
		private final int sobelMatrixSize;
		private final boolean verbose;

		/**
		 * Construct a <tt>EdgeDetectorParameter</tt> parameter object.
		 * 
		 * @param sobelMatrixSize
		 *            the size of the generated Sobel-matrix. Should always be
		 *            greater or equal 3 and odd
		 * @param edgeDetectionTolerance
		 *            the minimum pixel value to be considered an edge pixel
		 * @param verbose
		 *            a boolean which indicates if the methods of this class
		 *            should show it's output
		 * 
		 */
		public EdgeDetectorParameter(final int sobelMatrixSize,
				final int edgeDetectionTolerance, final boolean verbose) {
			super();
			this.sobelMatrixSize = sobelMatrixSize;
			this.edgeDetectionTolerance = edgeDetectionTolerance;
			this.verbose = verbose;
		}
	}

	private final EdgeDetectorParameter parameters;

	/**
	 * Create an EdgeDetector object with the specified parameters.
	 * 
	 * @param parameters
	 *            the EdgeDetectorParameter parameter object which specifies the
	 *            <code>sobelMatrixSize</code>, the
	 *            <code>edgeDetectionTolerance</code> and if the class should be
	 *            <code>verbose</code>.
	 * @see EdgeDetectorParameter
	 */
	public EdgeDetector(final EdgeDetectorParameter parameters) {
		super();
		this.parameters = parameters;
	}

	private int[][] findEdges(final int[][] imageDataToFindEdgesIn,
			final EdgeDetectorParameter parameters) {

		final int sobelMatrixSize = parameters.sobelMatrixSize;
		final SobelMatrix sobelMatrix = new SobelMatrix(sobelMatrixSize);

		final int sobelMiddle = sobelMatrix.getMiddle();

		final int width = imageDataToFindEdgesIn.length;
		final int height = imageDataToFindEdgesIn[0].length;

		final int xStart = sobelMiddle;
		final int yStart = sobelMiddle;

		final int xEnd = width - sobelMiddle;
		final int yEnd = height - sobelMiddle;

		final int[][] foundEdges = new int[width][height];

		int maxSobelValue = 0;

		for (int x = xStart; x < xEnd; x++) {
			for (int y = yStart; y < yEnd; y++) {
				final int sobelValue = sobelMatrix.getSobelValueForPixel(
						imageDataToFindEdgesIn, x, y);

				maxSobelValue = Math.max(maxSobelValue, sobelValue);

				foundEdges[x][y] = sobelValue;
			}
		}

		mapEdgesData(foundEdges, maxSobelValue);

		final boolean verbose = parameters.verbose;
		if (verbose) {
			final ShortProcessor processor = new ShortProcessor(width, height);
			processor.setIntArray(foundEdges);

			final String title = String.format(
					"Sharped Edges with sobel matrix size %d", sobelMatrixSize);

			new ImagePlus(title, processor).show();
		}

		return foundEdges;
	}

	/**
	 * Returns the edges data of the given ImageProcessor
	 * 
	 * @param imageProcessor
	 *            the image processor in which the edges should be found.
	 * @return the edges data of the given <tt>ImageProcessor</tt>
	 */
	public int[][] getEdgesData(final ImageProcessor imageProcessor) {
		final int[][] edgesImage = findEdges(imageProcessor.getIntArray(),
				parameters);

		return edgesImage;
	}

	private void mapEdgesData(final int[][] foundEdges, final int maxValue) {
		final int width = foundEdges.length;
		final int height = foundEdges[0].length;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final int edgesPixelValue = foundEdges[x][y];

				final float mappedEdgesPixelValue = PApplet.map(
						edgesPixelValue, 0, maxValue, 0, 255);

				setEdgePixelWithTolerance(foundEdges, x, y,
						(int) mappedEdgesPixelValue);
			}
		}
	}

	private void setEdgePixelWithTolerance(final int[][] edgesPixelData,
			final int x, final int y, final int value) {

		final int detectionTolerance = parameters.edgeDetectionTolerance;

		if (value >= detectionTolerance) {
			edgesPixelData[x][y] = value;
		} else {
			edgesPixelData[x][y] = 0;
		}
	}
}
