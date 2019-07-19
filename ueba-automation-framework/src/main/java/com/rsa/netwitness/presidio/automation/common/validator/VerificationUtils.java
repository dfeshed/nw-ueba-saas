package com.rsa.netwitness.presidio.automation.common.validator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rsa.netwitness.presidio.automation.domain.config.HostConf;
import com.rsa.netwitness.presidio.automation.utils.common.FileCommands;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VerificationUtils<T> {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(VerificationUtils.class.getName());

    Type type = new TypeToken<List<T>>() {}.getType();

    public VerificationUtils() {
    }

    private void storeExpectedFromMongo(List<T> actualEvents, String expectedJsonFile){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String expectedResults = gson.toJson(actualEvents, type);
        FileCommands.writeToFile( expectedJsonFile, expectedResults);
    }

    private void storeExpectedObject(T object, String expectedJsonFile, Type type){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String expectedResults = gson.toJson(object, type);
        FileCommands.writeToFile( expectedJsonFile, expectedResults);
    }

    private void storeExpectedString(String expectedString, String expectedJsonFile){
        FileCommands.writeToFile( expectedJsonFile, expectedString);
    }

    private void verifyExpectedString(String actualStr, String expectedJsonFile) {

        // Read expected file
        String expectedStr = FileCommands.readFromFile(expectedJsonFile);

        // Compare
        Assert.assertEquals(expectedStr, actualStr + "\n");
    }

    private void verifyExpectedFromMongo(List<T> actualEvents, String expectedJsonFile) {

        String json = FileCommands.readFromFile(expectedJsonFile);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        List<T> expectedEvents = gson.fromJson(json, type);

        // transforming actualEvents object by gson in order to get the same object type as expected
        // See if can improve by getting List<T> when deserializing the expected json string
        String tempJson = gson.toJson(actualEvents, type);
        List<T> transformedActualEvents = gson.fromJson(tempJson, type);

        // Verify that there are objects
        Assert.assertTrue(transformedActualEvents.size() > 0);

        // Verify the number of objects retrieved
        Assert.assertEquals(transformedActualEvents.size(), expectedEvents.size());

        // Verify array JSON objects retrieved
        try {
            JSONArray jsonObj1 = new JSONArray(json);
            JSONArray jsonObj2 = new JSONArray(tempJson);
            assertThat(jsonObj1)
                    .isEqualToComparingFieldByFieldRecursively(jsonObj2);
        } catch (JSONException e) {
            LOGGER.error("Unexpected JSON exception: " + e.getMessage());
            Assert.assertFalse(true);
        }
    }

    private void verifyExpectedObject(T actualObject, String expectedJsonFile, Type type) {

        // Read expected file
        String expectedJsonStr = FileCommands.readFromFile(expectedJsonFile);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        // Serialize actual object
        String actualJsonStr = gson.toJson(actualObject, type);

        // Deserialize expected and actual
        T expectedObj = gson.fromJson(expectedJsonStr, type);
        T actualObj = gson.fromJson(actualJsonStr, type);

        // Compare
        String expectedStr = expectedObj.toString();
        String actualStr   = actualObj.toString();
        Assert.assertEquals(expectedStr, actualStr);
    }


    public void verify(List<T> actualEvents, String expectedJsonFile) {
        if (HostConf.isGenerate) {
            storeExpectedFromMongo(actualEvents, expectedJsonFile);
        }
        else {
            verifyExpectedFromMongo(actualEvents, expectedJsonFile);
        }
    }

    public void verify(T expectedObject, String expectedJsonFile, Type type) {

        if (HostConf.isGenerate) {
            storeExpectedObject(expectedObject, expectedJsonFile, type);
        }
        else {
            verifyExpectedObject(expectedObject, expectedJsonFile, type);
        }
    }

    public void verifyStr(String expectedString, String expectedJsonFile) {

        if (HostConf.isGenerate) {
            storeExpectedString(expectedString, expectedJsonFile);
        }
        else {
            verifyExpectedString(expectedString, expectedJsonFile);
        }
    }
}
