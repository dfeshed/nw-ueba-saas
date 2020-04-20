package presidio.sdk.api.validation;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class NotEmptyIfAnotherFieldHasValueAnnotationTest {

    @Test
    public void valid() {
        TestClass testClass = new TestClass("copy", "not empty");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void validLong() {
        TestClass longTestClass = new TestClass("copy", 1L);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestClass>> violations = validator.validate(longTestClass);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void firstFieldDoesntMatch() {
        TestClass testClass = new TestClass("not copy", "");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        Assert.assertTrue(CollectionUtils.isEmpty(violations));
    }

    @Test
    public void secondFieldEmpty() {
        TestClass testClass = new TestClass("copy", "");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmptyIfAnotherFieldHasValue);
    }

    @Test
    public void secondFieldNull() {
        TestClass testClass = new TestClass("copy", null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        Assert.assertEquals(1, violations.size());
        Assert.assertTrue(violations.iterator().next().getConstraintDescriptor().getAnnotation() instanceof NotEmptyIfAnotherFieldHasValue);
    }

    @NotEmptyIfAnotherFieldHasValue(fieldName = "firstField", fieldValues = {"copy", "move", "recycle"}, dependFieldName = "notEmptyField")
    public class TestClass {

        private String firstField;
        private Object notEmptyField;

        public TestClass(String firstField, Object notEmptyField) {
            this.firstField = firstField;
            this.notEmptyField = notEmptyField;
        }
    }
}
