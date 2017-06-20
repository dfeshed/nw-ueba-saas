package fortscale.utils.prettifiers;

import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by avivs on 20/01/16.
 */
public class NumbersPrettifierTest {

    @Test
    public void testTruncateDecimalsOnNatural() throws Exception {
        Assert.assertEquals("3.3", NumbersPrettifier.truncateDecimalsOnNatural(3.30));
        Assert.assertEquals("3", NumbersPrettifier.truncateDecimalsOnNatural(3.00));
        Assert.assertEquals("3.3", NumbersPrettifier.truncateDecimalsOnNatural("3.3"));
        Assert.assertEquals("3", NumbersPrettifier.truncateDecimalsOnNatural("3"));
    }
}