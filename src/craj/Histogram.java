package craj;

/**
 * Creates a histogram for the given image-data
 * 
 * @author Alexander Johr u26865 m18927, Caroline Rühling u26864 m18926
 * 
 */
public class Histogram {
	private final int pixelCount;
	private final int[] pixelValueSums;

	/**
	 * Constructs an Histogram of the given image-data.
	 * 
	 * @param imageData
	 *            an image-data array of pixel-values
	 */
	public Histogram(final int[][] imageData) {
		final int width = imageData.length;
		final int height = imageData[0].length;

		pixelCount = width * height;

		pixelValueSums = countPixelSums(imageData);
	}

	/**
	 * Calculates the sum of red, green and blue pixel-values of all pixels in
	 * the given array.
	 * 
	 * @param imageData
	 *            an array of pixels to calculate the sum of pixel-values
	 * @return the sum of red, green and blue pixel-values of all pixels
	 */
	private int[] countPixelSums(final int[][] imageData) {

		final int width = imageData.length;
		final int height = imageData[0].length;

		final int[] pixelValueSums = new int[256];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final int pixelValue = imageData[x][y];

				final int red = (pixelValue & 0x00ff0000) >> 16;
				final int green = (pixelValue & 0x0000ff00) >> 8;
				final int blue = pixelValue & 0x000000ff;

				final int greyValue = (red + green + blue) / 3;

				pixelValueSums[greyValue]++;
			}
		}

		return pixelValueSums;
	}

	/**
	 * returns the maximum of the image's histogram
	 * 
	 * @return the maximum of the image's histogram
	 */
	public int getMax() {
		return getMax(0.0f);
	}

	/**
	 * returns the maximum of the image's histogram, ignoring a count of values
	 * calculated by the given percentage
	 * 
	 * @param upperBoundPercentage
	 *            determines how many pixel values in percent should be ignored
	 * @return the maximum of the image's histogram
	 */
	public int getMax(final float upperBoundPercentage) {
		final int upperBoundPixelValueSum;
		upperBoundPixelValueSum = Math.round(upperBoundPercentage * pixelCount);

		int addedUpPixelValues = 0;

		final int lastHistogramIndex = pixelValueSums.length;
		int iHistogram = lastHistogramIndex;

		do {
			iHistogram--;

			final int pixelValue = pixelValueSums[iHistogram];
			addedUpPixelValues += pixelValue;

		} while (addedUpPixelValues <= upperBoundPixelValueSum
				&& iHistogram > 0);

		return iHistogram;
	}

	/**
	 * returns the minimum of the image's histogram
	 * 
	 * @return the minimum of the image's histogram
	 */
	public int getMin() {
		return getMin(0.0f);
	}

	/**
	 * returns the minimum of the image's histogram, ignoring a count of values
	 * calculated by the given percentage
	 * 
	 * @param lowerBoundPercentage
	 *            determines how many pixel values in percent should be ignored
	 * @return the minimum of the image's histogram
	 */
	public int getMin(final float lowerBoundPercentage) {
		final int lowerBoundPixelValueSum;
		lowerBoundPixelValueSum = Math.round(lowerBoundPercentage * pixelCount);

		int addedUpPixelValues = 0;
		int iHistogram = -1;

		do {
			iHistogram++;

			final int pixelValue = pixelValueSums[iHistogram];
			addedUpPixelValues += pixelValue;

		} while (addedUpPixelValues <= lowerBoundPixelValueSum);

		return iHistogram;
	}

	public int[] getPixelValueSums() {
		return pixelValueSums;
	}
}
