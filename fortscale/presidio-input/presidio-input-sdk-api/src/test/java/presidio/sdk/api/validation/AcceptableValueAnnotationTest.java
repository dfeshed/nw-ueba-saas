package presidio.sdk.api.validation;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * Created by alexp on 25-Jun-17.
 */
public class AcceptableValueAnnotationTest {

    @Test
    public void valid() {
        TestClass testClass = new TestClass("copy");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void fieldDoesntMatch() {
        TestClass testClass = new TestClass("not copy");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof AcceptableValues);
    }


    public class TestClass {

        @AcceptableValues(fieldValues = {"copy", "move", "recycle", "delete"})
        private String field;

        public TestClass(String field) {
            this.field = field;
        }
    }
}
