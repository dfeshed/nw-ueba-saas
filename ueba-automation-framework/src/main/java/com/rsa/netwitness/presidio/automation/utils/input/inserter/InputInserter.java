package com.rsa.netwitness.presidio.automation.utils.input.inserter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.data.domain.event.Event;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.io.FileWriter;
import java.util.Comparator;
import java.util.List;


public abstract class InputInserter {
    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    public InputInserter() {}

    /**
     * Calls store method to insert generated events into Mongo DB
     * @param evList    - list of generated events
     */
    public void insert(List<? extends Event> evList){

        Comparator<Event> comparing = Comparator.comparing(Event::getDateTime, Comparator.naturalOrder());
        evList.sort(comparing);

        List<? extends AbstractAuditableDocument> storeRecords = convert(evList);

        store(getDataSource(),storeRecords);
    }

    /***
     * Writes events into CSV file
     */
    public void store(String dataSource, List<? extends AbstractAuditableDocument> evList) {
        //saveJsonStringToFile(evList);
        presidioInputPersistencyService.store(Schema.valueOf(dataSource), evList);
    }


    public void saveJsonStringToFile(List<? extends AbstractAuditableDocument> evList)  {
        try{

            Gson gson = new GsonBuilder().setDateFormat("MM/dd/YYY HH:mm:ss a").setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String json = gson.toJson(evList);

            FileWriter writer = new FileWriter("/home/presidio/some_input_folder/" + "/debugEnrichedFile.json");

            writer.write(json);
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    protected abstract List<? extends AbstractAuditableDocument> convert(List<? extends Event> evList);
    protected abstract String getDataSource();
    protected abstract Class<? extends Event> getEventClass();
}
