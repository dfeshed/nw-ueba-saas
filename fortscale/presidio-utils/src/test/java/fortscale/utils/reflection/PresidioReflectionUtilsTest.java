package fortscale.utils.reflection;

import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PresidioReflectionUtilsTest {

    @Test
    public void getSubTypes() {
        Set<Class> expectedSubTypes = new HashSet<>();
        expectedSubTypes.add(TestObject.class);
        Assert.assertEquals(expectedSubTypes, PresidioReflectionUtils.getSubTypes(
                Collections.singletonList("fortscale.utils.reflection"), AbstractJsonObjectTransformer.class));
    }
}