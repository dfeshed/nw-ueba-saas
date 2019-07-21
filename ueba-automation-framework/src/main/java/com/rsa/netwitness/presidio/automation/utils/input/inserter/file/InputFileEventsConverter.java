package com.rsa.netwitness.presidio.automation.utils.input.inserter.file;

import fortscale.domain.core.EventResult;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.sdk.api.domain.rawevents.FileRawEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputFileEventsConverter {
    public List<? extends FileRawEvent> convert(List<? extends Event> events) {

        List<FileRawEvent> records = new ArrayList<>();
        HashMap<String, String> resultCode = new HashMap();
        resultCode.put("SUCCESS", "0x0");
        resultCode.put("FAILURE", "0x6");

        for (FileEvent event : (List<FileEvent>) events) {
            HashMap<String, String> additionalInfo = new HashMap();
            // prepare Additional Info
            additionalInfo.put("originIPv4", event.getMachineEntity().getMachineIp());
            additionalInfo.put("description",event.getFileDescription());
            additionalInfo.put("oSVersion",event.getMachineEntity().getOsVersion());
            additionalInfo.put("iPAddress", event.getMachineEntity().getMachineIp());
            additionalInfo.put("domainDN", event.getMachineEntity().getMachineDomainDN());
            additionalInfo.put("fileSystemType", event.getFileSystemEntity().getFileSystemType());
            additionalInfo.put("fileSystemLogonID", event.getFileSystemEntity().getFileSystemLogonID());
            additionalInfo.put("origin",event.getMachineEntity().getMachineId());
            additionalInfo.put("computer",event.getMachineEntity().getMachineId());
            additionalInfo.put("isUserAdmin", event.getUser().getAdministrator().toString());
            additionalInfo.put("event", event.getFileOperation().getOperationType().getName());

            FileRawEvent storeRecord = new FileRawEvent(
                    event.getDateTime(),
                    event.getEventId(),
                    event.getDataSource(),
                    event.getUser().getUserId(),
                    event.getFileOperation().getOperationType().getName(),
                    event.getFileOperation().getOperationType().getCategories(),
                    EventResult.getEventResult(convertResultToQuestConvention(event.getFileOperation().getOperationResult())),
                    event.getUser().getUsername(),
                    event.getUser().getFirstName() + " " + event.getUser().getLastName(),
                    additionalInfo,
                    event.getFileOperation().getSourceFile().getAbsoluteFilePath(),
                    event.getFileOperation().getSourceFile().isDriveShared(),
                    event.getFileOperation().getDestinationFile().getAbsoluteFilePath(),
                    event.getFileOperation().getDestinationFile().isDriveShared(),
                    event.getFileOperation().getSourceFile().getFileSize(),
                    resultCode.get(event.getFileOperation().getOperationResult()));
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