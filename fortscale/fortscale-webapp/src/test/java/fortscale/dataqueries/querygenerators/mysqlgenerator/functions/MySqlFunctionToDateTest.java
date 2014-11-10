package fortscale.dataqueries.querygenerators.mysqlgenerator.functions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MySqlFunctionToDateTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception {
        this.function = new MySqlFunctionToDate();
        setDtoJson("{\"fields\":[{\"id\":\"event_time\",\"alias\":\"date\",\"func\":{\"name\":\"to_date\"}}], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "TO_DATE(date_time)";
        assertEquals("TO_DATE function for date_time column" , expectedSql, functionSql);
    }
}