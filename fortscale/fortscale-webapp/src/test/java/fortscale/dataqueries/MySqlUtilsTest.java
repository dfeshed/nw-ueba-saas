package fortscale.dataqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.mysqlgenerator.MySqlUtils;
import org.eclipse.core.runtime.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.StringValueResolver;

import static org.junit.Assert.assertEquals;

public class MySqlUtilsTest {

    DataQueryUtils dataQueryUtils;
    MySqlUtils mySqlUtils;
    public static String dto1 = "{\"fields\":[],\"conditions\":[{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]}],\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
    protected DataQueryDTO dataQueryDTO1;
    private ObjectMapper mapper = new ObjectMapper();
    protected StringValueResolver stringValueResolver;

    @Before
    public void setUp() throws Exception {
        dataQueryUtils = new DataQueryUtils();
        mySqlUtils = new MySqlUtils();
        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);

        stringValueResolver = Mockito.mock(StringValueResolver.class);
        dataQueryUtils.setEmbeddedValueResolver(stringValueResolver);
        mySqlUtils.setDataQueryUtils(dataQueryUtils);

        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.username.column" + "}")).thenReturn("account_name");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.source_machine_score.column" + "}")).thenReturn("hostnameScore");
        Mockito.when(stringValueResolver.resolveStringValue("${" + "entities.kerberos_logins.field.source_machine_score.type" + "}")).thenReturn("NUMBER");

    }

    @Test
    public void getFieldSqlWithAlias() throws Exception {
        DataQueryDTO.DataQueryField field = new DataQueryDTO.DataQueryField();
        field.setId("username");
        field.setAlias("theNameOfTheUser");
        field.setEntity("kerberos_logins");
        String sqlPart = mySqlUtils.getFieldSql(field,dataQueryDTO1,true);
        assertEquals("get SQL of a field with alias", sqlPart, "account_name as theNameOfTheUser");
    }

    @Test
    public void getFieldSqlNoAlias() throws Exception {
        DataQueryDTO.DataQueryField field = new DataQueryDTO.DataQueryField();
        field.setId("username");
        field.setEntity("kerberos_logins");
        String sqlPart = mySqlUtils.getFieldSql(field,dataQueryDTO1);
        assertEquals("get SQL of a field with no alias", sqlPart, "account_name");
    }

    @Test
    public void getConditionSql() throws Exception {
        DataQueryDTO.ConditionField conditionField = new DataQueryDTO.ConditionField();

        DataQueryDTO.DataQueryField field = new DataQueryDTO.DataQueryField();
        field.setId("source_machine_score");
        field.setAlias("score");
        field.setEntity("kerberos_logins");
        field.valueType = QueryValueType.NUMBER;

        conditionField.field =field;
        conditionField.operator=DataQueryDTO.Operator.greaterThanOrEquals;
        conditionField.valueType = QueryValueType.NUMBER;
        conditionField.setValue("50");

        String sqlPart = mySqlUtils.getConditionFieldSql(conditionField,dataQueryDTO1);
        assertEquals("get SQL of a field with no alias", sqlPart, "hostnameScore as score >= 50");
    }



}