package com.rsa.netwitness.presidio.automation.utils.ade.inserter;

import com.google.common.collect.Iterables;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.data.domain.event.Event;

import java.util.Comparator;
import java.util.List;

/**
 * Created by YaronDL on 7/10/2017.
 */
public abstract class AdeInserter {
    private AdeManagerSdk adeManagerSDK;

    public AdeInserter(AdeManagerSdk adeManagerSDK){
        this.adeManagerSDK = adeManagerSDK;
    }


    /**
     * Calls store method to insert generated events into mongodb collection
     * @param evList    - list of generated events
     */
    public void insert(List<? extends Event> evList){
        Comparator<Event> comparing = Comparator.comparing(Event::getDateTime, Comparator.naturalOrder());
        evList.sort(comparing);

        List<? extends EnrichedRecord> enrichedRecords = convert(evList);  //converterFactory.getConverter(dataSourceType).convert(evList);

        EnrichedRecordsMetadata enrichedRecordsMetadata = getEnrichedRecordsMetadata(evList);

        // store the data at the enriched_* collections
        adeManagerSDK.storeEnrichedRecords(enrichedRecordsMetadata, enrichedRecords);
    }

    private EnrichedRecordsMetadata getEnrichedRecordsMetadata(List<? extends Event> sortedEvList){
        if (sortedEvList.isEmpty()) return null;
        return new EnrichedRecordsMetadata(getDataSource(), Iterables.getFirst(sortedEvList,null).getDateTime(), sortedEvList.get(sortedEvList.size()-1).getDateTime());
    }

    protected abstract List<? extends EnrichedRecord> convert(List<? extends Event> evList);
    public abstract String getDataSource();
    protected abstract Class<? extends Event> getEventClass();
}
