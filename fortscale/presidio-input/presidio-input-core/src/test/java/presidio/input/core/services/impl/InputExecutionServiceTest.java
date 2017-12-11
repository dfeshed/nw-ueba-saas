package presidio.input.core.services.impl;

import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.input.core.FortscaleInputCoreApplicationTest;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.spring.InputCoreConfigurationTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FortscaleInputCoreApplicationTest.springConfig.class, InputCoreConfigurationTest.class})
public class InputExecutionServiceTest {

    @Autowired
    PresidioExecutionService executionService;

    @Autowired
    AdeDataService adeDataService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    EnrichedDataAdeToCollectionNameTranslator translator;

    @Test
    public void testCleanup() throws Exception {
        Instant startTime = Instant.now().truncatedTo(ChronoUnit.HOURS);
        Instant endTime = Instant.now().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);

        List<EnrichedRecord> records = new ArrayList<>();
        records.add(new EnrichedAuthenticationRecord(Instant.now()));
        records.add(new EnrichedAuthenticationRecord(Instant.now().minus(1, ChronoUnit.HOURS)));

        adeDataService.store(Schema.AUTHENTICATION, startTime, endTime, records);

        executionService.cleanup(Schema.AUTHENTICATION, startTime, endTime, 1d);
        List<EnrichedRecord> all = mongoTemplate.findAll(EnrichedRecord.class, translator.toCollectionName(Schema.AUTHENTICATION.toString().toLowerCase()));
        // TODO: uncomment when the ade cleanup is implemented
        //Assert.assertEquals(1, all.size());
    }
}
