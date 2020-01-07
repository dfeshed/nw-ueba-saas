package presidio.input.core.services.transformation.transformer;

import fortscale.utils.reflection.ReflectionUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import presidio.input.core.services.transformation.transformer.test.TestJsonObjectTransformer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class InputTransformerSubTypesTest {
    @Test
    public void test_input_transformer_sub_types() {
        Set<Class<?>> expected = Collections.singleton(TestJsonObjectTransformer.class);
        Set<Class<?>> actual = ReflectionUtils.getSubTypesOf(
                Collections.singletonList("presidio.input.core.services.transformation.transformer.test"),
                AbstractJsonObjectTransformer.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void production_input_transformer_sub_types() {
        Set<Class<?>> actual = ReflectionUtils.getSubTypesOf(
                Arrays.asList("fortscale.utils.transform", "presidio.input.core.services.transformation.transformer"),
                AbstractJsonObjectTransformer.class);
        Assert.assertTrue(actual.size() > 20);
        Assert.assertFalse(actual.contains(TestJsonObjectTransformer.class));
    }
}
