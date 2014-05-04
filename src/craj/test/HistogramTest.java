package craj.test;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import craj.Histogram;

public class HistogramTest {

	private final int[][] intArray;

	public HistogramTest() throws IOException {
		super();
		final File fileDir = new File(
				"SelectedGoogleMaps\\TestHistogram\\test.jpg");

		final BufferedImage image = ImageIO.read(fileDir);

		final ImagePlus imagePlus = new ImagePlus("MinMaxTest", image);
		final ImageProcessor imageProcessor = imagePlus.getProcessor();

		intArray = imageProcessor.getIntArray();

	}

	@Test
	public void test() throws IOException {

		final Histogram histogram = new Histogram(intArray);
		final int[] pixelValueSums = histogram.getPixelValueSums();
	}

	@Test
	public void testMinEqualsMax() throws IOException {

		final Histogram histogram = new Histogram(intArray);
		final int min = histogram.getMin(0.5f);
		final int max = histogram.getMax(0.5f);

		Assert.assertEquals(min, max);
	}

	@Test
	public void testMinMax() throws IOException {

		final Histogram histogram = new Histogram(intArray);
		final int min = histogram.getMin();
		final int max = histogram.getMax();

		Assert.assertEquals(22, min);
		Assert.assertEquals(255, max);
	}

}
