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

import fortscale.common.general.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdeEventTypeToAdeEnrichedRecordClassResolverConfig.class)
public class DataSourceToAdeEnrichedRecordClassResolverConfigTest {

    @Autowired
    private AdeEventTypeToAdeEnrichedRecordClassResolver dataSourceToAdeEnrichedRecordClassResolver;

    @Test
    public void testDlpfileIsResolved(){
        Class<? extends EnrichedRecord> enrichedRecord = dataSourceToAdeEnrichedRecordClassResolver.getClass(DataSource.DLPFILE.toString().toLowerCase());
        Assert.assertTrue(enrichedRecord.equals(EnrichedDlpFileRecord.class));
    }

    @Test
    public void testDlpmailIsNotResolved(){
        Class<? extends EnrichedRecord> enrichedRecord = dataSourceToAdeEnrichedRecordClassResolver.getClass(DataSource.DLPMAIL.toString().toLowerCase());
        Assert.assertTrue(enrichedRecord == null);
    }

    @Test
    public void testNonExistingDataSourceCase(){
        Class<? extends EnrichedRecord> enrichedRecord = dataSourceToAdeEnrichedRecordClassResolver.getClass("NonExistingDataSource");
        Assert.assertTrue(enrichedRecord == null);
    }
}
