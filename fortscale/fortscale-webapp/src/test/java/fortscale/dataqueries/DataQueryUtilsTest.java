package fortscale.dataqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.mysqlgenerator.DataQueryGeneratorTest;
import fortscale.dataqueries.querygenerators.mysqlgenerator.MySqlUtils;
import org.eclipse.core.runtime.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataQueryUtilsTest{
    public static String dto1 = "{\"fields\":[],\"conditions\":[{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]}],\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
    protected DataQueryDTO dataQueryDTO1;
    private ObjectMapper mapper = new ObjectMapper();

    protected StringValueResolver stringValueResolver;

    @Autowired
    private MySqlUtils mySqlUtils;


    DataQueryUtils dataQueryUtils = new DataQueryUtils();

    @Before
    public void setUp() throws Exception {
        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);
        stringValueResolver = Mockito.mock(StringValueResolver.class);
        dataQueryUtils.setEmbeddedValueResolver(stringValueResolver);

        String entityToTest = "kerberos_logins";
        String entityToTestFields = "source_machine_type, destination_machine_type, failure_code";
        Mockito.when(stringValueResolver.resolveStringValue("entities.kerberos_logins.field.source_machine_type.type")).thenReturn("STRING");

        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities." + entityToTest + ".fields" + "}")).thenReturn(entityToTestFields);
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities" + "}")).thenReturn(entityToTest);
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.short_name" + "}")).thenReturn("Kerberos");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.logins.short_name" + "}")).thenReturn("Login");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.source_machine_type.name" + "}")).thenReturn("Source Computer Type");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.source_machine_type.type" + "}")).thenReturn("STRING");

        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.destination_machine_type.name" + "}")).thenReturn("Destination Computer Type");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.destination_machine_type.type" + "}")).thenReturn("STRING");

        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.failure_code.name" + "}")).thenReturn("Error Code");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.failure_code.type" + "}")).thenReturn("STRING");

    }

    @Test
    public void testGetAllEntityFields() throws Exception {
        ArrayList<String> arr = dataQueryUtils.getAllEntityFields("kerberos_logins");

        String listString="";
        for (String s : arr){ listString += s + ", ";}
        assertEquals("Get all entity fields of kerberos_logins", listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void testGetAllLogicalEntities() throws Exception {
        List<LogicalDataQueryEntity> arr = dataQueryUtils.getAllLogicalEntities();
        String listString="";
        for (LogicalDataQueryEntity.Field field : arr.get(0).fields){ listString += field.id + ", ";}
        assertEquals("SQL Select Part for DTO1" , listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void testGetLogicalEntity() throws Exception {
        LogicalDataQueryEntity entity = dataQueryUtils.getLogicalEntity("kerberos_logins");
        String listString="";
        for (LogicalDataQueryEntity.Field field : entity.fields){ listString += field.id + ", ";}
        assertEquals("SQL Select Part for DTO1" , listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

   /* @Test
    public void getEntityPartitions() throws Exception {
        ArrayList<DataQueryPartition> partitions = dataQueryUtils.getEntityPartitions("kerberos_logins");
        String listString="";
        *//*for (LogicalDataQueryEntity.Field field : entity.fields){ listString += field.id + ", ";}*//*
        assertEquals("SQL Select Part for DTO1" , listString, "source_machine_type, destination_machine_type, failure_code, ");
    }*/



}