package presidio.input.sdk.impl.services;


import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.sdk.impl.repositories.DlpFileDataRepository;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PresidioInputPersistencyServiceConfig.class)
@EnableMongoRepositories(basePackageClasses = DlpFileDataRepository.class)
@Import(MongodbTestConfig.class)
public class PresidioInputPersistencyServiceMongoImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Test
    public void contextLoads() throws Exception {

        Assert.assertNotNull(presidioInputPersistencyService);

    }


    @Test
    public void storeOneEventToMongoAndReadEventFromMongo() {
        List<AbstractAuditableDocument> list = new ArrayList<>();
        AbstractAuditableDocument doc = new DlpFileDataDocument(("2017-06-06 10:10:10,executing_application,hostname," +
                "first_name,dddd,last_name,username,malware_scan_result,event_id,source_ip,false,false,destination_path," +
                "destination_file_name,2.23,source_path,source_file_name,source_drive_type,destination_drive_type," +
                "event_type").split(","));
        list.add(doc);
        presidioInputPersistencyService.store(DataSource.DLPFILE, list);
        List<DlpFileDataDocument> all = mongoTemplate.findAll(DlpFileDataDocument.class);
        Assert.assertEquals(doc, all.get(0));
    }

    @Test
    public void deleteAllEventsFromMongoCollectionDlpFile() {
        List<AbstractAuditableDocument> list = new ArrayList<>();
        AbstractAuditableDocument doc = new DlpFileDataDocument(("2017-06-06 10:10:10,executing_application,hostname," +
                "first_name,dddd,last_name,username,malware_scan_result,event_id,source_ip,true,true,destination_path," +
                "destination_file_name,2.23,source_path,source_file_name,source_drive_type,destination_drive_type," +
                "event_type").split(","));
        list.add(doc);
        presidioInputPersistencyService.store(DataSource.DLPFILE, list);
        int numberOfEventsDeleted = presidioInputPersistencyService.clean(DataSource.DLPFILE, 0, 0);
        List<DlpFileDataDocument> all = mongoTemplate.findAll(DlpFileDataDocument.class);
        Assert.assertEquals(1, numberOfEventsDeleted);
        Assert.assertEquals(0, all.size());
    }
}
