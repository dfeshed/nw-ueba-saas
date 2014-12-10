package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MySqlFunctionMaxTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception {
        this.function = new MySqlFunctionMax();
        setDtoJson("{\"fields\":[{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"max\",\"params\":{\"distinct\":true}}}, {\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"max\"}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(1), dataQueryDTO);
        String expectedSql = "MAX(event_score)";
        assertEquals("MAX function for date_time column" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "MAX(DISTINCT event_score)";
        assertEquals("MAX function for date_time column with DISTINCT operator" , expectedSql, functionSql);
    }
}