package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Created by shays on 29/03/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationConfigurationHelperTest {

    @Mock
    ApplicationConfigurationService applicationConfigurationService;

    @InjectMocks
    ApplicationConfigurationHelper applicationConfigurationHelper;


    @Test
    public void testWriteToConfiguration() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //Set default values
        TestSubClass testSubClassDefaults = new TestSubClass();
        testSubClassDefaults.setAttributeNumber(1);
        testSubClassDefaults.setAttributeDouble(1.1);
        testSubClassDefaults.setAttributeString("a");
        testSubClassDefaults.setAttributeBoolean(Boolean.TRUE);
    //    testSubClassDefaults.setAttributeList(Arrays.asList("aa","abc"));

        applicationConfigurationHelper.syncWithConfiguration("prefix",testSubClassDefaults, Arrays.asList(
             new ImmutablePair<String, String>("attributeStringKey","attributeString"),
             new ImmutablePair<String, String>("attributeNumberKey","attributeNumber"),
             new ImmutablePair<String, String>("attributeDoubleKey","attributeDouble"),
             new ImmutablePair<String, String>("attributeBooleanKey","attributeBoolean")
         //    new ImmutablePair<String, String>("attributeListKey","attributeList")
        ));


        //Assert that application configuration updated
        Mockito.verify(applicationConfigurationService,Mockito.times(1)).insertConfigItem("prefix.attributeStringKey", "a");
        Mockito.verify(applicationConfigurationService,Mockito.times(1)).insertConfigItem("prefix.attributeNumberKey","1");
        Mockito.verify(applicationConfigurationService,Mockito.times(1)).insertConfigItem("prefix.attributeDoubleKey","1.1");
        Mockito.verify(applicationConfigurationService,Mockito.times(1)).insertConfigItem("prefix.attributeBooleanKey","True");
     //   Mockito.verify(applicationConfigurationService,Mockito.times(1)).insertConfigItem("prefix.attributeListKey","aa///abc");

        //Assert that original object was not chaged
        Assert.assertEquals("a", testSubClassDefaults.getAttributeString());
        Assert.assertEquals(1, testSubClassDefaults.getAttributeNumber());
        Assert.assertEquals(1.1,testSubClassDefaults.getAttributeDouble(),0.1);
        Assert.assertEquals(true, testSubClassDefaults.isAttributeBoolean());
//        Assert.assertEquals(2, testSubClassDefaults.getAttributeList().size());


    }

    @Test
    public void testReadFromConfiguration() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //Set default values
        TestSubClass testSubClassDefaults = new TestSubClass();
        testSubClassDefaults.setAttributeNumber(1);
        testSubClassDefaults.setAttributeDouble(1.1);
        testSubClassDefaults.setAttributeString("a");
        testSubClassDefaults.setAttributeBoolean(Boolean.TRUE);
     //   testSubClassDefaults.setAttributeList(Arrays.asList("aa","abc"));

        //Set fake values on applicationConfigurationService mock
        Mockito.when(applicationConfigurationService.getApplicationConfiguration("prefix.attributeStringKey")).thenReturn(new ApplicationConfiguration("prefix.attributeStringKey","b"));
        Mockito.when(applicationConfigurationService.getApplicationConfiguration("prefix.attributeNumberKey")).thenReturn(new ApplicationConfiguration("prefix.attributeNumberKey","2"));
        Mockito.when(applicationConfigurationService.getApplicationConfiguration("prefix.attributeDoubleKey")).thenReturn(new ApplicationConfiguration("prefix.attributeDoubleKey","2.2"));
        Mockito.when(applicationConfigurationService.getApplicationConfiguration("prefix.attributeBooleanKey")).thenReturn(new ApplicationConfiguration("prefix.attributeBooleanKey","False"));
   //     Mockito.when(applicationConfigurationService.getApplicationConfiguration("prefix.attributeListKey")).thenReturn(new ApplicationConfiguration("prefix.attributeListKey","False"));


        //Execute sync
        applicationConfigurationHelper.syncWithConfiguration("prefix", testSubClassDefaults, Arrays.asList(
                new ImmutablePair<String, String>("attributeStringKey", "attributeString"),
                new ImmutablePair<String, String>("attributeNumberKey", "attributeNumber"),
                new ImmutablePair<String, String>("attributeDoubleKey", "attributeDouble"),
                new ImmutablePair<String, String>("attributeBooleanKey", "attributeBoolean")
                //new ImmutablePair<String, String>("attributeListKey","attributeList")
        ));


        //Assert that original object was changed, and now contain the values from fake applicationConfigurationService mock
        Assert.assertEquals("b", testSubClassDefaults.getAttributeString());
        Assert.assertEquals(2, testSubClassDefaults.getAttributeNumber());
        Assert.assertEquals(2.2,testSubClassDefaults.getAttributeDouble(),0.1);
        Assert.assertEquals(false, testSubClassDefaults.isAttributeBoolean());

//        Assert.assertEquals(2, testSubClassDefaults.getAttributeList().size());
//        Assert.assertEquals("aa", testSubClassDefaults.getAttributeList().get(0));
//        Assert.assertEquals("abc", testSubClassDefaults.getAttributeList().get(1));


    }


    //The pojo that tests
    public class TestSubClass{

        private String attributeString;
        private int attributeNumber;
        private double attributeDouble;
        private boolean attributeBoolean;
        //private List<String> attributeList;


        public String getAttributeString() {
            return attributeString;
        }

        public void setAttributeString(String attributeString) {
            this.attributeString = attributeString;
        }

        public int getAttributeNumber() {
            return attributeNumber;
        }

        public void setAttributeNumber(int attributeNumber) {
            this.attributeNumber = attributeNumber;
        }

        public double getAttributeDouble() {
            return attributeDouble;
        }

        public boolean isAttributeBoolean() {
            return attributeBoolean;
        }

        public void setAttributeBoolean(boolean attributeBoolean) {
            this.attributeBoolean = attributeBoolean;
        }

        public void setAttributeDouble(double attributeDouble) {
            this.attributeDouble = attributeDouble;
        }

//        public List<String> getAttributeList() {
//            return attributeList;
//        }
//
//        public void setAttributeList(List<String> attributeList) {
//            this.attributeList = attributeList;
//        }
    }


}

