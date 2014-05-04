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

public class ContrastEnhancerTest {

	static final File fileDir1 = new File(
			"SelectedGoogleMaps\\TestContrast\\test3.jpg");
	static final File fileDirAllContrastImgs = new File(
			"SelectedGoogleMaps\\TestContrast");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

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

	private void testContrastEnhancer(final float lowerBoundPercentage,
			final float upperBoundPercentage, final int[][] intArray)
			throws IOException {
		final ContrastEnhancer contrastEnhancer = new ContrastEnhancer(
				lowerBoundPercentage, upperBoundPercentage, false);

		final int[][] copyOfIntArray = copy2DArray(intArray);
		contrastEnhancer.enhanceContrast(copyOfIntArray);

		final int width = copyOfIntArray.length;
		final int height = copyOfIntArray[0].length;

		final ColorProcessor colorProcessor = new ColorProcessor(width, height);
		colorProcessor.setIntArray(copyOfIntArray);

		final String title = String
				.format("testContrastEnhancer lowerBoundPercentage: %f ,  upperBoundPercentage: %f",
						lowerBoundPercentage, upperBoundPercentage);

		final ImagePlus imagePlus = new ImagePlus(title, colorProcessor);
		imagePlus.show();

	}

	@Test
	public void testContrastEnhancer1() throws IOException {

		final BufferedImage image = ImageIO.read(ContrastEnhancerTest.fileDir1);
		final ImagePlus imagePlus = new ImagePlus(
				"Before ContrastEnhancerTest", image);
		imagePlus.show();
		final ImageProcessor processor = imagePlus.getProcessor();

		final int width = processor.getWidth();
		final int height = processor.getHeight();

		final ImageProcessor duplicate = processor.duplicate();
		final ShortProcessor shortProcessor = duplicate
				.convertToShortProcessor();

		final EdgeDetectorParameter edgeDetectorParameters = new EdgeDetectorParameter(
				3, 100, false);
		final EdgeDetector edgeDetector = new EdgeDetector(
				edgeDetectorParameters);
		final int[][] edgesData = edgeDetector.getEdgesData(shortProcessor);
		final ShortProcessor edgesDataProcessor = new ShortProcessor(width,
				height);
		edgesDataProcessor.setIntArray(edgesData);
		new ImagePlus("Edges without Contrastenhancement", edgesDataProcessor)
				.show();

		final int[][] intArray = processor.getIntArray();

		final float maxBoundPercentage = 1.0f;

		for (float iOffset = 0.1f; iOffset < 1f; iOffset += 0.1f) {

			for (float iPosition = iOffset; iPosition < 1f; iPosition += 0.1) {

				final float lowerBoundPercentage = iPosition - iOffset;
				final float upperBoundPercentage = maxBoundPercentage
						- iPosition;

				testContrastEnhancerEdges(lowerBoundPercentage,
						upperBoundPercentage, intArray);
			}

			for (float iPosition = maxBoundPercentage; iPosition > iOffset; iPosition -= 0.1) {

				final float lowerBoundPercentage = iPosition - iOffset;
				final float upperBoundPercentage = maxBoundPercentage
						- iPosition;

				// testContrastEnhancer(lowerBoundPercentage,
				// upperBoundPercentage, intArray);
			}

		}

		System.in.read();
	}

	private void testContrastEnhancerEdges(final float lowerBoundPercentage,
			final float upperBoundPercentage, final int[][] intArray)
			throws IOException {
		final ContrastEnhancer contrastEnhancer = new ContrastEnhancer(
				lowerBoundPercentage, upperBoundPercentage, false);

		final int[][] copyOfIntArray = copy2DArray(intArray);
		contrastEnhancer.enhanceContrast(copyOfIntArray);

		final int width = copyOfIntArray.length;
		final int height = copyOfIntArray[0].length;

		final ColorProcessor colorProcessor = new ColorProcessor(width, height);
		colorProcessor.setIntArray(copyOfIntArray);

		final ShortProcessor shortProcessor = colorProcessor
				.convertToShortProcessor();

		final EdgeDetectorParameter edgeDetectorParameters = new EdgeDetectorParameter(
				3, 100, false);
		final EdgeDetector edgeDetector = new EdgeDetector(
				edgeDetectorParameters);
		final int[][] edgesData = edgeDetector.getEdgesData(shortProcessor);
		final ShortProcessor edgesDataProcessor = new ShortProcessor(width,
				height);
		edgesDataProcessor.setIntArray(edgesData);

		final String title = String
				.format("Edges with contrast lowerBoundPercentage: %f ,  upperBoundPercentage: %f",
						lowerBoundPercentage, upperBoundPercentage);
		final ImagePlus edgesDataOutputImage = new ImagePlus(title,
				edgesDataProcessor);
		edgesDataOutputImage.show();
	}

}
