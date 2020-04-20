package com.rsa.netwitness.presidio.automation.utils.input.inserter.activedirectory;

import fortscale.domain.core.EventResult;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputActiveDirectoryEventsConverter {
    public List<? extends AbstractInputDocument> convert(List<? extends Event> events) {

        HashMap<String, String> resultCode = new HashMap();
        resultCode.put("SUCCESS", "0x0");
        resultCode.put("FAILURE", "0x6");

        List<ActiveDirectoryRawEvent> records = new ArrayList<>();
        for (ActiveDirectoryEvent event : (List<ActiveDirectoryEvent>) events) {
            HashMap<String, String> additionalInfo = new HashMap();
            // prepare Additional Info
            additionalInfo.put("result", event.getOperation().getOperationResult());
            additionalInfo.put("origin", event.getSrcMachineEntity().getMachineId());
            additionalInfo.put("originIPv4", event.getSrcMachineEntity().getMachineIp());
            additionalInfo.put("to", "Dummy");
            additionalInfo.put("description", event.getActiveDirectoryDescription());
            additionalInfo.put("computer", event.getSrcMachineEntity().getMachineId());
            additionalInfo.put("oSVersion", event.getSrcMachineEntity().getOsVersion());
            additionalInfo.put("iPAddress", event.getSrcMachineEntity().getMachineIp());
            additionalInfo.put("domain", event.getSrcMachineEntity().getMachineDomainDN());
            additionalInfo.put("objectDN", event.getObjectDN());
            additionalInfo.put("isUserAdmin", event.getUser().getAdministrator().toString());
            additionalInfo.put("operationType", event.getOperation().getOperationType().getName());

            ActiveDirectoryRawEvent storeRecord = new ActiveDirectoryRawEvent(
                    event.getDateTime(),
                    event.getEventId(),
                    event.getDataSource(),
                    event.getUser().getUserId(),
                    event.getOperation().getOperationType().getName(),
                    event.getOperation().getOperationType().getCategories(),
                    EventResult.getEventResult(convertResultToQuestConvention(event.getOperation().getOperationResult())),
                    event.getUser().getUsername(),
                    event.getUser().getFirstName() + " " + event.getUser().getLastName(),
                    additionalInfo,
                    event.getOperation().getObjectName(),
                    resultCode.get(event.getOperation().getOperationResult()));
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
        else if (result.equalsIgnoreCase("PROTECTED")) {
            return "FAILURE";
        }
        else if (result.equals("NONE")) {
            return "FAILURE";
        }

        return result;
    }
}