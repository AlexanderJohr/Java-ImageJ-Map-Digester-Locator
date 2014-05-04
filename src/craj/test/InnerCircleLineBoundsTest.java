package craj.test;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import craj.PerfectCircleCoordinates;
import craj.PerfectCircleCoordinates.DifferenceFromOrigin;
import craj.ReferenceCircle;
import craj.ReferenceCircle.ReferenceCircleParameters;

public class InnerCircleLineBoundsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private Map<Byte, Byte> getInnerCircleLineBoundsForDiameter(
			final int diameter) {
		final ReferenceCircleParameters parameters = new ReferenceCircleParameters(
				diameter, 0, 0, 0);
		final ReferenceCircle referenceCircle = new ReferenceCircle(parameters,
				true);
		final PerfectCircleCoordinates perfectCircleCoordinates = referenceCircle
				.getPerfectCircleCoordinates();

		final List<DifferenceFromOrigin> edgeDifferencesFromCircleOrigin = perfectCircleCoordinates
				.getEdgeDifferencesFromCircleOrigin();
		final Map<Byte, Byte> innerCircleLineBounds = perfectCircleCoordinates
				.getInnerCircleLineBounds();

		return innerCircleLineBounds;
	}

	@Test
	public void ReferenceCircleDiameter3Test() {
		final String output = getInnerCircleLineBoundsForDiameter(3).toString();
		Assert.assertEquals(output, "{0=1}");
	}

	@Test
	public void ReferenceCircleDiameter5Test() {
		final String output = getInnerCircleLineBoundsForDiameter(5).toString();
		Assert.assertEquals(output, "{0=2, 1=1, -1=1}");
	}

	@Test
	public void ReferenceCircleDiameter7Test() {
		final String output = getInnerCircleLineBoundsForDiameter(7).toString();
		Assert.assertEquals(output, "{0=3, 1=3, 2=2, -2=2, -1=3}");
	}

	@Test
	public void ReferenceCircleDiameter9Test() {
		final String output = getInnerCircleLineBoundsForDiameter(9).toString();
		Assert.assertEquals(output, "{0=4, 1=4, 2=3, 3=2, -3=2, -2=3, -1=4}");
	}

}
