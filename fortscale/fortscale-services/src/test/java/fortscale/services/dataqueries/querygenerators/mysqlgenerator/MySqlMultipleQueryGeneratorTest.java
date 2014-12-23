package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.SupportedDBType;
import fortscale.services.dataqueries.DataQueryGeneratorTest;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.MultipleDataQueryDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MySqlMultipleQueryGeneratorTest extends DataQueryGeneratorTest {
    private MySqlMultipleQueryGenerator mySqlMultipleQueryGenerator;

    static String subQueryUnionAllDtoJson = "{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\",\"func\":{\"name\":\"count\",\"params\":{\"distinct\":\"true\"}}},{\"alias\":\"max_score\",\"id\":\"event_score\",\"func\":{\"name\":\"max\"}},{\"alias\":\"average_score\",\"id\":\"event_score\",\"func\":{\"name\":\"avg\"}},{\"alias\":\"ssh_count\",\"func\":{\"name\":\"sum\",\"params\":{\"field\":\"ssh_count\"}}},{\"alias\":\"kerberos_count\",\"func\":{\"name\":\"sum\",\"params\":{\"field\":\"kerberos_count\"}}}],\"entities\":[],\"sort\":[{\"field\":{\"id\":\"destination_machine\"}}],\"limit\":50,\"offset\":0,\"subQuery\":{\"combineMethod\":\"UnionAll\",\"dataQueries\":[{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\"},{\"id\":\"event_score\"}],\"entities\":[\"kerberos_logins\"],\"sort\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"is_user_administrator\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"},{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418673600,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1419278400,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"is_sensitive_machine\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"}]}},{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\"},{\"id\":\"event_score\"}],\"entities\":[\"ssh\"],\"sort\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"is_user_administrator\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"},{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418673600,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1419278400,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"is_sensitive_machine\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"}]}}]},\"groupBy\":[{\"id\":\"destination_machine\"}]}";
    DataQueryDTO dataQueryDto_UnionAll;
    MySqlQueryRunner mySqlQueryRunner;
    MultipleDataQueryDTO subQueryDto_UnionAll;
    MultipleDataQueryDTO subQueryDto_UnionDistinct;

    @Before
    public void setUp()
            throws Exception {
        super.setUp();
        mySqlMultipleQueryGenerator = new MySqlMultipleQueryGenerator();
        mySqlQueryRunner = Mockito.mock(MySqlQueryRunner.class);
        mySqlMultipleQueryGenerator.setMySqlQueryRunner(mySqlQueryRunner);
        mySqlMultipleQueryGenerator.setDataEntitiesConfig(dataEntitiesConfig);

        dataQueryDto_UnionAll = mapper.readValue(subQueryUnionAllDtoJson, DataQueryDTO.class);
        subQueryDto_UnionAll = dataQueryDto_UnionAll.getSubQuery();

        subQueryDto_UnionDistinct = dataQueryDto_UnionDistinct.getSubQuery();
        Mockito.when(mySqlQueryRunner.generateQuery(Mockito.any(DataQueryDTO.class))).thenReturn("[query]");
    }

    @Test
    public void testGenerateSql_UnionAll() throws Exception {
        String sqlStr = mySqlMultipleQueryGenerator.generateQueryPart(subQueryDto_UnionAll);
        String expectedString = "[query] UNION ALL [query]";
        assertEquals("Multiple queries combined using UNION ALL" , expectedString, sqlStr);
    }

    @Test
    public void testGenerateSql_UnionDistinct() throws Exception {
        String sqlStr = mySqlMultipleQueryGenerator.generateQueryPart(subQueryDto_UnionDistinct);
        String expectedString = "[query] UNION [query]";
        assertEquals("Multiple queries combined using UNION" , expectedString, sqlStr);
    }
}