package fortscale.common.dataqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataqueries.querydto.DataQueryDTOImpl;
import fortscale.common.dataqueries.querydto.DataQueryDtoHelper;
import fortscale.common.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlFieldGenerator;
import org.mockito.Mockito;

/**
 * Created by Yossi on 23/12/2014.
 * Base class for testing generators
 */
public class DataQueryGeneratorTestBase<T> {
    protected static String subQueryUnionDistinctDtoJson = "{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\",\"func\":{\"name\":\"count\",\"params\":{\"distinct\":\"true\"}}},{\"alias\":\"max_score\",\"id\":\"event_score\",\"func\":{\"name\":\"max\"}},{\"alias\":\"average_score\",\"id\":\"event_score\",\"func\":{\"name\":\"avg\"}},{\"alias\":\"ssh_count\",\"func\":{\"name\":\"sum\",\"params\":{\"field\":\"ssh_count\"}}},{\"alias\":\"kerberos_count\",\"func\":{\"name\":\"sum\",\"params\":{\"field\":\"kerberos_count\"}}}],\"entities\":[],\"sort\":[{\"field\":{\"id\":\"destination_machine\"}}],\"limit\":50,\"offset\":0,\"subQuery\":{\"combineMethod\":\"UnionDistinct\",\"dataQueries\":[{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\"},{\"id\":\"event_score\"}],\"entities\":[\"kerberos_logins\"],\"sort\":[],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"is_user_administrator\"},\"queryOperator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"},{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418673600,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1419278400,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"is_sensitive_machine\"},\"queryOperator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"}]}},{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\"},{\"id\":\"event_score\"}],\"entities\":[\"ssh\"],\"sort\":[],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"is_user_administrator\"},\"queryOperator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"},{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418673600,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1419278400,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"is_sensitive_machine\"},\"queryOperator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"}]}}]},\"groupBy\":[{\"id\":\"destination_machine\"}]}";
    protected static String dto1 = "{\"fields\":[],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
    protected static String complexWhereDTOJson = "{\"entities\":[\"kerberos_logins\"],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"in\",\"type\":\"field\",\"value\":\"50,70\",\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"user_name\"},\"queryOperator\":\"in\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"user_name\"},\"queryOperator\":\"between\",\"type\":\"field\",\"value\":\"my_user_name1,my_user_name2\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"date_time_unix\"},\"queryOperator\":\"between\",\"type\":\"field\",\"value\":\"1414360799,1414360800\",\"valueType\":\"DATE_TIME\"},{\"field\":{\"id\":\"user_name\"},\"queryOperator\":\"endsWith\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"user_name\"},\"queryOperator\":\"startsWith\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"user_name\"},\"queryOperator\":\"contains\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"hasValue\",\"type\":\"field\"},{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"hasNoValue\",\"type\":\"field\"},{\"field\":{\"id\":\"user_name\"},\"queryOperator\":\"hasNoValue\",\"type\":\"field\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"user_name\"},\"queryOperator\":\"hasValue\",\"type\":\"field\",\"valueType\":\"STRING\"}]}}";
    protected static String bug_FV_5557DTO_Json = "{ \"fields\": [], \"entities\": [ \"ssh\" ], \"sort\": [ { \"field\": { \"id\": \"event_time\" }, \"direction\": \"DESC\" } ], \"conditions\": { \"type\": \"term\", \"logicalOperator\": \"AND\", \"terms\": [ { \"field\": { \"id\": \"event_score\" }, \"queryOperator\": \"between\", \"type\": \"field\", \"value\": \"20,100\", \"valueType\": \"NUMBER\" } ] }, \"limit\": 20, \"offset\": 0 }";
    protected static String bug_FV_5557_top_DTO_Json = "{ \"fields\": [], \"entities\": [ \"ssh\" ], \"sort\": [ { \"field\": { \"id\": \"event_time\" }, \"direction\": \"DESC\" } ], \"conditions\": { \"type\": \"term\", \"logicalOperator\": \"AND\", \"terms\": [ { \"field\": { \"id\": \"event_score\" }, \"queryOperator\": \"between\", \"type\": \"field\", \"value\": \"50,100\", \"valueType\": \"NUMBER\" } ] }, \"limit\": 20, \"offset\": 0 }";
    protected static String betweenPartitionDTOJson = "{\"entities\":[\"kerberos_logins\"],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"between\",\"type\":\"field\",\"value\":\"1414184400,1414360799\",\"valueType\":\"STRING\"}]}}";
    protected static String joinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418209915,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418296315,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"join\":[{\"type\":\"RIGHT\",\"entity\":\"users\",\"left\":{\"entity\":\"kerberos_logins\",\"field\":\"normalized_username\"},\"right\":{\"entity\":\"users\",\"field\":\"normalized_username\"}}],\"sort\":[],\"limit\":50,\"offset\":0}";
    protected static String noJoinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418209915,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"queryOperator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418296315,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[],\"limit\":50,\"offset\":0}";
    protected static String tokenizedExpressionJson = "{\"fields\":[{\"entity\":\"vpn_session\",\"allFields\":true},{\"entity\":\"users\",\"allFields\":true}],\"entities\":[\"vpn_session\"],\"sort\":[{\"field\":{\"id\":\"start_time\"},\"direction\":\"DESC\"}],\"conditions\":{\"type\":\"term\",\"logicalOperator\":\"AND\",\"terms\":[{\"field\":{\"entity\":\"vpn_session\",\"id\":\"session_time_utc\"},\"queryOperator\":\"between\",\"type\":\"field\",\"value\":\"1427407200,1430168399\"},{\"field\":{\"entity\":\"vpn_session\",\"id\":\"session_score\"},\"queryOperator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50}]},\"limit\":20,\"offset\":0,\"join\":[{\"type\":\"LEFT\",\"entity\":\"users\",\"left\":{\"entity\":\"vpn_session\",\"field\":\"normalized_username\"},\"right\":{\"entity\":\"users\",\"field\":\"normalized_username\"}}]}";

    protected DataQueryDTOImpl dataQueryDTO1;
    protected DataQueryDTOImpl complexWhereDTO;
    protected DataQueryDTOImpl bug_FV_5557DTO;
    protected DataQueryDTOImpl bug_FV_5557_top_DTO;
    protected DataQueryDTOImpl betweenPartitionDTO;
    protected DataQueryDTOImpl joinDTO;
    protected DataQueryDTOImpl noJoinDTO;
    protected DataQueryDTOImpl dataQueryDto_UnionDistinct;
    protected DataQueryDTOImpl tokenizedExpression;

    protected QueryPartGenerator generator;
    protected ObjectMapper mapper = new ObjectMapper();
    protected DataEntitiesConfig dataEntitiesConfig;
    protected MySqlFieldGenerator mySqlFieldGenerator;
    protected DataQueryDtoHelper dataQueryDtoHelper;

    public void setUp()
            throws Exception {
        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTOImpl.class);
        complexWhereDTO = mapper.readValue(complexWhereDTOJson, DataQueryDTOImpl.class);
        bug_FV_5557DTO = mapper.readValue(bug_FV_5557DTO_Json, DataQueryDTOImpl.class);
        bug_FV_5557_top_DTO = mapper.readValue(bug_FV_5557_top_DTO_Json, DataQueryDTOImpl.class);
        betweenPartitionDTO = mapper.readValue(betweenPartitionDTOJson, DataQueryDTOImpl.class);
        joinDTO = mapper.readValue(joinDTOJson, DataQueryDTOImpl.class);
        noJoinDTO = mapper.readValue(noJoinDTOJson, DataQueryDTOImpl.class);
        dataQueryDto_UnionDistinct = mapper.readValue(subQueryUnionDistinctDtoJson, DataQueryDTOImpl.class);
        tokenizedExpression = mapper.readValue(tokenizedExpressionJson, DataQueryDTOImpl.class);

        dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);
        mySqlFieldGenerator = Mockito.mock(MySqlFieldGenerator.class);
        dataQueryDtoHelper = Mockito.mock(DataQueryDtoHelper.class);

        mySqlFieldGenerator.setDataEntitiesConfig(dataEntitiesConfig);
        mySqlFieldGenerator.setDataQueryDtoHelper(dataQueryDtoHelper);

        Mockito.when(dataQueryDtoHelper.getEntityId(Mockito.any(DataQueryDTOImpl.class))).thenReturn("kerberos_logins");

        if (generator != null) {
            generator.setDataEntitiesConfig(dataEntitiesConfig);
            generator.setMySqlFieldGenerator(mySqlFieldGenerator);
            generator.setDataQueryDtoHelper(dataQueryDtoHelper);
        }
    }
}
