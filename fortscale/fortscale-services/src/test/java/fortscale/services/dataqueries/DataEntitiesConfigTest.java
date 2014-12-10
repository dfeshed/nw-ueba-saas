package fortscale.services.dataqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.DataEntityField;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.StringValueResolver;

import java.util.List;

import static org.junit.Assert.*;

public class DataEntitiesConfigTest {
    public static String dto1 = "{\"fields\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
    public static String joinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418121428,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418207828,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\",\"users\"],\"join\":[{\"type\":\"RIGHT\",\"entity\":\"users\",\"joinFields\":[{\"entity\":\"kerberos_logins\",\"field\":\"normalized_username\"},{\"entity\":\"users\",\"field\":\"username\"}]}],\"sort\":[],\"limit\":50,\"offset\":0}";

    protected DataQueryDTO dataQueryDTO1;
    protected DataQueryDTO joinDTO;
    protected ObjectMapper mapper = new ObjectMapper();

    protected StringValueResolver stringValueResolver;

    DataEntitiesConfig dataEntitiesConfig = new DataEntitiesConfig();

    @Before
    public void setUp() throws Exception {
        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);
        joinDTO = mapper.readValue(joinDTOJson, DataQueryDTO.class);

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
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.partitions" + "}")).thenReturn("daily");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.username.column" + "}")).thenReturn("account_name");

    }

    @Test
    public void testGetAllEntityFields() throws Exception {
        List<String> arr = dataEntitiesConfig.getAllEntityFields("kerberos_logins");

        String listString="";
        for (String s : arr){ listString += s + ", ";}
        assertEquals("Get all entity fields of kerberos_logins", listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void testGetAllLogicalEntities() throws Exception {
        List<DataEntity> arr = dataEntitiesConfig.getAllLogicalEntities();
        String listString="";
        for (DataEntityField field : arr.get(0).getFields()){ listString += field.getId() + ", ";}
        assertEquals("SQL Select Part for DTO1" , listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void testGetLogicalEntity() throws Exception {
        DataEntity entity = dataEntitiesConfig.getLogicalEntity("kerberos_logins");
        String listString="";
        for (DataEntityField field : entity.getFields()){ listString += field.getId() + ", ";}
        assertEquals("SQL Select Part for DTO1" , listString, "source_machine_type, destination_machine_type, failure_code, ");
    }

    @Test
    public void getEntityPartitions() throws Exception {
        PartitionStrategy partition = dataEntitiesConfig.getEntityPartitionStrategy("kerberos_logins");
        assertEquals("partition.entity_field" , "yearmonthday",partition.getImpalaPartitionFieldName());
    }

    @Test
    public void testGetFieldColumn() throws Exception {
        String entity = dataEntitiesConfig.getFieldColumn("kerberos_logins", "username");
        assertEquals("Get value of a column from impala" , entity, "account_name");
    }


}