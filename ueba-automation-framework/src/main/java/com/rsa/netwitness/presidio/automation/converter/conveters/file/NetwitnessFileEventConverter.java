package com.rsa.netwitness.presidio.automation.converter.conveters.file;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.ListValueGenerator;


public class NetwitnessFileEventConverter implements EventConverter<FileEvent> {

    private static ListValueGenerator<String> refIdGen = new ListValueGenerator<>(Lists.list("4663", "5145"));

    @Override
    public NetwitnessEvent convert(FileEvent event) {
        NetwitnessFileEventBuilder builder = new NetwitnessFileEventBuilder(event);
        String operationType = builder.getOperationType();

        if ("FILE_OPENED".equals(operationType) || "FOLDER_OPENED".equals(operationType)) {
            String nextId = refIdGen.getNext();

            if ("5145".equals(nextId)) {
                return builder.getWin_5145();
            }
            if ("4663".equals(nextId)) {
                return builder.getWin_4663();
            }
            throw new RuntimeException("No such ID: " + nextId);

        } else if ("FILE_DELETED".equals(operationType) || "FOLDER_DELETED".equals(operationType)) {
            return builder.getWin_4660();

        } else if ("FOLDER_ACCESS_RIGHTS_CHANGED".equals(operationType) || "FOLDER_CLASSIFICATION_CHANGED".equals(operationType) || "FILE_OWNERSHIP_CHANGED".equals(operationType)) {
            return builder.getWin_4670();
        }

        return builder.getWin_4663();
    }


}
