package craj;

import craj.PerfectCircleCoordinates.DifferenceFromOrigin;

/**
 * This class is used to calculate the count and the sum of intensity of circles
 * drawn around every pixel in an given edges image.
 * <p>
 * First it iterates over all pixels in the edge pixel data and decides to
 * calculate further only if the edge pixel value is higher then zero.
 * <p>
 * Then for every edge pixel a circle is drawn with the same origin coordinate
 * as the edge pixel in the cross count table and the cross value table.
 * <p>
 * In the cross count table the count is increased by one to show that the
 * circle crosses this pixel. These counts represent how often an edge circle
 * crossed this pixel.
 * <p>
 * In the cross value table the value is increased by the intensity of the edge
 * pixel to calculate the sum of intensity off all crossing edges pixel.
 * <p>
 * All pixels which have more or equal of the cross value and the cross count of
 * the given reference circle reference cross value and cross count, the pixel
 * is considered to be a circle candidate.
 * <p>
 * Finally for all circle candidate the average black value of the inner circle
 * pixels is calculated. If the black value is higher oder equal the reference
 * black value, the pixel is considered to be a circle finding.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class CircleCrossCalculator {

	private final int[][] crossValueCounts;
	private final int[][] crossValues;
	private final ReferenceCircle referenceCircle;

	/**
	 * Create a <tt>CircleCrossCalculator</tt> for the given reference circle
	 * and circle pixel coordinates to draw circles around every edge pixel in
	 * the given edges data array and calculate count and the sum of intensity
	 * of the edges pixels for every pixel in the resulting
	 * <tt>CircleCalculationResultTable</tt> where circle pixels cross each
	 * other.
	 * 
	 * @param referenceCircle
	 *            the reference circle for the resulting
	 *            <tt>CircleCalculationResultTable</tt>
	 * @param edgesDataArray
	 *            the edges image data where for every edge pixel a circle is
	 *            drawn in the result tables
	 * @param circleCoordinates
	 *            the pixel data of the circle which is drawn around every edge
	 *            pixel
	 */
	public CircleCrossCalculator(final ReferenceCircle referenceCircle,
			final int[][] edgesDataArray,
			final PerfectCircleCoordinates circleCoordinates) {
		super();

		final int width = edgesDataArray.length;
		final int height = edgesDataArray[0].length;

		this.referenceCircle = referenceCircle;

		crossValues = new int[width][height];
		crossValueCounts = new int[width][height];

		calcCrossValues(edgesDataArray, referenceCircle, circleCoordinates);
	}

	private void calcCrossValues(final int[][] edgesDataArray,
			final ReferenceCircle referenceCircle,
			final PerfectCircleCoordinates circleCoordinates) {

		final int width = edgesDataArray.length;
		final int height = edgesDataArray[0].length;

		// Iterate over all pixels of the edge detection image
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final boolean isEdgePixel = edgesDataArray[x][y] != 0;

				if (isEdgePixel) {

					final int edgesDataPixel = edgesDataArray[x][y];

					for (final DifferenceFromOrigin point : circleCoordinates
							.getEdgeDifferencesFromCircleOrigin()) {
						// Clockwise + + ; + - ; - - ; - +

						final int xPlusX = x + point.x;
						final int yPlusY = y + point.y;

						increaseIfInRange(edgesDataPixel, xPlusX, yPlusY);
					}
				}
			}
		}

	}

	/**
	 * Returns the calculated cross values and cross value counts for the
	 * specified reference circle as an <tt>CircleCalculationResultTable</tt>
	 * 
	 * @return the calculated cross values and cross value counts for the
	 *         specified reference circle
	 */
	public CircleCalculationResultTable getResultTable() {
		final CircleCalculationResultTable resultTable;
		resultTable = new CircleCalculationResultTable(crossValues,
				crossValueCounts, referenceCircle);
		return resultTable;
	}

	private void increaseIfInRange(final int edgesDataPixel, final int xToSet,
			final int yToSet) {

		if (isInRange(crossValues, xToSet, yToSet)) {
			crossValues[xToSet][yToSet] += edgesDataPixel;
			crossValueCounts[xToSet][yToSet]++;
		}

	}

	private boolean isInRange(final int[][] data, final int cx, final int cy) {
		if (cx >= 0 && cx < data.length && cy >= 0 && cy < data[0].length) {
			return true;
		} else {
			return false;
		}
	}

}
