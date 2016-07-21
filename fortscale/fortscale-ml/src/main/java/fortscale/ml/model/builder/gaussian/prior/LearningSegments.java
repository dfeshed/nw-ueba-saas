package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * LearningSegments is a collection of segments.
 * Each segment contains an interval of ContinuousDataModel means.
 * Each segment is to be used separately as input for learning a GaussianPriorModel
 * (which is to be used as input for ContinuousDataModels whose mean is contained within the segment).
 *
 * Segments will be created around the supplied segment centers according to the decision of a Segmentor
 * implementation. The Segmentor will decide if a segment should be created, and if so - how wide should it be.
 *
 * Parts of the real line can be unassigned to any segment in the case where the Segmentor decides to not create
 * a segment, or if parts of the real line don't have any segment center nearby.
 *
 * Segments can overlap. In this case, it means that the overlapping area should be used in the learning process
 * of the two segments's GaussianPriorModel. Later on, when a ContinuousDataModel with a mean inside the overlapping
 * area will request a GaussianPriorModel, the one which is associated with the segment with the closest center
 * will be used:
 *
 *    all of the models inside s1
 *     will be used for learning
 *        a GaussianPriorModel
 *            _____+_____
 *          /            \
 *         -------s1-------          when a model with mean x needs
 *                      ____________ some GaussianPriorModel, s1's will
 *                    /              be chosen (because it's closer)
 * ------------------x----------------------------------------------------> (real line of all
 *                                                                           possible model means)
 *                ---------s2--------
 *                 \_______+_______/
 *             all of the models inside s2
 *              will be used for learning
 *                 a GaussianPriorModel
 */
public class LearningSegments implements Iterable<Pair<Double, Double>> {
	private List<Pair<Double, Double>> segments;

	public LearningSegments(List<ContinuousDataModel> models,
							Iterable<Double> segmentCenters,
							Segmentor segmentor) {
		Assert.notNull(models, "models can't be null");
		Assert.notNull(segmentCenters, "segmentCenters can't be null");
		Assert.notNull(segmentor, "segmentor can't be null");
		double[] sortedMeans = models.stream()
				.mapToDouble(ContinuousDataModel::getMean)
				.sorted()
				.toArray();
		segments = StreamSupport.stream(segmentCenters.spliterator(), false)
				.map(segmentCenter -> {
					Assert.isTrue(segmentCenter >= 0, "segment centers can't be negative");
					return segmentor.createSegment(sortedMeans, segmentCenter);
				})
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	public int size() {
		return segments.size();
	}

	public Pair<Double, Double> get(int index) {
		return segments.get(index);
	}

	@Override
	public Iterator<Pair<Double, Double>> iterator() {
		return segments.iterator();
	}
}
