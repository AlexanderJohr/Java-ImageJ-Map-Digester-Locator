package craj;

import ij.process.ImageProcessor;

import java.awt.Color;
import java.util.Map;

/**
 * This class holds the data of a circle finding. It is used to store in which
 * image the circle was found in which coordinate.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class CircleFinding {
	public final float averagePixelBlackValue;
	private final int pixelCrossCount;
	private final int pixelCrossValue;
	public final ReferenceCircle referenceCircle;
	public final int x, y;

	/**
	 * Create a circle finding with the given coordinate, the source image data,
	 * the reference circle and the calculated cross value and cross count.
	 * 
	 * @param x
	 *            the x coordinate of the circle finding in the image
	 * @param y
	 *            the y coordinate of the circle finding in the image
	 * @param referenceCircle
	 *            the reference circle of this circle finding
	 * @param sourceImageData
	 *            the image data where the circle finding was found
	 * @param pixelCrossValue
	 *            the calculated cross value of this circle finding
	 * @param pixelCrossCount
	 *            the calculated cross count of this circle finding
	 */
	public CircleFinding(final int x, final int y,
			final ReferenceCircle referenceCircle,
			final int[][] sourceImageData, final int pixelCrossValue,
			final int pixelCrossCount) {

		this.x = x;
		this.y = y;
		this.referenceCircle = referenceCircle;
		this.pixelCrossValue = pixelCrossValue;
		this.pixelCrossCount = pixelCrossCount;

		final float averagePixelValue = getAveragePixelValue(x, y,
				referenceCircle, sourceImageData);
		averagePixelBlackValue = ReferenceCircle.REFERENCE_BLACK_VALUE
				- averagePixelValue;
	}

	/**
	 * Draws the circle finding with the given color in the given image
	 * processor, to show where the circle finding appears in the image.
	 * 
	 * @param processor
	 *            the image processor the circle finding is drawn to
	 * @param color
	 *            the color of the appearance of the circle finding in the image
	 */
	public void drawIntoImageProcessor(final ImageProcessor processor,
			final Color color) {

		final int diameter = referenceCircle.diameter;

		final int width = diameter, height = diameter;
		final int halfDiameter = diameter / 2;
		final int leftBoundOfCircle = x - halfDiameter;
		final int topBoundOfCircle = y - halfDiameter;

		processor.setColor(color);
		processor.drawOval(leftBoundOfCircle, topBoundOfCircle, width, height);
	}

	private float getAveragePixelValue(final int sourceX, final int sourceY,
			final ReferenceCircle referenceCircle, final int[][] sourceImageData) {

		final PerfectCircleCoordinates perfectCircleCoordinates = referenceCircle
				.getPerfectCircleCoordinates();

		final Map<Byte, Byte> innerCircleLineBounds = perfectCircleCoordinates
				.getInnerCircleLineBounds();

		final int diameter = referenceCircle.diameter;
		final int halfDiameter = diameter / 2;

		final int circleTop = -halfDiameter + 1;
		final int circleBottom = +halfDiameter - 1;

		final int width = sourceImageData.length;
		final int height = sourceImageData[0].length;

		final int circleInImageTop = sourceY + circleTop;

		final int circleInImageBottom = sourceY + circleBottom;

		final int imageTop = 0;
		final int imageLeft = 0;
		final int imageRight = width - 1;
		final int imageBottom = height - 1;

		final int intersectTop = Math.max(imageTop, circleInImageTop);
		final int intersectBottom = Math.min(imageBottom, circleInImageBottom);

		double sumPixelValue = 0;
		int pixelCount = 0;

		for (int imageY = intersectTop, circleY = circleTop; imageY <= intersectBottom; imageY++, circleY++) {

			final Byte rightFirstEdgePointForY;
			rightFirstEdgePointForY = innerCircleLineBounds.get(new Byte(
					(byte) circleY));

			final Byte leftLineBound = (byte) -(rightFirstEdgePointForY - 1);
			final Byte rightLineBound = (byte) (rightFirstEdgePointForY - 1);

			final int circleLeftLineBoundInImage = sourceX + leftLineBound;
			final int circleRightLineBoundInImage = sourceX + rightLineBound;

			final int intersectLeft = Math.max(imageLeft,
					circleLeftLineBoundInImage);
			final int intersectRight = Math.min(imageRight,
					circleRightLineBoundInImage);

			for (int imageX = intersectLeft; imageX <= intersectRight; imageX++) {
				final int imageDataPixelValue = sourceImageData[imageX][imageY];

				pixelCount++;
				sumPixelValue += imageDataPixelValue;
			}
		}

		final int averageInnerCirclePixelValue = (int) (sumPixelValue / pixelCount);

		return averageInnerCirclePixelValue;
	}

	@Override
	public String toString() {

		final int referenceCrossValue = referenceCircle.referenceCrossValue;
		final int referenceCrossCount = referenceCircle.referenceCrossCount;

		final double pixelCrossValuePercentage = (double) pixelCrossValue
				/ referenceCrossValue;
		final double pixelCrossCountPercentage = (double) pixelCrossCount
				/ referenceCrossCount;

		return String
				.format("CircleFinding [x=%d, y=%d, averagePixelValue=%f, diameter=%d, pixelCrossValue=%d from %d (%f), pixelCrossCount=%d from %d (%f)]",
						x, y, averagePixelBlackValue, referenceCircle.diameter,
						pixelCrossValue, referenceCrossValue,
						pixelCrossValuePercentage, pixelCrossCount,
						referenceCrossCount, pixelCrossCountPercentage);
	}
}
