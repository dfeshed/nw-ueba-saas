package fortscale.ml.model.retriever.smart_data;

import fortscale.common.util.GenericHistogram;

import java.time.Instant;

/**
 * Created by barak_schuster on 11/6/17.
 */
public class SmartValueData {
    private GenericHistogram genericHistogram;
    private Instant weightsModelEndTime;

    public SmartValueData(GenericHistogram genericHistogram, Instant weightsModelEndTime) {
        this.genericHistogram = genericHistogram;
        this.weightsModelEndTime = weightsModelEndTime;
    }

    public GenericHistogram getGenericHistogram() {
        return genericHistogram;
    }

    public Instant getWeightsModelEndTime() {
        return weightsModelEndTime;
    }
}
