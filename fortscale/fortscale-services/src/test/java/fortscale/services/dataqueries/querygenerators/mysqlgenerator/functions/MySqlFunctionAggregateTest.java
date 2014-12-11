package fortscale.services.dataqueries.querygenerators.mysqlgenerator.functions;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class MySqlFunctionAggregateTest extends MySqlFunctionTest {

    @Before
    public void setUp() throws Exception {
        this.function = new MySqlFunctionAggregate();
        String[] jsonFields = {
                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"avg\"}}",
                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"avg\",\"params\":{\"distinct\":true}}}",

                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"min\"}}",
                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"min\",\"params\":{\"distinct\":true}}}",

                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"max\"}}",
                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"max\",\"params\":{\"distinct\":true}}}",

                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"sum\"}}",
                "{\"alias\":\"score\",\"id\":\"event_score\",\"func\":{\"name\":\"sum\",\"params\":{\"distinct\":true}}}"

        };


        setDtoJson("{\"fields\":[" + StringUtils.join(jsonFields, ",")  +"], \"entities\": [\"kerberos_logins\"]}");
        super.setUp();
    }

    @Test
    public void testGenerateSql_avg() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(0), dataQueryDTO);
        String expectedSql = "AVG(event_score)";
        assertEquals("AVG function for event_score column" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_avg_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(1), dataQueryDTO);
        String expectedSql = "AVG(DISTINCT event_score)";
        assertEquals("AVG function for event_score column with DISTINCT operator" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_min() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(2), dataQueryDTO);
        String expectedSql = "MIN(event_score)";
        assertEquals("MIN function for event_score column" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_min_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(3), dataQueryDTO);
        String expectedSql = "MIN(DISTINCT event_score)";
        assertEquals("MIN function for event_score column with DISTINCT operator" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_max() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(4), dataQueryDTO);
        String expectedSql = "MAX(event_score)";
        assertEquals("MAX function for event_score column" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_max_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(5), dataQueryDTO);
        String expectedSql = "MAX(DISTINCT event_score)";
        assertEquals("MAX function for event_score column with DISTINCT operator" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_sum() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(6), dataQueryDTO);
        String expectedSql = "SUM(event_score)";
        assertEquals("SUM function for event_score column" , expectedSql, functionSql);
    }

    @Test
    public void testGenerateSql_sum_distinct() throws Exception {
        String functionSql = function.generateSql(dataQueryDTO.getFields().get(7), dataQueryDTO);
        String expectedSql = "SUM(DISTINCT event_score)";
        assertEquals("SUM function for event_score column with DISTINCT operator" , expectedSql, functionSql);
    }


}