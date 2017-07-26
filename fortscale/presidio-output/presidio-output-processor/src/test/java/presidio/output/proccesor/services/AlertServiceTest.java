package presidio.output.proccesor.services;

import fortscale.domain.SMART.EntityEvent;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartPageIterator;
import presidio.output.domain.services.AlertPersistencyService;
import presidio.output.processor.services.alert.AlertServiceImpl;

import java.time.Instant;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertServiceTest {

    @MockBean
    private AlertPersistencyService alertPersistencyService;

    @MockBean
    private SmartDataStore smartDataStore;

    private AlertServiceImpl alertService = new AlertServiceImpl(alertPersistencyService);

    @Test
    public void alertServiceTest() {

//        List<AbstractAuditableDocument> list = new ArrayList<>();
//        AbstractAuditableDocument doc = new DlpFileDataDocument(("2017-06-06T10:00:00Z,copy,executing_application,hostname," +
//                "first_name,dddd,last_name,username,malware_scan_result,event_id,source_ip,false,false,destination_path," +
//                "2.23,destination_file_name,source_path,source_file_name,source_drive_type,destination_drive_type,").split(","));
//        list.add(doc);
        Instant startTime = Instant.parse("2017-06-06T10:00:00Z");
        Instant endTime = Instant.parse("2017-06-06T11:00:00Z");
        TimeRange timeRange = new TimeRange(startTime, endTime);
        int smartScoreThreshold = 0;
        PageIterator<EntityEvent> smarts = new SmartPageIterator<EntityEvent>(smartDataStore, timeRange, smartScoreThreshold);
        alertService.generateAlerts(smarts);
    }

}
