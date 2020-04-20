package com.rsa.netwitness.presidio.automation.utils.input.inserter;



import presidio.data.domain.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputInserterFactory {
    public Map<Class<? extends Event>, InputInserter> eventTypeToInputInserter = new HashMap<>();

    public InputInserterFactory(List<InputInserter> inserterList){
        for(InputInserter inserter: inserterList){
            eventTypeToInputInserter.put(inserter.getEventClass(), inserter);
        }
    }

    public InputInserter getInputInserter(Class<? extends Event> eventClass){
        return eventTypeToInputInserter.get(eventClass);
    }
}
