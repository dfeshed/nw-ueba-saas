package presidio.ade.domain.record.enriched;

/**
 * Created by YaronDL on 6/15/2017.
 */

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.enriched.dlpfile.AdeScoredDlpFileRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.ade.domain.record.util.AdeEnrichedRecordToAdeScoredEnrichedRecordResolver;

import java.time.Instant;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdeEnrichedRecordToAdeScoredEnrichedRecordResolverConfig.class)
public class AdeEnrichedRecordToAdeScoredEnrichedRecordClassResolverConfigTest {
    @Autowired
    private AdeEnrichedRecordToAdeScoredEnrichedRecordResolver adeEnrichedRecordToAdeScoredEnrichedRecordResolver;

    @Test
    public void testDlpfileIsResolved(){
        Class<? extends AdeScoredEnrichedRecord> record = adeEnrichedRecordToAdeScoredEnrichedRecordResolver.getClass(EnrichedDlpFileRecord.class);
        Assert.assertTrue(record.equals(AdeScoredDlpFileRecord.class));
    }
}
