package presidio.ade.sdk.executions.online;

import presidio.ade.domain.store.input.ADEInputRecord;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by barak_schuster on 5/28/17.
 */
public class ADEMockedInputRecordGenerator {

    public static List<ADEMockedInputRecord> generate (ADEInputRecordsMetaData metaData)
    {
        PodamFactory dataFactory=new PodamFactoryImpl();
        Instant startInstant = metaData.getStartInstant();
        Instant endInstant = metaData.getEndInstant();
        List<ADEMockedInputRecord> inputRecords = new LinkedList<>();
        while (startInstant.isBefore(endInstant))
        {
            ADEMockedInputRecord adeInputRecord = dataFactory.manufacturePojo(ADEMockedInputRecord.class);
            adeInputRecord.setEventTime(startInstant);
            inputRecords.add(adeInputRecord);
            startInstant=startInstant.plusSeconds(1);
        }
        return inputRecords;
    }
}
