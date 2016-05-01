package fortscale.ml.model.builder;

import org.junit.Test;

public class SMARTThresholdModelBuilderConfTest {
	@Test(expected = IllegalArgumentException.class)
	public void should() {
		new SMARTThresholdModelBuilderConf(10, 5);
	}
}
