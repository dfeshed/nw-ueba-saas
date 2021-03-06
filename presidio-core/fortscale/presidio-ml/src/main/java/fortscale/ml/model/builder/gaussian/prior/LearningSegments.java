package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.IContinuousDataModel;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
 *
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/display/FSC/Gaussian+model
 */
public class LearningSegments implements Iterable<LearningSegments.Segment> {
	public static class Segment {
		private double center;
		private Segmentor.Segment segment;

		public Segment(Double center, Segmentor.Segment segment) {
			this.center = center;
			this.segment = segment;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof Segment)) {
				return false;
			}
			Segment o = (Segment) obj;
			return new EqualsBuilder().append(o.center, center).append(o.segment, segment).isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(center).append(segment).hashCode();
		}

		public double getCenter() {
			return center;
		}

		public List<IContinuousDataModel> getModels() {
			return segment.models;
		}

		public double getLeftMean() {
			return segment.leftMean;
		}

		public double getRightMean() {
			return segment.rightMean;
		}

		/**
		 * @return ToString you know...
		 */
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	private List<IContinuousDataModel> sortedModels;
	private SegmentCenters segmentCenters;
	private Segmentor segmentor;

	public LearningSegments(List<IContinuousDataModel> models,
							SegmentCenters segmentCenters,
							Segmentor segmentor) {
		Assert.notNull(models, "models can't be null");
		Assert.notNull(segmentCenters, "segmentCenters can't be null");
		Assert.notNull(segmentor, "segmentor can't be null");
		this.segmentCenters = segmentCenters;
		this.segmentor = segmentor;
		sortedModels = models.stream()
				.sorted(Comparator.comparing(IContinuousDataModel::getMean))
				.collect(Collectors.toList());
	}

	@Override
	public Iterator<Segment> iterator() {
		Stream<Double> segmentCentersStream =
				StreamSupport.stream(((Iterable<Double>) () -> segmentCenters.iterate(sortedModels)).spliterator(), false);
		return segmentCentersStream
				.map(segmentCenter -> {
					Assert.isTrue(segmentCenter >= 0, "segment centers can't be negative");
					Segmentor.Segment segment = segmentor.createSegment(sortedModels, segmentCenter);
					if (segment == null) {
						return null;
					}
					return new Segment(segmentCenter, segment);
				})
				.filter(Objects::nonNull)
				.iterator();
	}

	/**
	 * @return ToString you know...
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
