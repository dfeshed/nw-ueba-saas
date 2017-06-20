package presidio.ade.domain.record.scored.enriched_scored;

/**
 * Created by YaronDL on 6/15/2017.
 */

import fortscale.common.general.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DataSourceToAdeScoredEnrichedRecordClassResolverConfig.class)
public class DataSourceToAdeScoredEnrichedRecordClassResolverConfigTest {
    @Autowired
    private DataSourceToAdeScoredEnrichedRecordClassResolver dataSourceToAdeScoredEnrichedRecordClassResolver;

    @Test
    public void testDlpfileIsResolved(){
        Class<? extends AdeScoredEnrichedRecord> record = dataSourceToAdeScoredEnrichedRecordClassResolver.getClass(DataSource.DLPFILE.toString().toLowerCase());
        Assert.assertTrue(record.equals(AdeScoredDlpFileRecord.class));
    }

    @Test
    public void testDlpmailIsNotResolved(){
        Class<? extends AdeScoredEnrichedRecord> record = dataSourceToAdeScoredEnrichedRecordClassResolver.getClass(DataSource.DLPMAIL.toString().toLowerCase());
        Assert.assertTrue(record == null);
    }

    @Test
    public void testNonExistingDataSourceCase(){
        Class<? extends AdeScoredEnrichedRecord> record = dataSourceToAdeScoredEnrichedRecordClassResolver.getClass("NonExistingDataSource");
        Assert.assertTrue(record == null);
    }
}
