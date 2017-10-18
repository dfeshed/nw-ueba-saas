package fortscale.ml.model.retriever.smart_data;

import java.util.Collections;
import java.util.List;

/**
 * Created by barak_schuster on 30/08/2017.
 */
public class SmartWeightsModelBuilderData {
    private long distinctDays;
    private int numOfContexts;
    private List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers;

    public SmartWeightsModelBuilderData() {
        numOfContexts = 0;
        smartAggregatedRecordDataContainers = Collections.emptyList();
        distinctDays = 0;
    }

    public SmartWeightsModelBuilderData(int numOfContexts, List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers, long distinctDays) {
        this.numOfContexts = numOfContexts;
        this.smartAggregatedRecordDataContainers = smartAggregatedRecordDataContainers;
        this.distinctDays = distinctDays;
    }

    public int getNumOfContexts() {
        return numOfContexts;
    }

    public List<SmartAggregatedRecordDataContainer> getSmartAggregatedRecordDataContainers() {
        return smartAggregatedRecordDataContainers;
    }
}
