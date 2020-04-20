package presidio.data.generators.user;

import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by barak_schuster on 10/8/17.
 */
public class UserRegexCyclicValuesGeneratorTest {
    @Test
    public void generateUserIdValuesByRegex() {
        IUserGenerator generator = new UserRegexCyclicValuesGenerator("s-[a-z]{1}[1-3]{1}");

        User firstGeneratedValue = generator.getNext();
        int amountOfCharsInAbc = 26;
        int amountOfCyclicNumbers = 3;
        int expectedIterationsTillRepeat = amountOfCharsInAbc * amountOfCyclicNumbers;
        User currentGeneratedValue = null;
        int generatedValuesCounter;
        Set<User> generatedValues = new HashSet<>();
        for (generatedValuesCounter = 0; generatedValuesCounter < expectedIterationsTillRepeat; generatedValuesCounter++) {
            currentGeneratedValue = generator.getNext();
            Assert.assertFalse(generatedValues.contains(currentGeneratedValue),"cyclic values should not repeat");
            generatedValues.add(currentGeneratedValue);
        }

        Assert.assertEquals(currentGeneratedValue, firstGeneratedValue,"if it was cyclic we should have returned to the first value in the cycle");
    }

    @Test
    public void generateStringValuesByRegex() {
        String lastNamePattern = "the grey";
        IUserGenerator generator =
                new UserRegexCyclicValuesGenerator("user[1-73]{1}","s-[a-z]{1}[1-3]{1}",
                        "gandalf[1-73]{1}", lastNamePattern,true,false);

        User firstGeneratedValue = generator.getNext();
        int amountOfCharsInAbc = 26;
        int amountOfCyclicNumbers = 3;
        int expectedIterationsTillRepeat = amountOfCharsInAbc * amountOfCyclicNumbers;
        User currentGeneratedValue = null;
        int generatedValuesCounter;
        Set<User> generatedValues = new HashSet<>();
        for (generatedValuesCounter = 0; generatedValuesCounter < expectedIterationsTillRepeat; generatedValuesCounter++) {
            currentGeneratedValue = generator.getNext();
            Assert.assertTrue(currentGeneratedValue.getAdministrator());
            Assert.assertFalse(currentGeneratedValue.getAnonymous());
            Assert.assertEquals(lastNamePattern,currentGeneratedValue.getLastName());
            Assert.assertFalse(generatedValues.contains(currentGeneratedValue),"cyclic id's should not repeat");
            generatedValues.add(currentGeneratedValue);
        }

        Assert.assertEquals(currentGeneratedValue, firstGeneratedValue,"if it was cyclic we should have returned to the first value in the cycle");
    }
}