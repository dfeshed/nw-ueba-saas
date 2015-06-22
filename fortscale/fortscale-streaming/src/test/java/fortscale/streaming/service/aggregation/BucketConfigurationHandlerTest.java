package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by amira on 22/06/2015.
 */
public class BucketConfigurationHandlerTest {

    @Test
    public void testGetRelatedBucketConfs() {
        String filePah = "src/test/resources/BucketConfigurationHandlerTest.json";
        BucketConfigurationHandler bch = new BucketConfigurationHandler(filePah);
        JSONObject event = new JSONObject();

        event.put(BucketConfigurationHandler.EVENT_FIELD_DATA_SOURCE, "ssh");
        List<FeatureBucketConf> bcl = bch.getRelatedBucketConfs(event);
        Assert.assertEquals(2, bcl.size());
        FeatureBucketConf fbc = bcl.get(0);
        Assert.assertEquals("bc1", fbc.getName());
        fbc = bcl.get(1);
        Assert.assertEquals("bc3", fbc.getName());

        event.put(BucketConfigurationHandler.EVENT_FIELD_DATA_SOURCE, "vpn");
        bcl = bch.getRelatedBucketConfs(event);
        Assert.assertEquals(2, bcl.size());
        fbc = bcl.get(0);
        Assert.assertEquals("bc2", fbc.getName());
        fbc = bcl.get(1);
        Assert.assertEquals("bc3", fbc.getName());

        event.put(BucketConfigurationHandler.EVENT_FIELD_DATA_SOURCE, "notexists");
        bcl = bch.getRelatedBucketConfs(event);
        Assert.assertNull(bcl);

    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullFileName() {
        BucketConfigurationHandler bch = new BucketConfigurationHandler(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullEmptyFileName() {
        BucketConfigurationHandler bch = new BucketConfigurationHandler("");
    }



}
