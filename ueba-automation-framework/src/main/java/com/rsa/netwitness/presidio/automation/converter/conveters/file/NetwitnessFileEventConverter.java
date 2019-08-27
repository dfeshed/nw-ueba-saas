package com.rsa.netwitness.presidio.automation.converter.conveters.file;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.random.RandomListElementGenerator;

import java.util.List;


public class NetwitnessFileEventConverter implements EventConverter<FileEvent> {

    private static final List<String> refIds = Lists.newArrayList("4663", "5145");

    @Override
    public NetwitnessEvent convert(FileEvent event) {
        RandomListElementGenerator<String> referenceIdsGen = new RandomListElementGenerator<>(refIds);

        NetwitnessFileEventBuilder builder = new NetwitnessFileEventBuilder(event);

        switch (builder.getOperationType()) {
            case "FILE_OPENED":
            case "FOLDER_OPENED":
                if ("5145".equals(referenceIdsGen.getNext())) {
                    return builder.getWin_5145();
                }

            case "FILE_DELETED":
            case "FOLDER_DELETED":
                return builder.getWin_4660();

            case "FOLDER_ACCESS_RIGHTS_CHANGED":
            case "FOLDER_CLASSIFICATION_CHANGED":
            case "FILE_OWNERSHIP_CHANGED":
                return builder.getWin_4670();

            default:
                return builder.getWin_4663();
        }
    }


}
