package presidio.ade.sdk.executions.data.generator;

import fortscale.utils.time.SystemDateService;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;
import presidio.ade.sdk.executions.online.ADEMockedInputRecord;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by barak_schuster on 5/28/17.
 */
public class ADEMockedInputRecordGenerator {

    private final PodamFactory dataFactory;
    private final SystemDateService systemDateService;

    public ADEMockedInputRecordGenerator(PodamFactory dataFactory, SystemDateService systemDateService) {
        this.dataFactory = dataFactory;
        this.systemDateService = systemDateService;
    }

    public List<ADEMockedInputRecord> generate(ADEInputRecordsMetaData metaData) {

        Instant startInstant = metaData.getStartInstant();
        Instant endInstant = metaData.getEndInstant();
        List<ADEMockedInputRecord> inputRecords = new LinkedList<>();
        while (systemDateService.getInstant().isBefore(endInstant)) {
            ADEMockedInputRecord adeInputRecord = dataFactory.manufacturePojo(ADEMockedInputRecord.class);
            inputRecords.add(adeInputRecord);
            systemDateService.forceDurationAdvance(Duration.of(10, ChronoUnit.SECONDS));
        }
        return inputRecords;
    }
}
