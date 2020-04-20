package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.PartitionedContinuousDataModel;

import java.util.*;


public class PartitionedContinuousHistogramModelBuilder extends ContinuousHistogramModelBuilder {

    public Model build(Collection<Double> values, int numOfMaxValuesSamples, long numOfPartitions) {
        ContinuousDataModel model = (ContinuousDataModel) build(values, numOfMaxValuesSamples);
        return new PartitionedContinuousDataModel().setParameters(model.getN(), model.getMean(), model.getSd(), model.getMaxValue(), numOfPartitions);
    }


}
