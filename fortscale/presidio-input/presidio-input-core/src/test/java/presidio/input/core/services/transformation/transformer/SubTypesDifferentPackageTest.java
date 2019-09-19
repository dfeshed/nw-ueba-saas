package presidio.input.core.services.transformation.transformer;

import edu.emory.mathcs.backport.java.util.Arrays;
import fortscale.utils.reflection.PresidioReflectionUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.junit.Assert;
import org.junit.Test;

public class SubTypesDifferentPackageTest {
    @Test
    public void getSubTypes() {
        Assert.assertTrue(PresidioReflectionUtils.getSubTypes(
                Arrays.asList(new String[]{"presidio.input.core.services.transformation.transformer",
                        "fortscale.utils.transform"}),
                AbstractJsonObjectTransformer.class).size() > 20);
    }
}
