package craj;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * Holds the calculation result data and provides methods to get the maximum
 * value, a visualization of the data, and the circle findings regarding the
 * given reference circle values.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class CircleCalculationResultTable {

	private static final Font FONT = new Font("Courier New", Font.PLAIN, 8);

	private static final int INITIAL_MAX_CROSS_COUNT = -1;
	private static final int INITIAL_MAX_CROSS_VALUE = -1;

	private final int[][] crossCounts;

	private final int[][] crossValues;
	private int maxCrossCount = CircleCalculationResultTable.INITIAL_MAX_CROSS_COUNT;

	private int maxCrossValue = CircleCalculationResultTable.INITIAL_MAX_CROSS_VALUE;

	private final ReferenceCircle referenceCircle;

	/**
	 * Construct a CircleCalculationResultTable for the given referenceCircle,
	 * crossValues and crossValueCounts.
	 * 
	 * @param crossValues
	 *            the cross values for the image for this reference circle
	 * @param crossValueCounts
	 *            the cross counts for the image for this reference circle
	 * @param referenceCircle
	 *            the reference circle for this result data
	 */
	public CircleCalculationResultTable(final int[][] crossValues,
			final int[][] crossValueCounts,
			final ReferenceCircle referenceCircle) {

		if (crossValues.length == 0 || crossValues[0].length == 0) {
			throw new IllegalArgumentException(
					"Width and height of the 2D-array crossValues can't be 0");
		}
		if (crossValueCounts.length == 0 || crossValueCounts[0].length == 0) {
			throw new IllegalArgumentException(
					"Width and height of the 2D-array crossValueCounts can't be 0");
		}
		if (crossValueCounts.length != crossValues.length
				&& crossValueCounts[0].length != crossValues[0].length) {
			throw new IllegalArgumentException(
					"Width and height of crossValues and crossValueCounts have to be equal.");
		}

		this.crossValues = crossValues;
		crossCounts = crossValueCounts;
		this.referenceCircle = referenceCircle;
	}

	private int calculateMaxCrossCount() {
		int currentMaxCrossCount = 0;
		for (int x = 0; x < crossCounts.length; x++) {
			for (int y = 0; y < crossCounts[0].length; y++) {
				currentMaxCrossCount = Math.max(currentMaxCrossCount,
						crossCounts[x][y]);
			}
		}
		return currentMaxCrossCount;
	}

	private int calculateMaxCrossValue() {
		int currentMaxCrossValue = 0;
		for (int x = 0; x < crossValues.length; x++) {
			for (int y = 0; y < crossValues[0].length; y++) {
				currentMaxCrossValue = Math.max(currentMaxCrossValue,
						crossValues[x][y]);
			}
		}
		return currentMaxCrossValue;
	}

	/**
	 * Returns an ImagePlus object with the result data *
	 * 
	 * @return an ImagePlus object with the result data
	 */
	public ImagePlus getCalculationOutputImage() {

		final int width = crossValues.length;
		final int height = crossValues[0].length;

		final int[][] outputValues = new int[width][height];

		final int maxValue = getMaxCrossValue();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final int pixelValue = crossValues[x][y];

				final float mapRgb = PApplet.map(pixelValue, 0, maxValue, 0,
						255);
				final int interpolatedPixelValue = Math.round(mapRgb);

				outputValues[x][y] = interpolatedPixelValue;

			}
		}
		final ShortProcessor shortProcessor = new ShortProcessor(width, height);
		shortProcessor.setIntArray(outputValues);

		final int diameter = referenceCircle.diameter;
		final String title = String.format("Calculation Output Image: %d",
				diameter);

		final ImagePlus calculationOutputImage = new ImagePlus(title,
				shortProcessor);

		return calculationOutputImage;
	}

	/**
	 * Returns an ImagePlus object with the result data plus the numbers of the
	 * pixel count and cross values mapped to a letter count of 3.
	 * 
	 * 
	 * @return an ImagePlus object with the result data plus the numbers of the
	 *         pixel count and cross values
	 */
	public ImagePlus getCalculationOutputImageWithNumbers() {

		final ImagePlus outputImageWithoutNumbers = getCalculationOutputImage();
		final ImageProcessor shortProcessor = outputImageWithoutNumbers
				.getProcessor();

		final int width = shortProcessor.getWidth();
		final int height = shortProcessor.getHeight();

		final int referenceCrossValue;
		referenceCrossValue = referenceCircle.referenceCrossValue;

		final int resizeFactor = 15;
		final int resizedWidth = width * resizeFactor, resizedHeight = height
				* resizeFactor;

		final ImageProcessor resizedImage = shortProcessor.resize(resizedWidth,
				resizedHeight);
		resizedImage.setFont(CircleCalculationResultTable.FONT);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final int pixelCrossValue = crossValues[x][y];
				final int pixelCrossCount = crossCounts[x][y];

				final int mapedCrossValue = Math.round(PApplet.map(
						pixelCrossValue, 0, referenceCrossValue, 0, 999));

				final String crossValueText = Integer.toString(mapedCrossValue);
				final String crossCountText = Integer.toString(pixelCrossCount);

				resizedImage.drawString(crossValueText, x * resizeFactor, y
						* resizeFactor + 1);
				resizedImage.drawString(crossCountText, x * resizeFactor, y
						* resizeFactor - 6);

			}
		}

		final ImagePlus calculationOutputImage = new ImagePlus(
				"Calculation Output Image" + referenceCircle.diameter,
				resizedImage);

		return calculationOutputImage;

	}

	/**
	 * Returns the circle findings regarding the reference circle and the given
	 * blackValueEdgesData where the coordinate of the circle finding is
	 * searched to approve, that the average black value in the inner circle is
	 * high enough to declare it a circle finding.
	 * 
	 * @param blackValueEdgesData
	 *            the edges data where all circle candidates are searched to
	 *            calculate the average inner circle black value
	 * @return the circle findings
	 */
	public List<CircleFinding> getCircleFindings(
			final int[][] blackValueEdgesData) {

		final int width = crossValues.length;
		final int height = crossValues[0].length;

		final int minCrossValue = referenceCircle.minCrossValue;
		final int minCrossCount = referenceCircle.minCrossCount;
		final int minBlackValue = referenceCircle.minBlackValue;

		final ArrayList<CircleFinding> circleCoordinates = new ArrayList<>();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final int pixelCrossValue = crossValues[x][y];
				final int pixelCrossCount = crossCounts[x][y];

				if (pixelCrossValue >= minCrossValue
						&& pixelCrossCount >= minCrossCount) {

					final CircleFinding circleCandidate = new CircleFinding(x,
							y, referenceCircle, blackValueEdgesData,
							pixelCrossValue, pixelCrossCount);

					final float averagePixelBlackValue = circleCandidate.averagePixelBlackValue;

					if (averagePixelBlackValue >= minBlackValue) {
						circleCoordinates.add(circleCandidate);
					}
				}
			}
		}

		return circleCoordinates;
	}

	/**
	 * Returns the maximum cross count. The maximum is only calculated the fist
	 * time this method is called. If this method is not called, the calculation
	 * of the maximum is ignored, for it is time consuming.
	 * 
	 * @return the maximum cross count
	 */
	public int getMaxCrossCount() {
		if (maxCrossCount == CircleCalculationResultTable.INITIAL_MAX_CROSS_COUNT) {
			maxCrossCount = calculateMaxCrossCount();
		}
		return maxCrossCount;
	}

	/**
	 * Returns the maximum cross value. The maximum is only calculated the fist
	 * time this method is called. If this method is not called, the calculation
	 * of the maximum is ignored, for it is time consuming.
	 * 
	 * @return the maximum cross value
	 */
	public int getMaxCrossValue() {
		if (maxCrossValue == CircleCalculationResultTable.INITIAL_MAX_CROSS_VALUE) {
			maxCrossValue = calculateMaxCrossValue();
		}

		return maxCrossValue;
	}

}
