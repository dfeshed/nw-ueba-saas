package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.file.FileEvent;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class NetwitnessFileEventBuilder extends NetwitnessEvent {
    private final FileEvent event;

    NetwitnessFileEventBuilder(FileEvent event) {
        event_time = eventTimeFormatter.format(event.getDateTime());
        schema = Schema.FILE;
        eventTimeEpoch = event.getDateTime();
        cefVendor = "Microsoft";
        cefProduct = "Windows Snare";
        this.event = event;
    }


    NetwitnessFileEventBuilder getWin_4670() {
        setCommonFields();
        reference_id = "4670";
        obj_name = event.getFileOperation().getSourceFile().getAbsoluteFilePath(); // (only when obj.type = FILE)
        obj_type = "File";

        return this;
    }

    NetwitnessFileEventBuilder getWin_4663() {
        setCommonFields();
        reference_id = "4663";
        obj_name = event.getFileOperation().getSourceFile().getAbsoluteFilePath(); // (only when obj.type = FILE)
        obj_type = "File";
        category = "File System";
        accesses = accessesFieldValueByOperationType.getOrDefault(getOperationType(), "WriteData (or AddFile)");
        return this;
    }

    NetwitnessFileEventBuilder getWin_4660() {
        setCommonFields();
        reference_id = "4660";
        category = "File System";
        return this;
    }

    NetwitnessFileEventBuilder getWin_5145() {
        setCommonFields();
        reference_id = "5145";
        filename = event.getFileOperation().getSourceFile().getAbsoluteFilePath();
        ip_src = event.getMachineEntity().getMachineIp();
        return this;
    }

    private void setCommonFields(){
        event_source_id = event.getEventId();
        user_dst = event.getUser().getUserId();  // machine account ($ at the end)
        device_type = "winevent_snare";
        cefEventDesc = getOperationType();
        cefEventType = event.getFileOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";
        result_code = event.getFileOperation().getOperationResultCode();
        device_ip = event.getMachineEntity().getMachineIp();
        accesses = accessesFieldValueByOperationType.getOrDefault(getOperationType(), null);

    }

    private Map<String, String> accessesFieldValueByOperationType = Stream.of(new String[][] {
        { "FILE_OPENED", "ReadData (or ListDirectory)" },
        { "FOLDER_OPENED", "ReadData (or ListDirectory)" },
        { "FILE_CREATED", "WriteData (or AddFile)" },
        { "FOLDER_CREATED", "WriteData (or AddFile)" },
        { "FILE_MODIFIED", "AppendData (or AddSubdirectory or CreatePipeInstance)" },
        { "FOLDER_MODIFIED", "AppendData (or AddSubdirectory or CreatePipeInstance)" },
        { "FILE_WRITE_DAC_CHANGED", "WRITE_DAC" },
        { "FOLDER_WRITE_DAC_CHANGED", "WRITE_DAC" },
        { "FILE_WRITE_DAC_PERMISSION_CHANGED", "WRITE_DAC" },
        { "FILE_WRITE_OWNERSHIP_CHANGED", "WRITE_OWNER" },
        { "FOLDER_WRITE_OWNERSHIP_CHANGED", "WRITE_OWNER" },
        { "FILE_WRITE_OWNER_PERMISSION_CHANGED", "WRITE_OWNER" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    String getOperationType() {return  event.getFileOperation().getOperationType().getName().toUpperCase().replaceAll(" ","_");}

}
