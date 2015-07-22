package fortscale.aggregation.feature.extraction;

import net.minidev.json.JSONObject;

public class EventCounterFeatureExtractor {
	public static final String NAME = "eventCounterFeatureExtractor";

	public static Object createFeature() {
		return new Counter();
	}

	public static void updateFeature(Object feature, JSONObject message) {
		Counter counter = (Counter)feature;
		counter.increment();
	}

	private static final class Counter {
		private int counter;

		public Counter() {
			counter = 0;
		}

		public void increment() {
			counter++;
		}
	}
}
