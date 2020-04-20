package com.rsa.netwitness.presidio.automation.utils.input.inserter.Authentication;

import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.EventResult;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputAuthenticationEventsConverter {
    public List<? extends AbstractAuditableDocument> convert(List<? extends Event> events) {

        List<String> operationTypeCategories = new ArrayList<>();
        List<AbstractAuditableDocument> records = new ArrayList<>();
        HashMap<String, String> resultCode = new HashMap();
        resultCode.put("SUCCESS", "0x0");
        resultCode.put("FAILURE", "0x6");

        for (AuthenticationEvent event : (List<AuthenticationEvent>) events) {
            HashMap<String, String> additionalInfo = new HashMap();
            // prepare Additional Info
            additionalInfo.put("result", event.getResult());
            additionalInfo.put("originIPv4",event.getSrcMachineEntity().getMachineIp());
            additionalInfo.put("description",event.getAuthenticationDescription());
            additionalInfo.put("oSVersion", event.getSrcMachineEntity().getOsVersion());
            additionalInfo.put("iPAddress",event.getSrcMachineEntity().getMachineIp());
            additionalInfo.put("srcDomainFQDN",event.getSrcMachineEntity().getDomainFQDN());
            additionalInfo.put("domainDN", event.getSrcMachineEntity().getMachineDomainDN());
            additionalInfo.put("isUserAdmin", event.getUser().getAdministrator().toString());
            additionalInfo.put("operationType", event.getAuthenticationOperation().getOperationType().getName());

            AuthenticationRawEvent storeRecord = new AuthenticationRawEvent(
                    event.getDateTime(),
                    event.getEventId(),
                    event.getDataSource(),
                    event.getUser().getUserId(),
                    event.getAuthenticationOperation().getOperationType().getName(),
                    operationTypeCategories, //event.getOperationTypeCategories(),
                    EventResult.getEventResult(convertResultToQuestConvention(event.getResult().toUpperCase())),
                    event.getUser().getUsername(),
                    event.getUser().getFirstName() + " " + event.getUser().getLastName(),
                    additionalInfo,
                    event.getSrcMachineEntity().getMachineId(),
                    event.getSrcMachineEntity().getMachineId(), //event.getSrcMachineEntity().getMachineIp(),
                    event.getDstMachineEntity().getMachineId(),
                    event.getDstMachineEntity().getMachineId(), //event.getDstMachineEntity().getMachineIp(),
                    event.getDstMachineEntity().getMachineDomain(),
                    resultCode.get(event.getResult()),
                    event.getSite(),
                    event.getLocation().getCountry(),
                    event.getLocation().getCity()
            );
            records.add(storeRecord);
        }
        return records;
    }

    private String convertResultToQuestConvention(String result) {
        if (result.equals(OPERATION_RESULT.SUCCESS.value)){
            return "SUCCESS";
        } else if (result.equals(OPERATION_RESULT.FAILURE.value)) {
            return "FAILURE";
        }
        else if (result.equals("PROTECTED")) {
            return "FAILURE";
        }
        else if (result.equals("NONE")) {
            return "FAILURE";
        }

        return result;
    }
}