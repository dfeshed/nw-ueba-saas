package presidio.ade.sdk.data_generator;

import fortscale.utils.time.SystemDateService;
import uk.co.jemos.podam.api.PodamFactory;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by barak_schuster on 5/28/17.
 */
public class MockedEnrichedRecordGenerator {
    private final PodamFactory dataFactory;
    private final SystemDateService systemDateService;

    public MockedEnrichedRecordGenerator(PodamFactory dataFactory, SystemDateService systemDateService) {
        this.dataFactory = dataFactory;
        this.systemDateService = systemDateService;
    }

    public List<MockedEnrichedRecord> generate(EnrichedRecordsMetadata metaData) {
        Instant startInstant = metaData.getStartInstant();
        Instant endInstant = metaData.getEndInstant();
        List<MockedEnrichedRecord> inputRecords = new LinkedList<>();
        while (systemDateService.getInstant().isBefore(endInstant)) {
            MockedEnrichedRecord adeInputRecord = dataFactory.manufacturePojo(MockedEnrichedRecord.class);
            inputRecords.add(adeInputRecord);
            systemDateService.forceDurationAdvance(Duration.of(10, ChronoUnit.SECONDS));
        }
        return inputRecords;
    }
}
