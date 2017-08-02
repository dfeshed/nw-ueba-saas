package presidio.ade.domain.record.scored.enriched_scored;

/**
 * Created by YaronDL on 6/15/2017.
 */

import fortscale.common.general.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeScoredEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeScoredEnrichedRecordClassResolverConfig;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.dlpfile.AdeScoredDlpFileRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdeEventTypeToAdeScoredEnrichedRecordClassResolverConfig.class)
public class DataSourceToAdeScoredEnrichedRecordClassResolverConfigTest {
    @Autowired
    private AdeEventTypeToAdeScoredEnrichedRecordClassResolver dataSourceToAdeScoredEnrichedRecordClassResolver;

    @Test
    public void testDlpfileIsResolved(){
        Class<? extends AdeScoredEnrichedRecord> record = dataSourceToAdeScoredEnrichedRecordClassResolver.getClass(Schema.DLPFILE.toString().toLowerCase());
        Assert.assertTrue(record.equals(AdeScoredDlpFileRecord.class));
    }

    @Test
    public void testDlpmailIsNotResolved(){
        Class<? extends AdeScoredEnrichedRecord> record = dataSourceToAdeScoredEnrichedRecordClassResolver.getClass(Schema.DLPMAIL.toString().toLowerCase());
        Assert.assertTrue(record == null);
    }

    @Test
    public void testNonExistingDataSourceCase(){
        Class<? extends AdeScoredEnrichedRecord> record = dataSourceToAdeScoredEnrichedRecordClassResolver.getClass("NonExistingDataSource");
        Assert.assertTrue(record == null);
    }
}
