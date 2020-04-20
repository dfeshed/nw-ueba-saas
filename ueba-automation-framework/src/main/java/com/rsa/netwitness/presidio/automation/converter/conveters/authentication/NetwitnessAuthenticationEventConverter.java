package com.rsa.netwitness.presidio.automation.converter.conveters.authentication;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.ListValueGenerator;


public class NetwitnessAuthenticationEventConverter implements EventConverter<AuthenticationEvent> {

    private static ListValueGenerator<String> successReferenceIdsGen = new ListValueGenerator<>(Lists.list("4769", "4624", "4648", "rhlinux"));
    private static ListValueGenerator<String> failureReferenceIdsGen = new ListValueGenerator<>(Lists.list("4769", "4625", "4648", "rhlinux"));


    @Override
    public NetwitnessEvent convert(AuthenticationEvent event) {
        NetwitnessWindowsAuthenticationEventBuilder builderWin = new NetwitnessWindowsAuthenticationEventBuilder(event);
        NetwitnessLinuxAuthenticationEventBuilder builderLin = new NetwitnessLinuxAuthenticationEventBuilder(event);

        if (event.getResult() == null || event.getResult().isEmpty()) throw new RuntimeException("ReferenceId is missing from event");

        String currentId = event.getResult().equalsIgnoreCase("success") ?
                successReferenceIdsGen.getNext() : failureReferenceIdsGen.getNext();

        switch (currentId) {
            case "4769": return builderWin.getWin_4769();
            case "4624": return builderWin.getWin_4624();
            case "4625": return builderWin.getWin_4625();
            case "4648": return builderWin.getWin_4648();
            case "rhlinux": return builderLin.getRhlinux();
            default: throw new RuntimeException("ReferenceId not defined. Found: " + currentId);
        }
    }


}
