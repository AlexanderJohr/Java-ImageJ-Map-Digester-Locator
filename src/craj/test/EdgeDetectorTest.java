package craj.test;

import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import craj.ContrastEnhancer;
import craj.EdgeDetector;
import craj.EdgeDetector.EdgeDetectorParameter;

public class EdgeDetectorTest {

	static final File fileDirAllContrastImgs = new File(
			"SelectedGoogleMaps\\TestContrast");
	static File[] files;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		EdgeDetectorTest.files = EdgeDetectorTest.fileDirAllContrastImgs
				.listFiles();
	}

	private int[][] copy2DArray(final int[][] arrayToCopy) {

		final int width = arrayToCopy.length;
		final int height = arrayToCopy[0].length;

		final int[][] arrayToCopyTo = new int[width][height];

		for (int x = 0; x < width; x++) {
			System.arraycopy(arrayToCopy[x], 0, arrayToCopyTo[x], 0, height);
		}

		return arrayToCopyTo;
	}

	@Test
	public void testContrastEnhancerAllImages() throws IOException {

		for (final File file : EdgeDetectorTest.files) {
			if (file.isFile() && file.canRead()) {
				testEdgeDetectorForFile(file);
			}
		}
		System.in.read();
	}

	@Test
	public void testContrastEnhancerFirstImage() throws IOException {

		final File file = EdgeDetectorTest.files[0];
		if (file.isFile() && file.canRead()) {
			testEdgeDetectorForFile(file);
		}

		System.in.read();
	}

	private void testEdgeDetectorForFile(final File file) {
		try {
			final BufferedImage image = ImageIO.read(file);
			final ImagePlus imagePlus = new ImagePlus("Before Edgetection",
					image);
			imagePlus.show();

			final ImageProcessor processor = imagePlus.getProcessor();
			final int[][] intArray = processor.getIntArray();

			final int width = intArray.length;
			final int height = intArray[0].length;

			final float lowerBoundPercentage = 0.1f;
			final float upperBoundPercentage = 0.3f;

			final ContrastEnhancer contrastEnhancer = new ContrastEnhancer(
					lowerBoundPercentage, upperBoundPercentage, true);

			final int[][] copyOfIntArray = copy2DArray(intArray);
			contrastEnhancer.enhanceContrast(copyOfIntArray);

			final ColorProcessor colorProcessor = new ColorProcessor(width,
					height);
			colorProcessor.setIntArray(copyOfIntArray);
			final ShortProcessor shortProcessor = colorProcessor
					.convertToShortProcessor();

			testImageWithVariousEdgeDetectionParameters(shortProcessor);

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void testImageWithVariousEdgeDetectionParameters(
			final ImageProcessor imageProcessor) {
		for (int iSobelMatrixSize = 3; iSobelMatrixSize <= 7; iSobelMatrixSize += 2) {

			for (int iDetectionTolerance = 20; iDetectionTolerance <= 20; iDetectionTolerance += 20) {

				final EdgeDetectorParameter edgeDetectorParameters = new EdgeDetectorParameter(
						iSobelMatrixSize, iDetectionTolerance, true);

				final String title = String.format(
						"SobelMatrix: %d  DetectionTolerance %d",
						iSobelMatrixSize, iDetectionTolerance);

				final EdgeDetector edgeDetector = new EdgeDetector(
						edgeDetectorParameters);
				final int[][] edgesData = edgeDetector
						.getEdgesData(imageProcessor);
				final int width = imageProcessor.getWidth();
				final int height = imageProcessor.getHeight();

				final ShortProcessor shortProcessor = new ShortProcessor(width,
						height);
				shortProcessor.setIntArray(edgesData);

				final ImagePlus imagePlus = new ImagePlus(title, shortProcessor);
				imagePlus.show();

			}

		}

	}

}
