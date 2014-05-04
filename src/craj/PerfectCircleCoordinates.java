package craj;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the pixel data of a perfect circle which is used to draw circles
 * in the resulting cross count and cross value tables and increase the
 * performance because the calculation of the circle pixels in its origin space
 * is already done and does not have to be calculated every time a circle is
 * drawn.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class PerfectCircleCoordinates {

	/**
	 * This class holds the difference of the x and y coordinates of pixel data
	 * regarding its given origin.
	 * 
	 */
	public static class DifferenceFromOrigin {
		final byte x, y;

		/**
		 * Create a DifferenceFromOrigin object with the specified x and y
		 * coordinate.
		 * 
		 * @param x
		 *            the x coordinate
		 * @param y
		 *            the y coordinate
		 */
		public DifferenceFromOrigin(final byte x, final byte y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof DifferenceFromOrigin) {
				final DifferenceFromOrigin other = (DifferenceFromOrigin) obj;

				final boolean xValuesAreEqual = x == other.x;
				final boolean yValuesAreEqual = y == other.y;
				final boolean xAndYValuesAreEqual = xValuesAreEqual
						&& yValuesAreEqual;
				if (xAndYValuesAreEqual) {
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return String.format("X: %d Y: %d", x, y);
		}
	}

	private final List<DifferenceFromOrigin> edgeDifferencesFromCircleOrigin;

	private final Map<Byte, Byte> innerCircleLineBounds;

	/**
	 * Create a PerfectCircleCoordinates object from a <tt>ShortProcessor</tt>
	 * in which the circle was drawn.
	 * 
	 * @param circle
	 *            the <tt>ShortProcessor</tt> in which the circle was drawn.
	 * @param verbose
	 *            a boolean which indicates if the methods of this class should
	 *            show it's output
	 */
	public PerfectCircleCoordinates(final ShortProcessor circle,
			final boolean verbose) {

		final int diameter = circle.getWidth();

		edgeDifferencesFromCircleOrigin = calcEdgesCoordinateDifferencesFromCircleOrigin(
				circle, verbose);

		innerCircleLineBounds = calcInnerCircleLineBounds(
				edgeDifferencesFromCircleOrigin, diameter);
	}

	private List<DifferenceFromOrigin> calcEdgesCoordinateDifferencesFromCircleOrigin(
			final ShortProcessor circleImg, final boolean verbose) {

		final ArrayList<DifferenceFromOrigin> coordinateDifferences = new ArrayList<DifferenceFromOrigin>();

		final int diameter = circleImg.getWidth();
		final int origin = diameter / 2;

		final int left = 0, top = 0;
		final int right = diameter, bottom = diameter;

		for (int x = left; x < right; x++) {
			for (int y = top; y < bottom; y++) {

				if (circleImg.get(x, y) > 0) {

					final byte xDifference = (byte) (x - origin);
					final byte yDifference = (byte) (y - origin);

					coordinateDifferences.add(new DifferenceFromOrigin(
							xDifference, yDifference));
				}
			}
		}

		if (verbose) {
			final ImagePlus outputCoordinateDifferences = new ImagePlus(
					"CoordinateDifferences: " + diameter, new BufferedImage(
							diameter, diameter, BufferedImage.TYPE_BYTE_BINARY));
			final ImageProcessor processor = outputCoordinateDifferences
					.getProcessor();
			for (final DifferenceFromOrigin point : coordinateDifferences) {
				processor.set(point.x + origin, point.y + origin, 1);
			}
			outputCoordinateDifferences.show();
		}

		return coordinateDifferences;
	}

	private Map<Byte, Byte> calcInnerCircleLineBounds(
			final List<DifferenceFromOrigin> edgeDifferencesFromCircleOrigin,
			final int diameter) {

		final Map<Byte, Byte> innerCircleLineBounds = new HashMap<>();

		final int radius = diameter / 2;
		final int top = -radius + 1;
		final int bottom = +radius - 1;

		for (int y = top; y <= bottom; y++) {
			for (int x = 0; x <= radius; x++) {

				final DifferenceFromOrigin differenceFromOrigin = new DifferenceFromOrigin(
						(byte) x, (byte) y);
				final boolean isEdgePoint = edgeDifferencesFromCircleOrigin
						.contains(differenceFromOrigin);
				if (isEdgePoint) {
					innerCircleLineBounds.put((byte) y, (byte) x);
					break;
				}
			}
		}

		return innerCircleLineBounds;
	}

	/**
	 * Returns the edges pixel data around the origin.
	 * 
	 * @return the edges pixel data around the origin
	 */
	public List<DifferenceFromOrigin> getEdgeDifferencesFromCircleOrigin() {
		return edgeDifferencesFromCircleOrigin;
	}

	/**
	 * Returns a map of the y coordinate and the corresponding right bound of
	 * that y coordinate from the origin of the circle.
	 * 
	 * @return a map of the y coordinate and the corresponding right bound of
	 *         that y coordinate
	 */
	public Map<Byte, Byte> getInnerCircleLineBounds() {
		return innerCircleLineBounds;
	}

}
