package craj;

import ij.ImagePlus;
import ij.process.ShortProcessor;

import java.awt.Color;

/**
 * Represents the perfect circle which holds the maximum cross value and cross
 * count a circle with the same diameter could possibly have.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class ReferenceCircle {

	/**
	 * This class holds the parameter data for the <tt>ReferenceCircle</tt>
	 */
	public static class ReferenceCircleParameters {
		private final float blackValueDifferenceTolerance;
		private final float countDifferenceTolerance;
		private final int diameter;
		private final float valueDifferenceTolerance;

		/**
		 * Construct a ReferenceCircleParameters parameter object.
		 * 
		 * @param diameter
		 *            the diameter of the to be created reference circle
		 * @param valueDifferenceTolerance
		 *            the tolerance of the cross value in which a circle with
		 *            the same diameter should be considered a circle
		 * @param countDifferenceTolerance
		 *            the tolerance of the cross count in which a circle with
		 *            the same diameter should be considered a circle
		 * @param blackValueDifferenceTolerance
		 *            the tolerance of the black value in which a circle with
		 *            the same diameter should be considered a circle
		 */
		public ReferenceCircleParameters(final int diameter,
				final float valueDifferenceTolerance,
				final float countDifferenceTolerance,
				final float blackValueDifferenceTolerance) {

			this.diameter = diameter;
			this.valueDifferenceTolerance = valueDifferenceTolerance;
			this.countDifferenceTolerance = countDifferenceTolerance;
			this.blackValueDifferenceTolerance = blackValueDifferenceTolerance;
		}
	}

	public static final int REFERENCE_BLACK_VALUE = 255;

	/**
	 * the diameter of the to be created reference circle
	 */
	public final int diameter;

	/**
	 * the minimum black value in which a circle with the same diameter should
	 * be considered a circle
	 */
	public final int minBlackValue;

	/**
	 * the minimum cross count in which a circle with the same diameter should
	 * be considered a circle
	 */
	public final int minCrossCount;

	/**
	 * the minimum cross value in which a circle with the same diameter should
	 * be considered a circle
	 */
	public final int minCrossValue;

	private final PerfectCircleCoordinates perfectCircleCoordinates;

	/**
	 * The cross count that was calculated for this perfect reference circle
	 */
	public final int referenceCrossCount;
	/**
	 * The cross value that was calculated for this perfect reference circle
	 */
	public final int referenceCrossValue;

	/**
	 * Construct a <tt>ReferenceCircle</tt> object with the given parameters
	 * 
	 * @param parameters
	 *            the parameters to create the <tt>ReferenceCircle</tt> object
	 * @param verbose
	 *            a boolean which indicates if the methods of this class should
	 *            show it's output
	 * 
	 * @see ReferenceCircleParameters
	 */
	public ReferenceCircle(final ReferenceCircleParameters parameters,
			final boolean verbose) {

		if (checkDiameters(parameters.diameter)) {
			diameter = parameters.diameter;
		} else {
			throw new IllegalArgumentException("Only odd diameters are allowed");
		}

		final ShortProcessor perfectCircleEdges = drawPerfectCircleEdges(diameter);

		perfectCircleCoordinates = new PerfectCircleCoordinates(
				perfectCircleEdges, verbose);

		if (verbose) {
			final ImagePlus neuesFenster = new ImagePlus(
					"Perfect Circle Edges " + diameter,
					perfectCircleEdges.convertToColorProcessor());
			neuesFenster.show();
		}

		final CircleCrossCalculator crossCalculator = new CircleCrossCalculator(
				this, perfectCircleEdges.getIntArray(),
				perfectCircleCoordinates);

		final CircleCalculationResultTable resultTable = crossCalculator
				.getResultTable();

		referenceCrossValue = resultTable.getMaxCrossValue();
		referenceCrossCount = resultTable.getMaxCrossCount();

		final float valueDifferenceTolerance = parameters.valueDifferenceTolerance;
		final float countDifferenceTolerance = parameters.countDifferenceTolerance;
		final float blackValueDifferenceTolerance = parameters.blackValueDifferenceTolerance;

		minCrossValue = (int) Math.ceil(referenceCrossValue
				* valueDifferenceTolerance);
		minCrossCount = (int) Math.ceil(referenceCrossCount
				* countDifferenceTolerance);

		minBlackValue = (int) Math.ceil(ReferenceCircle.REFERENCE_BLACK_VALUE
				* blackValueDifferenceTolerance);

		if (verbose) {
			System.out.println("referenceCrossCount with diameter " + diameter
					+ ": " + referenceCrossCount);
		}
	}

	private boolean checkDiameters(final int diameterToCheck) {
		if (diameterToCheck % 2 == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Draws a perfect circle in the origin of a <tt>ShortProcessor</tt> with
	 * the given diameter.
	 * 
	 * @param diameter
	 *            of the circle to be drawn
	 * @return a <tt>ShortProcessor</tt> in which a circle was drawn
	 */
	private ShortProcessor drawPerfectCircleEdges(final int diameter) {
		ShortProcessor circleImage = null;

		circleImage = new ShortProcessor(diameter, diameter);
		circleImage.setColor(Color.WHITE);

		circleImage.drawOval(0, 0, diameter - 1, diameter - 1);

		return circleImage;
	}

	/**
	 * Returns the <tt>PerfectCircleCoordinates</tt> for this reference circle.
	 * 
	 * @return the <tt>PerfectCircleCoordinates</tt> for this reference circle.
	 */
	public PerfectCircleCoordinates getPerfectCircleCoordinates() {
		return perfectCircleCoordinates;
	}

}
