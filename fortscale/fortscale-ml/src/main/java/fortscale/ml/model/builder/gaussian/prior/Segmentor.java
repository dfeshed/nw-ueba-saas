package fortscale.ml.model.builder.gaussian.prior;

import org.apache.commons.lang3.tuple.Pair;

public interface Segmentor {
	Pair<Double, Double> createSegment(double[] sortedMeans, double segmentCenter);
}
