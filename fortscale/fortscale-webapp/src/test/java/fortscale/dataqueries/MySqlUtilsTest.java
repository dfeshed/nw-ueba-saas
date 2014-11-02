package fortscale.dataqueries;

import org.eclipse.core.runtime.Assert;
import org.junit.Before;
import org.junit.Test;

public class MySqlUtilsTest {

    DataQueryUtils dataQueryUtils;

    @Before
    public void setUp() throws Exception {
        dataQueryUtils = new DataQueryUtils();
    }

    @Test
    public void testGetAllLogicalEntities() throws Exception {
        Assert.isNotNull(dataQueryUtils.getAllLogicalEntities(), "Can't get logical entities.");
    }
}