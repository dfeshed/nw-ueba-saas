package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters;

import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.NetwitnessEvent;


class NetwitnessFileEventConverter {

    private static StringCyclicValuesGenerator counter = new StringCyclicValuesGenerator(new String[]{"4663", "5145"});

    public NetwitnessEvent getNext(FileEvent event) {

        NetwitnessFileEventBuilder builder = new NetwitnessFileEventBuilder(event);

        switch (builder.getOperationType()) {
            case "FILE_OPENED":
            case "FOLDER_OPENED":
                if ("5145".equals(counter.getNext())) return builder.getWin_5145();
                else return builder.getWin_4663(); // default

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
