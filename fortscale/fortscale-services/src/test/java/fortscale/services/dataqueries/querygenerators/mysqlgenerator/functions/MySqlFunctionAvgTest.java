package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MySqlFunctionAvgTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception {
        this.function = new MySqlFunctionAvg();
        setDtoJson("{\"fields\":[{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"avg\",\"params\":{\"distinct\":true}}}, {\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"avg\"}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(1), dataQueryDTO);
        String expectedSql = "AVG(event_score)";
        assertEquals("AVG function for date_time column" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "AVG(DISTINCT event_score)";
        assertEquals("AVG function for date_time column with DISTINCT operator" , expectedSql, functionSql);
    }
}