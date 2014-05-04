package craj.imagejplugin;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import craj.CircleFinding;
import craj.ContrastEnhancer;
import craj.EdgeDetector;
import craj.EdgeDetector.EdgeDetectorParameter;
import craj.ReferenceCircle;
import craj.ReferenceCircle.ReferenceCircleParameters;
import craj.mapDigesterLocator.MapDigesterCircleFinder;
import craj.mapDigesterLocator.MapDigesterCircleFinder.MapDigesterCircleFinderParameter;

public class MapDigesterLocatorPlugin implements PlugInFilter {

	@Override
	public void run(final ImageProcessor ip) {

		final float valueDifferenceTolerance = 0.60f;
		final float countDifferenceTolerance = 0.70f;
		final boolean verboseReferenceCircle = false;
		final float minBlackValue = 0.80f;

		final List<ReferenceCircle> referenceCircles = Arrays.asList(





				new ReferenceCircle(new ReferenceCircleParameters(27,
						valueDifferenceTolerance, countDifferenceTolerance,
								minBlackValue), verboseReferenceCircle),

				new ReferenceCircle(new ReferenceCircleParameters(29,
						valueDifferenceTolerance, countDifferenceTolerance,
								minBlackValue), verboseReferenceCircle),

				new ReferenceCircle(new ReferenceCircleParameters(31,
						valueDifferenceTolerance, countDifferenceTolerance,
								minBlackValue), verboseReferenceCircle),

				new ReferenceCircle(new ReferenceCircleParameters(33,
						valueDifferenceTolerance, countDifferenceTolerance,
								minBlackValue), verboseReferenceCircle),

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
						minBlackValue), verboseReferenceCircle),

						new ReferenceCircle(new ReferenceCircleParameters(49,
								valueDifferenceTolerance, countDifferenceTolerance,
								minBlackValue), verboseReferenceCircle),

								new ReferenceCircle(new ReferenceCircleParameters(51,
										valueDifferenceTolerance, countDifferenceTolerance,
										minBlackValue), verboseReferenceCircle),

										new ReferenceCircle(new ReferenceCircleParameters(53,
												valueDifferenceTolerance, countDifferenceTolerance,
												minBlackValue), verboseReferenceCircle),

												new ReferenceCircle(new ReferenceCircleParameters(55,
														valueDifferenceTolerance, countDifferenceTolerance,
														minBlackValue), verboseReferenceCircle),

														new ReferenceCircle(new ReferenceCircleParameters(57,
																valueDifferenceTolerance, countDifferenceTolerance,
																minBlackValue), verboseReferenceCircle),

																new ReferenceCircle(new ReferenceCircleParameters(59,
																		valueDifferenceTolerance, countDifferenceTolerance,
																		minBlackValue), verboseReferenceCircle),

																		new ReferenceCircle(new ReferenceCircleParameters(61,
																				valueDifferenceTolerance, countDifferenceTolerance,
																				minBlackValue), verboseReferenceCircle),

																		new ReferenceCircle(new ReferenceCircleParameters(63,
																				valueDifferenceTolerance, countDifferenceTolerance,
																				minBlackValue), verboseReferenceCircle),

																				new ReferenceCircle(new ReferenceCircleParameters(65,
																						valueDifferenceTolerance, countDifferenceTolerance,
																						minBlackValue), verboseReferenceCircle),

																						new ReferenceCircle(new ReferenceCircleParameters(67,
																								valueDifferenceTolerance, countDifferenceTolerance,
																								minBlackValue), verboseReferenceCircle),

																								new ReferenceCircle(new ReferenceCircleParameters(69,
																										valueDifferenceTolerance, countDifferenceTolerance,
																										minBlackValue), verboseReferenceCircle),

																										new ReferenceCircle(new ReferenceCircleParameters(71,
																												valueDifferenceTolerance, countDifferenceTolerance,
																												minBlackValue), verboseReferenceCircle)



				)
						;

		final EdgeDetectorParameter edgeDetectorParametersForCrossValueCalculation = new EdgeDetectorParameter(
				5, 100, true);
		final EdgeDetector edgeDetectorForCrossValueCalculation = new EdgeDetector(
				edgeDetectorParametersForCrossValueCalculation);

		final EdgeDetectorParameter edgeDetectorParametersForBlackValueCalculation = new EdgeDetectorParameter(
				3, 0, true);
		final EdgeDetector edgeDetectorForBlackValueCalculation = new EdgeDetector(
				edgeDetectorParametersForBlackValueCalculation);

		final List<ContrastEnhancer> contrastEnhancerList = Arrays.asList(

		//new ContrastEnhancer(0.5f, 0.25f, true)

		//, new ContrastEnhancer(0.25f, 0.5f, true)

		//, new ContrastEnhancer(0.1f, 0.8f, true)

		//,
		new ContrastEnhancer(0.9f, 0.06f, true)

		);

		final MapDigesterCircleFinderParameter mapDigesterCircleFinderParameters;

		mapDigesterCircleFinderParameters = new MapDigesterCircleFinderParameter(
				edgeDetectorForBlackValueCalculation,
				edgeDetectorForCrossValueCalculation, contrastEnhancerList,
				true, referenceCircles);

		final MapDigesterCircleFinder mapDigesterCircleFinder = new MapDigesterCircleFinder(
				mapDigesterCircleFinderParameters);
		final List<CircleFinding> circleFindings = mapDigesterCircleFinder
				.getCircleCoordinates(ip);

		ip.setColor(Color.GREEN);

		for (int i = 0; i < circleFindings.size(); i++) {
			final CircleFinding circleFinding = circleFindings.get(i);

			final int x = circleFinding.x;
			final int y = circleFinding.y;

			final int diameter = circleFinding.referenceCircle.diameter;
			final int halfDiameter = diameter / 2;

			ip.drawOval(x - halfDiameter, y - halfDiameter, diameter, diameter);

		}

		new ImagePlus("Circle Fingings", ip).show();
	}

	@Override
	public int setup(final String arg, final ImagePlus imp) {
		return PlugInFilter.DOES_ALL;
	}

}