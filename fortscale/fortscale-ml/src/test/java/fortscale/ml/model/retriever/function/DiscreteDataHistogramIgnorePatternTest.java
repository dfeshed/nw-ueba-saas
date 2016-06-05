package fortscale.ml.model.retriever.function;

import fortscale.common.datastructures.GenericHistogram;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DiscreteDataHistogramIgnorePatternTest {
	private static final Date date = new Date();

	@Test(expected = ClassCastException.class)
	public void should_fail_if_given_illegal_input_type() {
		new DiscreteDataHistogramIgnorePattern(null).execute("illegalInputType", date, date);
	}

	@Test
	public void should_not_ignore_value_1() {
		GenericHistogram input = new GenericHistogram();
		input.add("value", 1d);
		IDataRetrieverFunction function = new DiscreteDataHistogramIgnorePattern(null);
		GenericHistogram output = (GenericHistogram)function.execute(input, date, date);
		Assert.assertEquals(1, output.getTotalCount(), 0);
	}

	@Test
	public void should_not_ignore_value_2() {
		GenericHistogram input = new GenericHistogram();
		input.add("value", 10d);
		IDataRetrieverFunction function = new DiscreteDataHistogramIgnorePattern("^.+value$");
		GenericHistogram output = (GenericHistogram)function.execute(input, date, date);
		Assert.assertEquals(10, output.getTotalCount(), 0);
	}

	@Test
	public void should_ignore_empty_string() {
		GenericHistogram input = new GenericHistogram();
		input.add("", 1d);
		IDataRetrieverFunction function = new DiscreteDataHistogramIgnorePattern(null);
		GenericHistogram output = (GenericHistogram)function.execute(input, date, date);
		Assert.assertEquals(0, output.getTotalCount(), 0);
	}

	@Test
	public void should_ignore_blank_string() {
		GenericHistogram input = new GenericHistogram();
		input.add("   ", 10d);
		IDataRetrieverFunction function = new DiscreteDataHistogramIgnorePattern(null);
		GenericHistogram output = (GenericHistogram)function.execute(input, date, date);
		Assert.assertEquals(0, output.getTotalCount(), 0);
	}

	@Test
	public void should_ignore_value_matching_pattern() {
		GenericHistogram input = new GenericHistogram();
		input.add("PC_TEST", 1d);
		IDataRetrieverFunction function = new DiscreteDataHistogramIgnorePattern("^PC_.*");
		GenericHistogram output = (GenericHistogram)function.execute(input, date, date);
		Assert.assertEquals(0, output.getTotalCount(), 0);
	}
}
