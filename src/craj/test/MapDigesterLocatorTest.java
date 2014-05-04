package craj.test;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import craj.CircleFinding;
import craj.ContrastEnhancer;
import craj.EdgeDetector;
import craj.EdgeDetector.EdgeDetectorParameter;
import craj.ReferenceCircle;
import craj.ReferenceCircle.ReferenceCircleParameters;
import craj.mapDigesterLocator.FileCircleFindingsPair;
import craj.mapDigesterLocator.GenericObservable;
import craj.mapDigesterLocator.GenericObserver;
import craj.mapDigesterLocator.MapDigesterCircleFinder.MapDigesterCircleFinderParameter;
import craj.mapDigesterLocator.MapDigesterLocator.MapDigesterLocatorParameter;
import craj.mapDigesterLocator.MapDigesterLocatorForFiles;

public class MapDigesterLocatorTest implements
		GenericObserver<FileCircleFindingsPair> {

	private final File imgDir = new File("SelectedGoogleMaps/Test2");

	@Test
	public void testMapDigesterLocatorImagesFromDiscTest3()
			throws InterruptedException, IOException {

		final float valueDifferenceTolerance = 0.66f;
		final float countDifferenceTolerance = 0.81f;
		final boolean verboseReferenceCircle = false;
		final float minBlackValue = 0.92f;

		final List<ReferenceCircle> referenceCircles = Arrays.asList(
				new ReferenceCircle(new ReferenceCircleParameters(35,

				valueDifferenceTolerance, countDifferenceTolerance,
						minBlackValue), verboseReferenceCircle),
				new ReferenceCircle(new ReferenceCircleParameters(37,

				valueDifferenceTolerance, countDifferenceTolerance,
						minBlackValue), verboseReferenceCircle),
				new ReferenceCircle(new ReferenceCircleParameters(39,

				valueDifferenceTolerance, countDifferenceTolerance,
						minBlackValue), verboseReferenceCircle),
				new ReferenceCircle(new ReferenceCircleParameters(41,

				valueDifferenceTolerance, countDifferenceTolerance,
						minBlackValue), verboseReferenceCircle),
				new ReferenceCircle(new ReferenceCircleParameters(43,

				valueDifferenceTolerance, countDifferenceTolerance,
						minBlackValue), verboseReferenceCircle),
				new ReferenceCircle(new ReferenceCircleParameters(45,

				valueDifferenceTolerance, countDifferenceTolerance,
						minBlackValue), verboseReferenceCircle),
				new ReferenceCircle(new ReferenceCircleParameters(47,
						valueDifferenceTolerance, countDifferenceTolerance,
						minBlackValue), verboseReferenceCircle));

		final EdgeDetectorParameter edgeDetectorParametersForCrossValueCalculation = new EdgeDetectorParameter(
				5, 100, false);
		final EdgeDetector edgeDetectorForCrossValueCalculation = new EdgeDetector(
				edgeDetectorParametersForCrossValueCalculation);

		final EdgeDetectorParameter edgeDetectorParametersForBlackValueCalculation = new EdgeDetectorParameter(
				3, 0, false);
		final EdgeDetector edgeDetectorForBlackValueCalculation = new EdgeDetector(
				edgeDetectorParametersForBlackValueCalculation);

		final List<ContrastEnhancer> contrastEnhancerList = Arrays.asList(

		new ContrastEnhancer(0.5f, 0.25f, false)

		, new ContrastEnhancer(0.25f, 0.5f, false)

		, new ContrastEnhancer(0.1f, 0.8f, false)

		, new ContrastEnhancer(0.9f, 0.06f, false)

		);

		final MapDigesterCircleFinderParameter mapDigesterCircleFinderParameters;

		mapDigesterCircleFinderParameters = new MapDigesterCircleFinderParameter(
				edgeDetectorForBlackValueCalculation,
				edgeDetectorForCrossValueCalculation, contrastEnhancerList,
				false, referenceCircles);

		final MapDigesterLocatorParameter params = new MapDigesterLocatorParameter(
				true, true, true, imgDir, mapDigesterCircleFinderParameters);

		final MapDigesterLocatorForFiles mapDigesterLocator = new MapDigesterLocatorForFiles(
				params);

		mapDigesterLocator.addObserver(this);
		mapDigesterLocator.start();
		mapDigesterLocator.join();

		System.in.read();
	}

	@Override
	public void update(
			final GenericObservable<FileCircleFindingsPair> oberservable,
			final FileCircleFindingsPair circleFindingsPair) {

		final File fileInWhichCirclesWhereFound = circleFindingsPair.file;
		final List<CircleFinding> circleFindings = circleFindingsPair.circleFindings;

		final String foundSomethingNotification = String.format(
				"Found something in %s:", fileInWhichCirclesWhereFound);
		System.out.println(foundSomethingNotification);

		final String absolutePath = fileInWhichCirclesWhereFound
				.getAbsolutePath();
		final ImagePlus outputImage = new ImagePlus(absolutePath);
		final ImageProcessor outputImageProcessor = outputImage.getProcessor();
		outputImageProcessor.setColor(Color.PINK);

		for (int i = 0; i < circleFindings.size(); i++) {
			final CircleFinding circleFinding = circleFindings.get(i);

			final int x = circleFinding.x;
			final int y = circleFinding.y;

			final int diameter = circleFinding.referenceCircle.diameter;
			final int halfDiameter = diameter / 2;

			final String circleFindingOutput = String.format("%d: %s", i,
					circleFinding);
			final String circleFindingNumber = String.format("%d", i);

			outputImageProcessor.drawOval(x - halfDiameter, y - halfDiameter,
					diameter, diameter);
			outputImageProcessor.drawString(circleFindingNumber, x, y);

			System.out.println(circleFindingOutput);
		}

		outputImage.show();

		System.out.println();

	}

}
