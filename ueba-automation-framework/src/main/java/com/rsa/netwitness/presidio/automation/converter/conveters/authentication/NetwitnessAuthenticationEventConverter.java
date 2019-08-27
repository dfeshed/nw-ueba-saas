package com.rsa.netwitness.presidio.automation.converter.conveters.authentication;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.random.RandomListElementGenerator;

import java.util.List;


public class NetwitnessAuthenticationEventConverter implements EventConverter<AuthenticationEvent> {

    private static final List<String> successReferenceIds = Lists.newArrayList("4769", "4624", "4648", "rhlinux");
    private static final List<String> failureReferenceIds = Lists.newArrayList("4769", "4625", "4648", "rhlinux");

    @Override
    public NetwitnessEvent convert(AuthenticationEvent event) {
        RandomListElementGenerator<String> successReferenceIdsGen = new RandomListElementGenerator<>(successReferenceIds);
        RandomListElementGenerator<String> failureReferenceIdsGen = new RandomListElementGenerator<>(failureReferenceIds);

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
