package fortscale.ml.model.retriever.smart_data;

import java.util.Collections;
import java.util.List;

/**
 * Created by barak_schuster on 30/08/2017.
 */
public class SmartWeightsModelBuilderData {
    private int numOfContexts;
    private List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers;

    public SmartWeightsModelBuilderData() {
        numOfContexts = 0;
        smartAggregatedRecordDataContainers = Collections.emptyList();
    }

    public SmartWeightsModelBuilderData(int numOfContexts, List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers) {
        this.numOfContexts = numOfContexts;
        this.smartAggregatedRecordDataContainers = smartAggregatedRecordDataContainers;
    }

    public int getNumOfContexts() {
        return numOfContexts;
    }

    public List<SmartAggregatedRecordDataContainer> getSmartAggregatedRecordDataContainers() {
        return smartAggregatedRecordDataContainers;
    }
}
