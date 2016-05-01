package fortscale.ml.model.builder;

import org.junit.Test;

public class SMARTScoreMappingModelBuilderConfTest {
	@Test(expected = IllegalArgumentException.class)
	public void should() {
		new SMARTScoreMappingModelBuilderConf(10, 5);
	}
}
