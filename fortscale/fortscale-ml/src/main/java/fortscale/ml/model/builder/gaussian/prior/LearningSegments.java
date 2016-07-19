package fortscale.ml.model.builder.gaussian.prior;

/**
 * LearningSegments is a collection of segments.
 * Each segment contains an interval of ContinuousDataModel means.
 * Each segment is used separately as input for learning a GaussianPriorModel
 * (which shall be used for ContinuousDataModels whose mean is contained within the segment).
 * An implementing class will implement the strategy of how to segment the real line of all possible means.
 * It could decide that some parts of the real line shouldn't be part of any segment, i.e. - when there's
 * not big enough ContinuousDataModels concentration.
 */
public interface LearningSegments {
	int size();
}
