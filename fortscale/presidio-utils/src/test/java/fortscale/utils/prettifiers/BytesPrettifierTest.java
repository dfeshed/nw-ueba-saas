package fortscale.utils.prettifiers;

import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by avivs on 20/01/16.
 */
public class BytesPrettifierTest {

    @Test
    public void testPrettify() throws Exception {
        Assert.assertEquals("1.023 KB", BytesPrettifier.prettify("1048", 3));
        Assert.assertEquals("1.5 GB", BytesPrettifier.prettify("1610612736", 2));
        Assert.assertEquals("1.6 TB", BytesPrettifier.prettify("1849267445000", 1));
        Assert.assertEquals("1.49 PB", BytesPrettifier.prettify("1688849860000000"));
    }

    @Test
    public void testRatePrettify() throws Exception {
        Assert.assertEquals("1.0 KB/s", BytesPrettifier.ratePrettify("1024", 2));
        Assert.assertEquals("1.5 GB/m", BytesPrettifier.ratePrettify("1610612736", 2, "m"));
        Assert.assertEquals("1.54 TB/h", BytesPrettifier.ratePrettify("1699267440000", "h"));
        Assert.assertEquals("1.5 PB/s", BytesPrettifier.ratePrettify("1698849860000000"));
    }


}