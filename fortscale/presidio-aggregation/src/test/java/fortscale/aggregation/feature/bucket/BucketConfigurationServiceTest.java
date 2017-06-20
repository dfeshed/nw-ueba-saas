package fortscale.aggregation.feature.bucket;


import fortscale.common.event.EventMessage;
import net.minidev.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by amira on 22/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/bucketconf-context-test.xml" })
public class BucketConfigurationServiceTest  {

    @Autowired
    BucketConfigurationService bch;
    
    @Value("${impala.table.fields.data.source}")
    private String dataSourceFieldName;

    @Test
    public void testGetRelatedBucketConfs() {
        JSONObject event = new JSONObject();

        event.put(dataSourceFieldName, "ssh");
        List<FeatureBucketConf> bcl = bch.getRelatedBucketConfs(new EventMessage(event));
        Assert.assertEquals(2, bcl.size());
        FeatureBucketConf fbc = bcl.get(0);
        Assert.assertEquals("bc1", fbc.getName());
        fbc = bcl.get(1);
        Assert.assertEquals("bc3", fbc.getName());

        event.put(dataSourceFieldName, "vpn");
        bcl = bch.getRelatedBucketConfs(new EventMessage(event));
        Assert.assertEquals(2, bcl.size());
        fbc = bcl.get(0);
        Assert.assertEquals("bc2", fbc.getName());
        fbc = bcl.get(1);
        Assert.assertEquals("bc3", fbc.getName());

        event.put(dataSourceFieldName, "notexists");
        bcl = bch.getRelatedBucketConfs(new EventMessage(event));
        Assert.assertNull(bcl);
    }
}
