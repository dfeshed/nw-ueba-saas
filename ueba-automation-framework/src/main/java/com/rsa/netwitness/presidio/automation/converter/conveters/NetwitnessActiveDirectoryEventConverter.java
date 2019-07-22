package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import static com.rsa.netwitness.presidio.automation.converter.conveters.mongo.EventToMetadataConverterActiveDirectory.chooseReferenceId;


class NetwitnessActiveDirectoryEventConverter {

    private static final String[] referenceIds = new String[]{"4741", "4742", "4733", "4734", "4740", "4794",
            "5376", "5377", "5136", "4764", "4670", "4743", "4739", "4727", "4728", "4754", "4756", "4757", "4758",
            "4720", "4722", "4723", "4724", "4725", "4726", "4738", "4767", "4717", "4729", "4730", "4731", "4732"};

    private static StringCyclicValuesGenerator counter = new StringCyclicValuesGenerator(referenceIds);

    NetwitnessEvent getNext(ActiveDirectoryEvent event) {
        NetwitnessActiveDirectoryEventBuilder builder = new NetwitnessActiveDirectoryEventBuilder(event);
        String refId = chooseReferenceId(event, counter.getNext());
        return builder.getByRefId(refId);
    }



}
