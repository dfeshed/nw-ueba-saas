package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MySqlFunctionMinTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception {
        this.function = new MySqlFunctionMin();
        setDtoJson("{\"fields\":[{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"min\",\"params\":{\"distinct\":true}}}, {\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"min\"}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(1), dataQueryDTO);
        String expectedSql = "MIN(event_score)";
        assertEquals("MIN    function for date_time column" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "MIN(DISTINCT event_score)";
        assertEquals("MIN    function for date_time column" , expectedSql, functionSql);
    }
}