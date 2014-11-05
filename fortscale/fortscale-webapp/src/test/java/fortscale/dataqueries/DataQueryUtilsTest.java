package fortscale.dataqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataQueryUtilsTest{
    public static String dto1 = "{\"fields\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
    protected DataQueryDTO dataQueryDTO1;
    private ObjectMapper mapper = new ObjectMapper();

    protected StringValueResolver stringValueResolver;


    DataEntitiesConfig dataEntitiesConfig = new DataEntitiesConfig();

    @Before
    public void setUp() throws Exception {
        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);
        stringValueResolver = Mockito.mock(StringValueResolver.class);
        dataEntitiesConfig.setEmbeddedValueResolver(stringValueResolver);

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

        //for entity partition test
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.access_event.partitions" + "}")).thenReturn("event_time_utc daily day_partition");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.username.column" + "}")).thenReturn("account_name");

    }

    @Test
    public void testGetAllEntityFields() throws Exception {
        ArrayList<String> arr = dataEntitiesConfig.getAllEntityFields("kerberos_logins");

        String listString="";
        for (String s : arr){ listString += s + ", ";}
        assertEquals("Get all entity fields of kerberos_logins", listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void testGetAllLogicalEntities() throws Exception {
        List<DataEntity> arr = dataEntitiesConfig.getAllLogicalEntities();
        String listString="";
        for (DataEntity.Field field : arr.get(0).fields){ listString += field.id + ", ";}
        assertEquals("SQL Select Part for DTO1" , listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void testGetLogicalEntity() throws Exception {
        DataEntity entity = dataEntitiesConfig.getLogicalEntity("kerberos_logins");
        String listString="";
        for (DataEntity.Field field : entity.fields){ listString += field.id + ", ";}
        assertEquals("SQL Select Part for DTO1" , listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void getEntityPartitions() throws Exception {
        PartitionStrategy partition = dataEntitiesConfig.getEntityPartitionStrategy("access_event");
        assertEquals("partition.entity_field" , partition.getImpalaPartitionFieldName(), "event_time_utc");
    }

    @Test
    public void testGetFieldColumn() throws Exception {
        String entity = dataEntitiesConfig.getFieldColumn("kerberos_logins", "username");
        assertEquals("Get value of a column from impala" , entity, "account_name");
    }


}