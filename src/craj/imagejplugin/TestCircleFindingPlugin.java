package craj.imagejplugin;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import craj.test.CircleFindingGetAveragePixelValueTest;

public class TestCircleFindingPlugin implements PlugInFilter {

	@Override
	public void run(final ImageProcessor ip) {
		final CircleFindingGetAveragePixelValueTest circleFindingGetAveragePixelValueTest = new CircleFindingGetAveragePixelValueTest();
		circleFindingGetAveragePixelValueTest.testGetAveragePixelValueForIP(ip);

	}

	@Override
	public int setup(final String arg, final ImagePlus imp) {
		return PlugInFilter.DOES_ALL;
	}

}
