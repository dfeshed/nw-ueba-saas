package com.rsa.netwitness.presidio.automation.common.scenarios.events;

import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileOperationActions;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

public class EnrichmentExtraData {

    /** Enrichment tests events that are missing from Alerts scenarios **/

    public Stream<FileEvent> fileOperationTypes() throws GeneratorException {
        String user = "enrich-eswkjfnsdjv";

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(user);
        SingleUserGenerator userGenerator = new SingleUserGenerator(user);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30),
                        60, 5, 2);

        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MODIFIED, eventIdGen, myTimeGenerator, userGenerator);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_WRITE_OWNER_PERMISSION_CHANGED, eventIdGen, myTimeGenerator, userGenerator));
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_WRITE_DAC_PERMISSION_CHANGED, eventIdGen, myTimeGenerator, userGenerator));
        return events.stream();
    }

}