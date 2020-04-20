package com.rsa.netwitness.presidio.automation.utils.input.inserter.process;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ProcessRawEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputProcessEventsConverter {
    public List<? extends AbstractInputDocument> convert(List<? extends Event> events) {

        HashMap<String, String> resultCode = new HashMap();
        resultCode.put("SUCCESS", "0x0");
        resultCode.put("FAILURE", "0x6");

        ArrayList<ProcessRawEvent> records = new ArrayList<>();
        for (ProcessEvent event : (List<ProcessEvent>) events) {
            HashMap<String, String> additionalInfo = new HashMap();
            // prepare Additional Info

            ProcessRawEvent storeRecord = new ProcessRawEvent(
                    event.getDateTime(),
                    event.getEventId(),
                    event.getDataSource(),
                    event.getUser().getUserId(),
                    event.getProcessOperation().getOperationType().getName(),
                    event.getUser().getUsername(),
                    event.getUser().getFirstName() + " " + event.getUser().getLastName(),
                    additionalInfo,
                    event.getMachineEntity().getMachineId(),
                    event.getMachineEntity().getMachineNameRegexCluster(),
                    "machineOwner",
                    event.getProcessOperation().getSourceProcess().getProcessDirectory(),
                    event.getProcessOperation().getSourceProcess().getProcessFileName(),
                    event.getProcessOperation().getSourceProcess().getProcessDirectoryGroups(),
                    event.getProcessOperation().getOperationType().getCategories(), // src process categories - move to other field in generator?
                    event.getProcessOperation().getSourceProcess().getProcessCertificateIssuer(),
                    event.getProcessOperation().getDestinationProcess().getProcessDirectory(),
                    event.getProcessOperation().getDestinationProcess().getProcessFileName(),
                    event.getProcessOperation().getDestinationProcess().getProcessDirectoryGroups(),
                    event.getProcessOperation().getOperationType().getCategories(), // dst process categories - move to other field in generator?
                    event.getProcessOperation().getDestinationProcess().getProcessCertificateIssuer());
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