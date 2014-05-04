package craj.test;

import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import craj.CircleCalculationResultTable;
import craj.CircleCrossCalculator;
import craj.CircleFinding;
import craj.ContrastEnhancer;
import craj.EdgeDetector;
import craj.EdgeDetector.EdgeDetectorParameter;
import craj.ReferenceCircle;
import craj.ReferenceCircle.ReferenceCircleParameters;

public class CircleFindingGetAveragePixelValueTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	private final File image1Circle;
	private final File image1HalfCircle;
	private final List<ReferenceCircle> referenceCirclesList = new ArrayList<ReferenceCircle>();
	private final File testFolder;

	public CircleFindingGetAveragePixelValueTest() {
		testFolder = new File(new File("SelectedGoogleMaps"),
				"TestGetAveragePixelValue");
		image1Circle = new File(testFolder, "1Circle.png");
		image1HalfCircle = new File(testFolder, "1HalfCircle.png");

		for (int iDiameter = 3; iDiameter <= 55; iDiameter += 2) {
			final ReferenceCircleParameters referenceCircleParameters = new ReferenceCircleParameters(
					iDiameter, 0.5f, 0.9f, 0.0f);
			final ReferenceCircle referenceCircle = new ReferenceCircle(
					referenceCircleParameters, false);
			referenceCirclesList.add(referenceCircle);
		}
	}

	@Test
	public void test() throws IOException {
		testGetAveragePixelValueForFile(image1Circle);

		System.in.read();
	}

	public void testGetAveragePixelValueForFile(final File imageFile)
			throws IOException {

		final BufferedImage image = ImageIO.read(imageFile);

		System.out.println("Begin CircleFinderThread: " + image.getRGB(50, 50));

		final ImagePlus imagePlus = new ImagePlus("Image To find Circles In",
				image);

		imagePlus.show();

		final ImageProcessor processor = imagePlus.getProcessor();

		testGetAveragePixelValueForIP(processor);

	}

	public void testGetAveragePixelValueForIP(final ImageProcessor ip) {
		final int width = ip.getWidth();
		final int height = ip.getHeight();
		final int[][] imageData = ip.getIntArray();

		final ContrastEnhancer contrastEnhancer = new ContrastEnhancer(0.2f,
				0.7f, false);
		// contrastEnhancer.enhanceContrast(imageData);

		final ColorProcessor colorProcessor = new ColorProcessor(width, height);
		colorProcessor.setIntArray(imageData);

		new ImagePlus("After ContrastEnhancer", colorProcessor).show();

		final ShortProcessor shortProcessor = colorProcessor
				.convertToShortProcessor(true);

		final EdgeDetectorParameter edgeDetectorParameters = new EdgeDetectorParameter(
				3, 50, false);

		final EdgeDetector edgeDetector = new EdgeDetector(
				edgeDetectorParameters);

		final int[][] edgesData = edgeDetector.getEdgesData(shortProcessor);

		for (final ReferenceCircle referenceCircle : referenceCirclesList) {

			final CircleCrossCalculator circleCrossCalculator = new CircleCrossCalculator(
					referenceCircle, edgesData,
					referenceCircle.getPerfectCircleCoordinates());

			final CircleCalculationResultTable resultTable;
			resultTable = circleCrossCalculator.getResultTable();

			final ImagePlus outputImage = resultTable
					.getCalculationOutputImage();
			outputImage.show();

			final EdgeDetectorParameter edgeDetectorParametersForBlackValueCalculation = new EdgeDetectorParameter(
					3, 0, false);
			final EdgeDetector edgeDetectorForBlackValueCalculation = new EdgeDetector(
					edgeDetectorParametersForBlackValueCalculation);
			final int[][] blackValueEdgesData = edgeDetectorForBlackValueCalculation
					.getEdgesData(shortProcessor);

			final List<CircleFinding> circleCoordinates = resultTable
					.getCircleFindings(blackValueEdgesData);

			for (final CircleFinding circleFinding : circleCoordinates) {
				System.out.println(circleFinding);
			}

		}

	}
}
