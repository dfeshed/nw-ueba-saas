package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.StringCyclicValuesGenerator;


class NetwitnessAuthenticationEventConverter {

    private static final String[] successReferenceIds = new String[]{"4769", "4624", "4648", "rhlinux"};
    private static final String[] failureReferenceIds = new String[]{"4769", "4625", "4648", "rhlinux"};

    private static StringCyclicValuesGenerator successCounter = new StringCyclicValuesGenerator(successReferenceIds);
    private static StringCyclicValuesGenerator failuresCounter = new StringCyclicValuesGenerator(failureReferenceIds);


    NetwitnessEvent getNext(AuthenticationEvent event) {

        NetwitnessAuthenticationEventBuilder builder = new NetwitnessAuthenticationEventBuilder(event);
        if (event.getResult() == null || event.getResult().isEmpty()) throw new RuntimeException("ReferenceId is missing from event");

        String currentId = event.getResult().equalsIgnoreCase("success") ?
            successCounter.getNext() : failuresCounter.getNext();

        switch (currentId) {
            case "4769": return builder.getWin_4769();
            case "4624": return builder.getWin_4624();
            case "4625": return builder.getWin_4625();
            case "4648": return builder.getWin_4648();
            case "rhlinux": return builder.getRhlinux();
            default: throw new RuntimeException("ReferenceId not defined. Found: " + currentId);
        }
    }


}
