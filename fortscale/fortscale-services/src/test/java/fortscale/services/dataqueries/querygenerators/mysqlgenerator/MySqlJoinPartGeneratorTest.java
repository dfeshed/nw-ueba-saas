package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.SupportedDBType;
import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MySqlJoinPartGeneratorTest extends DataQueryGeneratorTest {
    private String entityId, entityTable;

    public static String doubleJoinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418209915,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418296315,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"join\":[{\"type\":\"RIGHT\",\"entity\":\"users\",\"left\":{\"entity\":\"kerberos_logins\",\"field\":\"normalized_username\"},\"right\":{\"entity\":\"users\",\"field\":\"normalized_username\"}},{\"entity\":\"ssh\",\"type\":\"LEFT\",\"left\":{\"entity\":\"users\",\"field\":\"normalized_username\"},\"right\":{\"entity\":\"ssh\",\"field\":\"normalized_username\"}}],\"sort\":[],\"limit\":50,\"offset\":0}";
    private DataQueryDTO doubleJoinDTO;

    @Before
    public void setUp()
            throws Exception {
        generator = new MySqlJoinPartGenerator();

        super.setUp();

        doubleJoinDTO = mapper.readValue(doubleJoinDTOJson, DataQueryDTO.class);

        entityId = "kerberos_logins";
        entityTable = "authentication_scores";

        ArrayList<String> fieldIds = new ArrayList<>();
        fieldIds.add("source_machine");
        fieldIds.add("event_time");

        Mockito.when(dataEntitiesConfig.getAllEntityFields(entityId)).thenReturn(fieldIds);
        Mockito.when(dataEntitiesConfig.getEntityTable(entityId)).thenReturn(entityTable);
        Mockito.when(dataEntitiesConfig.getEntityTable("users")).thenReturn("users");
        Mockito.when(dataEntitiesConfig.getEntityTable("ssh")).thenReturn("ssh");
        Mockito.when(dataEntitiesConfig.getFieldColumn(entityId, "normalized_username")).thenReturn("normalized_username_column");
        Mockito.when(dataEntitiesConfig.getFieldColumn("users", "normalized_username")).thenReturn("username_column");
        Mockito.when(dataEntitiesConfig.getFieldColumn("ssh", "normalized_username")).thenReturn("normalized_username_column");
        Mockito.when(dataEntitiesConfig.getEntityDbType(Mockito.any(String.class))).thenReturn(SupportedDBType.MySQL);
    }


    @Test
    public void testSingleRightJoin() throws Exception {
        String sqlStr = generator.generateQueryPart(joinDTO);
        String expectedString = "RIGHT JOIN users ON authentication_scores.normalized_username_column = users.username_column";
        assertEquals("RIGHT JOIN between kerberos_logins.normalized_username and users.username", expectedString, sqlStr);
    }

    @Test
    public void testDoubleJoin() throws Exception {
        String sqlStr = generator.generateQueryPart(doubleJoinDTO);
        String expectedString = "RIGHT JOIN users ON authentication_scores.normalized_username_column = users.username_column LEFT JOIN ssh ON users.username_column = ssh.normalized_username_column";
        assertEquals("Double join - kerberos_loings-users, users-ssh", expectedString, sqlStr);
    }
}