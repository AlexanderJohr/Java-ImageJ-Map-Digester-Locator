package craj;

import ij.ImagePlus;
import ij.process.ColorProcessor;
import processing.core.PApplet;

/**
 * This class provides methods to enhance the contrast of an image using the
 * specified lower bound percentage and upper bound percentage.
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class ContrastEnhancer {
	private final float lowerBoundPercentage;
	private final float upperBoundPercentage;
	private final boolean verbose;

	/**
	 * Create a <tt>ContrastEnhancer</tt> object with the given lower bound
	 * percentage and upper bound percentage.
	 * 
	 * @param lowerBoundPercentage
	 *            the percentage which indicates how many pixel values are
	 *            ignored to find the lower bound
	 * @param upperBoundPercentage
	 *            the percentage which indicates how many pixel values are
	 *            ignored to find the upper bound
	 * @param verbose
	 *            a boolean which indicates if the methods of this class should
	 *            show it's output
	 */
	public ContrastEnhancer(final float lowerBoundPercentage,
			final float upperBoundPercentage, final boolean verbose) {

		if (lowerBoundPercentage + upperBoundPercentage > 1.0f) {
			final String title = String
					.format("Lower bound and upper bound should not overlap. lowerBoundPercentage was %f ; upperBoundPercentage was %f",
							lowerBoundPercentage, upperBoundPercentage);
			throw new IllegalArgumentException(title);
		}

		this.lowerBoundPercentage = lowerBoundPercentage;
		this.upperBoundPercentage = upperBoundPercentage;
		this.verbose = verbose;
	}

	/**
	 * Enhance the given <code>imageData</code> with the specified lower bound
	 * percentage and upper bound percentage
	 * 
	 * @param imageData
	 *            the image data which contrast should be increased
	 */
	public void enhanceContrast(final int[][] imageData) {

		final Histogram histogram = new Histogram(imageData);

		final int min = histogram.getMin(lowerBoundPercentage);
		int max = histogram.getMax(upperBoundPercentage);

		if (min > max) {
			max = min + 1;
		}

		final int inputStart = min;
		final int inputStop = max;

		final int outputStart = 0;
		final int outputStop = 255;

		final int width = imageData.length;
		final int height = imageData[0].length;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				final int pixelValue = imageData[x][y];

				final int red = (pixelValue & 0x00ff0000) >> 16;
				final int green = (pixelValue & 0x0000ff00) >> 8;
				final int blue = pixelValue & 0x000000ff;

				final int clampedRed = Math.max(inputStart,
						Math.min(inputStop, red));
				final int clampedGreen = Math.max(inputStart,
						Math.min(inputStop, green));
				final int clampedBlue = Math.max(inputStart,
						Math.min(inputStop, blue));

				final int mappedRed = Math.round(PApplet.map(clampedRed,
						inputStart, inputStop, outputStart, outputStop));
				final int mappedGreen = Math.round(PApplet.map(clampedGreen,
						inputStart, inputStop, outputStart, outputStop));
				final int mappedBlue = Math.round(PApplet.map(clampedBlue,
						inputStart, inputStop, outputStart, outputStop));

				final int mappedValue = 0x00000000 | mappedRed << 16
						| mappedGreen << 8 | mappedBlue;

				imageData[x][y] = mappedValue;
			}
		}

		if (verbose) {
			final ColorProcessor colorProcessor = new ColorProcessor(width,
					height);
			colorProcessor.setIntArray(imageData);

			final String outputTitle = String.format(
					"After Contrast Enchancer %f %f", lowerBoundPercentage,
					upperBoundPercentage);
			new ImagePlus(outputTitle, colorProcessor).show();
		}
	}
}
