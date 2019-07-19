package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.mongo;

import com.rsa.netwitness.presidio.automation.utils.adapter.ReferenceIdGeneratorFactory;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.IStringGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventToMetadataConverterFile implements EventToMetadataConverter {
    private static final String[] fileOpenedReferenceIds = new String[]{"4663", "5145"};
    //private static final String[] fileDeletedReferenceIds = new String[]{"4663", "4660"};

    private IStringGenerator fileOpenedReferenceIdGenerator;

    // sourceMachineId, dest...

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> convert(Map<String, String> config, List<? extends Event> events) {
        fileOpenedReferenceIdGenerator = ReferenceIdGeneratorFactory.create(config, fileOpenedReferenceIds);
        List<Map<String, Object>> metadataList = new ArrayList<>(events.size());

        for (FileEvent event : (List<FileEvent>)events) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("event_time", String.valueOf(event.getDateTime().toEpochMilli()));
            metadata.put("mongo_source_event_time", event.getDateTime());
            metadata.put("user_dst", event.getUser().getUserId());
            handleOperationType(event, metadata);
            putCategory(metadata);
            putObjType(metadata);
            putSrcFilePath(event, metadata);
            metadata.put("event_type", (event.getFileOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE"));
            metadata.put("result_code", event.getFileOperation().getOperationResultCode());
            metadata.put("event_source_id", event.getEventId());
            metadata.put("device_type", "winevent_snare");
            metadataList.add(metadata);
        }

        return metadataList;
    }

    private void handleOperationType(FileEvent event, Map<String, Object> metadata) {
        String operationType = event.getFileOperation().getOperationType().getName().toUpperCase().replaceAll(" ","_");
        // noinspection IfCanBeSwitch
        if (operationType.equals("FILE_OPENED") || operationType.equals("FOLDER_OPENED")) {
            metadata.put("reference_id", fileOpenedReferenceIdGenerator.getNext());
            if (metadata.get("reference_id").equals("4663")) metadata.put("accesses", "ReadData (or ListDirectory)");
        } else if (operationType.equals("FILE_DELETED") || operationType.equals("FOLDER_DELETED")) {
            metadata.put("reference_id", "4660");
        } else if (operationType.equals("FOLDER_ACCESS_RIGHTS_CHANGED") || operationType.equals("FOLDER_CLASSIFICATION_CHANGED") || operationType.equals("FILE_OWNERSHIP_CHANGED")) {
            metadata.put("reference_id", "4670");
        } else if (operationType.equals("FILE_CREATED") || operationType.equals("FOLDER_CREATED")) {
            metadata.put("reference_id", "4663");
            metadata.put("accesses", "WriteData (or AddFile)");
        } else if (operationType.equals("FILE_MODIFIED") || operationType.equals("FOLDER_MODIFIED")) {
            metadata.put("reference_id", "4663");
            metadata.put("accesses", "AppendData (or AddSubdirectory or CreatePipeInstance)");
        } else if (operationType.equals("FILE_WRITE_DAC_CHANGED") || operationType.equals("FOLDER_WRITE_DAC_CHANGED")) {
            metadata.put("reference_id", "4663");
            metadata.put("accesses", "WRITE_DAC");
        } else if (operationType.equals("FILE_WRITE_OWNERSHIP_CHANGED") || operationType.equals("FOLDER_WRITE_OWNERSHIP_CHANGED")) {
            metadata.put("reference_id", "4663");
            metadata.put("accesses", "WRITE_OWNER");
        } else { // All operation types from scenario that not covered in NW will be converted as following:
            metadata.put("reference_id", "4663");
            metadata.put("accesses", "WriteData (or AddFile)");
        }
    }

    private static void putObjType(Map<String, Object> metadata) {
        Object referenceId = metadata.get("reference_id");
        if (referenceId == null) return;
        if (referenceId.equals("4670")) metadata.put("obj_type", "File");
    }

    private static void putCategory(Map<String, Object> metadata) {
        Object referenceId = metadata.get("reference_id");
        if (referenceId == null) return;
        if (referenceId.equals("4663") || referenceId.equals("4660")) metadata.put("category", "File System");
    }

    private static void putSrcFilePath(FileEvent event, Map<String, Object> metadata) {
        Object referenceId = metadata.get("reference_id");
        if (referenceId == null) return;
        String srcFilePath = event.getFileOperation().getSourceFile().getAbsoluteFilePath();
        if (referenceId.equals("4663")) metadata.put("obj_name", srcFilePath);
        else if (referenceId.equals("4670")) metadata.put("obj_name", srcFilePath);
        else if (referenceId.equals("5145")) metadata.put("filename", srcFilePath);
    }
}
