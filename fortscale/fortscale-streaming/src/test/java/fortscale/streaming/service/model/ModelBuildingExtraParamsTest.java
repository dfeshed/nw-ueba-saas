package fortscale.streaming.service.model;

import org.junit.Test;

import java.util.Collections;

public class ModelBuildingExtraParamsTest {
	@Test
	public void shouldSucceedInstantiating() {
		new ModelBuildingExtraParams(
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap()
		);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldFailGivenNullAsManagerParams() {
		new ModelBuildingExtraParams(
				null,
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap()
		);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldFailGivenNullAsSelectorParams() {
		new ModelBuildingExtraParams(
				Collections.emptyMap(),
				null,
				Collections.emptyMap(),
				Collections.emptyMap()
		);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldFailGivenNullAsRetrieverParams() {
		new ModelBuildingExtraParams(
				Collections.emptyMap(),
				Collections.emptyMap(),
				null,
				Collections.emptyMap()
		);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldFailGivenNullAsBuilderParams() {
		new ModelBuildingExtraParams(
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				null
		);
	}
}
