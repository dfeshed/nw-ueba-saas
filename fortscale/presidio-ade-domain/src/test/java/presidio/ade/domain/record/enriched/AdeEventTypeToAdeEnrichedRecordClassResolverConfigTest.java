package presidio.ade.domain.record.enriched;

import fortscale.common.general.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;

/**
 * Created by YaronDL on 6/15/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdeEventTypeToAdeEnrichedRecordClassResolverConfig.class)
public class AdeEventTypeToAdeEnrichedRecordClassResolverConfigTest {

    @Autowired
    private AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver;

    @Test
    public void testDlpfileIsResolved(){
        Class<? extends EnrichedRecord> enrichedRecord = adeEventTypeToAdeEnrichedRecordClassResolver.getClass(Schema.DLPFILE.getName().toLowerCase());
        Assert.assertTrue(enrichedRecord.equals(EnrichedDlpFileRecord.class));
    }

    @Test
    public void testDlpmailIsNotResolved(){
        Class<? extends EnrichedRecord> enrichedRecord = adeEventTypeToAdeEnrichedRecordClassResolver.getClass("dlpmail");
        Assert.assertTrue(enrichedRecord == null);
    }

    @Test
    public void testNonExistingAdeEventTypeCase(){
        Class<? extends EnrichedRecord> enrichedRecord = adeEventTypeToAdeEnrichedRecordClassResolver.getClass("NonExistingAdeEventType");
        Assert.assertTrue(enrichedRecord == null);
    }
}
