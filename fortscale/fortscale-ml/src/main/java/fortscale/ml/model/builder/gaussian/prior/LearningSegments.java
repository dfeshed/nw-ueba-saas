package fortscale.ml.model.builder.gaussian.prior;

import org.apache.commons.lang3.tuple.Pair;

/**
 * LearningSegments is a collection of segments.
 * Each segment contains an interval of ContinuousDataModel means.
 * Each segment is used separately as input for learning a GaussianPriorModel
 * (which shall be used as input for ContinuousDataModels whose mean is contained within the segment).
 * An implementing class will implement the strategy of how to segment the real line of all possible means.
 *
 * Parts of the real line can be unassigned to any segment, i.e. - when there's not big enough ContinuousDataModels
 * concentration. Unassigned parts won't have any GaussianPriorModel associated with them
 *
 * Segments can overlap. In this case, it means that the overlapping area should be used in the learning process
 * of the two segments's GaussianPriorModel. Later on, when a ContinuousDataModel with a mean inside the overlapping
 * area will request a GaussianPriorModel, the one which is associated with the segment with the nearest center
 * will be used:
 *
 *    all of the models inside s1
 *     will be used for learning
 *        a GaussianPriorModel
 *            _____+_____
 *          /            \
 *         -------s1-------          when a model with mean x needs
 *                      ____________ some GaussianPriorModel, s1's will
 *                    /              be chosen (because it's nearer)
 * ------------------x----------------------------------------------------> (real line of all
 *                                                                           possible model means)
 *                ---------s2--------
 *                 \_______+_______/
 *             all of the models inside s2
 *              will be used for learning
 *                 a GaussianPriorModel
 */
public interface LearningSegments {
	int size();

	Pair<Double, Double> get(int index);
}
