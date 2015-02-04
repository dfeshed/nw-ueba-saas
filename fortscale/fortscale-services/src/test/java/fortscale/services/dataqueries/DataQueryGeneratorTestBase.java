package fortscale.services.dataqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryDtoHelper;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlFieldGenerator;
import org.mockito.Mockito;

/**
 * Created by Yossi on 23/12/2014.
 * Base class for testing generators
 */
public class DataQueryGeneratorTestBase<T> {
    protected static String subQueryUnionDistinctDtoJson = "{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\",\"func\":{\"name\":\"count\",\"params\":{\"distinct\":\"true\"}}},{\"alias\":\"max_score\",\"id\":\"event_score\",\"func\":{\"name\":\"max\"}},{\"alias\":\"average_score\",\"id\":\"event_score\",\"func\":{\"name\":\"avg\"}},{\"alias\":\"ssh_count\",\"func\":{\"name\":\"sum\",\"params\":{\"field\":\"ssh_count\"}}},{\"alias\":\"kerberos_count\",\"func\":{\"name\":\"sum\",\"params\":{\"field\":\"kerberos_count\"}}}],\"entities\":[],\"sort\":[{\"field\":{\"id\":\"destination_machine\"}}],\"limit\":50,\"offset\":0,\"subQuery\":{\"combineMethod\":\"UnionDistinct\",\"dataQueries\":[{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\"},{\"id\":\"event_score\"}],\"entities\":[\"kerberos_logins\"],\"sort\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"is_user_administrator\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"},{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418673600,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1419278400,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"is_sensitive_machine\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"}]}},{\"fields\":[{\"id\":\"destination_machine\"},{\"id\":\"normalized_username\"},{\"id\":\"event_score\"}],\"entities\":[\"ssh\"],\"sort\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"is_user_administrator\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"},{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418673600,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1419278400,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"is_sensitive_machine\"},\"operator\":\"equals\",\"type\":\"field\",\"value\":true,\"valueType\":\"BOOLEAN\"}]}}]},\"groupBy\":[{\"id\":\"destination_machine\"}]}";
    protected static String dto1 = "{\"fields\":[],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[{\"field\":{\"id\":\"event_score\"},\"direction\":\"DESC\"},{\"field\":{\"id\":\"event_time\"},\"direction\":\"DESC\"}],\"limit\":20,\"offset\":0}";
    protected static String complexWhereDTOJson = "{\"entities\":[\"kerberos_logins\"],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":\"1414184400\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":\"1414360799\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_score\"},\"operator\":\"in\",\"type\":\"field\",\"value\":\"50,70\",\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"user_name\"},\"operator\":\"in\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"user_name\"},\"operator\":\"between\",\"type\":\"field\",\"value\":\"my_user_name1,my_user_name2\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"date_time_unix\"},\"operator\":\"between\",\"type\":\"field\",\"value\":\"1414360799,1414360800\",\"valueType\":\"DATE_TIME\"},{\"field\":{\"id\":\"user_name\"},\"operator\":\"endsWith\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"user_name\"},\"operator\":\"startsWith\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"user_name\"},\"operator\":\"contains\",\"type\":\"field\",\"value\":\"my_user_name\",\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_score\"},\"operator\":\"hasValue\",\"type\":\"field\"},{\"field\":{\"id\":\"event_score\"},\"operator\":\"hasNoValue\",\"type\":\"field\"}]}}";
    protected static String bug_FV_5557DTO_Json = "{ \"fields\": [], \"entities\": [ \"ssh\" ], \"sort\": [ { \"field\": { \"id\": \"event_time\" }, \"direction\": \"DESC\" } ], \"conditions\": { \"type\": \"term\", \"operator\": \"AND\", \"terms\": [ { \"field\": { \"id\": \"event_score\" }, \"operator\": \"between\", \"type\": \"field\", \"value\": \"20,100\", \"valueType\": \"NUMBER\" } ] }, \"limit\": 20, \"offset\": 0 }";
    protected static String bug_FV_5557_top_DTO_Json = "{ \"fields\": [], \"entities\": [ \"ssh\" ], \"sort\": [ { \"field\": { \"id\": \"event_time\" }, \"direction\": \"DESC\" } ], \"conditions\": { \"type\": \"term\", \"operator\": \"AND\", \"terms\": [ { \"field\": { \"id\": \"event_score\" }, \"operator\": \"between\", \"type\": \"field\", \"value\": \"50,100\", \"valueType\": \"NUMBER\" } ] }, \"limit\": 20, \"offset\": 0 }";
    protected static String betweenPartitionDTOJson = "{\"entities\":[\"kerberos_logins\"],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"between\",\"type\":\"field\",\"value\":\"1414184400,1414360799\",\"valueType\":\"STRING\"}]}}";
    protected static String joinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418209915,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418296315,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"join\":[{\"type\":\"RIGHT\",\"entity\":\"users\",\"left\":{\"entity\":\"kerberos_logins\",\"field\":\"normalized_username\"},\"right\":{\"entity\":\"users\",\"field\":\"normalized_username\"}}],\"sort\":[],\"limit\":50,\"offset\":0}";
    protected static String noJoinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418209915,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418296315,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"sort\":[],\"limit\":50,\"offset\":0}";

    protected DataQueryDTO dataQueryDTO1;
    protected DataQueryDTO complexWhereDTO;
    protected DataQueryDTO bug_FV_5557DTO;
    protected DataQueryDTO bug_FV_5557_top_DTO;
    protected DataQueryDTO betweenPartitionDTO;
    protected DataQueryDTO joinDTO;
    protected DataQueryDTO noJoinDTO;
    protected DataQueryDTO dataQueryDto_UnionDistinct;

    protected QueryPartGenerator generator;
    protected ObjectMapper mapper = new ObjectMapper();
    protected DataEntitiesConfig dataEntitiesConfig;
    protected MySqlFieldGenerator mySqlFieldGenerator;
    protected DataQueryDtoHelper dataQueryDtoHelper;

    public void setUp()
            throws Exception {
        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);
        complexWhereDTO = mapper.readValue(complexWhereDTOJson, DataQueryDTO.class);
        bug_FV_5557DTO = mapper.readValue(bug_FV_5557DTO_Json, DataQueryDTO.class);
        bug_FV_5557_top_DTO = mapper.readValue(bug_FV_5557_top_DTO_Json, DataQueryDTO.class);
        betweenPartitionDTO = mapper.readValue(betweenPartitionDTOJson, DataQueryDTO.class);
        joinDTO = mapper.readValue(joinDTOJson, DataQueryDTO.class);
        noJoinDTO = mapper.readValue(noJoinDTOJson, DataQueryDTO.class);
        dataQueryDto_UnionDistinct = mapper.readValue(subQueryUnionDistinctDtoJson, DataQueryDTO.class);

        dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);
        mySqlFieldGenerator = Mockito.mock(MySqlFieldGenerator.class);
        dataQueryDtoHelper = Mockito.mock(DataQueryDtoHelper.class);

        mySqlFieldGenerator.setDataEntitiesConfig(dataEntitiesConfig);
        mySqlFieldGenerator.setDataQueryDtoHelper(dataQueryDtoHelper);

        Mockito.when(dataQueryDtoHelper.getEntityId(Mockito.any(DataQueryDTO.class))).thenReturn("kerberos_logins");

        if (generator != null) {
            generator.setDataEntitiesConfig(dataEntitiesConfig);
            generator.setMySqlFieldGenerator(mySqlFieldGenerator);
            generator.setDataQueryDtoHelper(dataQueryDtoHelper);
        }
    }
}
