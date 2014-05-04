package craj;

/**
 * This class generates a Sobel-matrix with the given size automatically.
 * 
 * @author Alexander Johr u26865 m18927
 * 
 */
public class SobelMatrix {
	private final int sobelMatrixLength;

	private final int[][] xMatrix;

	private final int[][] yMatrix;

	/**
	 * Construct a <tt>SobelMatrix</tt> object for the given length which should
	 * always be greater or equal 3 and odd.
	 * 
	 * @param sobelMatrixLength
	 *            the size of the Sobel-matix which should always be greater or
	 *            equal 3 and odd.
	 */
	public SobelMatrix(final int sobelMatrixLength) {
		if (sobelMatrixLength % 2 == 0) {
			throw new IllegalArgumentException(
					"Only odd numbers are allowed for the matrix length.");
		}

		this.sobelMatrixLength = sobelMatrixLength;
		xMatrix = calculateXSobelMatrix(sobelMatrixLength);
		yMatrix = spinMatrixClockwise(xMatrix);
		System.out.println();
	}

	private int[][] calculateXSobelMatrix(final int sobelMatrixLength) {
		final int[][] matrix = new int[sobelMatrixLength][sobelMatrixLength];

		final int middle = getMiddle();

		for (int y = 0; y < middle; y++) {
			for (int x = 1; x <= middle; x++) {
				matrix[y][middle + x] = -x - y;
				matrix[y][middle - x] = +x + y;

				final int lowerY = sobelMatrixLength - y - 1;
				matrix[lowerY][middle + x] = -x - y;
				matrix[lowerY][middle - x] = +x + y;
			}
		}

		for (int x = 1; x <= middle; x++) {
			matrix[middle][middle + x] = -x - middle;
			matrix[middle][middle - x] = +x + middle;
		}

		return matrix;
	}

	/**
	 * Returns the middle of the Sobel-matrix.
	 * 
	 * @return the middle of the Sobel-matrix.
	 */
	public int getMiddle() {
		return sobelMatrixLength / 2;
	}

	/**
	 * Calculates the Sobel value for the given pixel in the given image data.
	 * 
	 * @param imageData
	 *            the image data where the pixel for the calculation can be
	 *            found
	 * @param x
	 *            the x coordinate of the pixel for the calculation in the
	 *            source image
	 * @param y
	 *            the y coordinate of the pixel for the calculation in the
	 *            source image
	 * @return the calculated Sobel value for the specified pixel using the
	 *         created Sobel-matrix
	 */
	public int getSobelValueForPixel(final int[][] imageData, final int x,
			final int y) {
		final int middle = getMiddle();

		float sumX = 0;
		float sumY = 0;

		for (int xSobel = 0; xSobel < sobelMatrixLength; xSobel++) {
			for (int ySobel = 0; ySobel < sobelMatrixLength; ySobel++) {
				final int sobelValueX = xMatrix[xSobel][ySobel];
				final int sobelValueY = yMatrix[xSobel][ySobel];

				final int destX = x + xSobel - middle;
				final int destY = y + ySobel - middle;

				final int pixelValue = imageData[destX][destY];

				sumX += sobelValueX * pixelValue;
				sumY += sobelValueY * pixelValue;
			}
		}

		final float sumXSquare = sumX * sumX;
		final float sumYSquare = sumY * sumY;

		final int sobelValueForPixel = (int) Math.sqrt(sumXSquare + sumYSquare);

		return sobelValueForPixel;
	}

	private int[][] spinMatrixClockwise(final int[][] matrixToBeSpinned) {
		final int length = matrixToBeSpinned.length;

		final int[][] spinnedMatrix = new int[length][length];

		for (int x = 0; x < length; x++) {
			for (int y = length - 1; y >= 0; y--) {
				spinnedMatrix[x][y] = matrixToBeSpinned[y][x];
			}
		}

		return spinnedMatrix;
	}
}
