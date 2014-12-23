package fortscale.services.dataqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
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
    protected static String joinDTOJson = "{\"fields\":[{\"entity\":\"kerberos_logins\",\"allFields\":true},{\"entity\":\"users\",\"id\":\"displayName\"},{\"entity\":\"users\",\"id\":\"id\"},{\"entity\":\"users\",\"id\":\"is_user_administrator\"},{\"entity\":\"users\",\"id\":\"is_user_executive\"},{\"entity\":\"users\",\"id\":\"accountIsDisabled\"},{\"entity\":\"users\",\"id\":\"is_user_service\"},{\"entity\":\"users\",\"id\":\"followed\"}],\"conditions\":{\"type\":\"term\",\"operator\":\"AND\",\"terms\":[{\"field\":{\"id\":\"event_score\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":50,\"valueType\":\"NUMBER\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"greaterThanOrEquals\",\"type\":\"field\",\"value\":1418209915,\"valueType\":\"STRING\"},{\"field\":{\"id\":\"event_time_utc\"},\"operator\":\"lesserThanOrEquals\",\"type\":\"field\",\"value\":1418296315,\"valueType\":\"STRING\"}]},\"entities\":[\"kerberos_logins\"],\"join\":[{\"type\":\"RIGHT\",\"entity\":\"users\",\"left\":{\"entity\":\"kerberos_logins\",\"field\":\"normalized_username\"},\"right\":{\"entity\":\"users\",\"field\":\"normalized_username\"}}],\"sort\":[],\"limit\":50,\"offset\":0}";

    protected DataQueryDTO dataQueryDTO1;
    protected DataQueryDTO joinDTO;
    protected DataQueryDTO dataQueryDto_UnionDistinct;

    protected QueryPartGenerator<T> generator;
    protected ObjectMapper mapper = new ObjectMapper();
    protected DataEntitiesConfig dataEntitiesConfig;
    protected MySqlFieldGenerator mySqlFieldGenerator;

    public void setUp()
            throws Exception {
        dataQueryDTO1 = mapper.readValue(dto1, DataQueryDTO.class);
        joinDTO = mapper.readValue(joinDTOJson, DataQueryDTO.class);
        dataQueryDto_UnionDistinct = mapper.readValue(subQueryUnionDistinctDtoJson, DataQueryDTO.class);

        dataEntitiesConfig = Mockito.mock(DataEntitiesConfig.class);
        mySqlFieldGenerator = Mockito.mock(MySqlFieldGenerator.class);
        mySqlFieldGenerator.setDataEntitiesConfig(dataEntitiesConfig);

        if (generator != null) {
            generator.setDataEntitiesConfig(dataEntitiesConfig);
            generator.setMySqlFieldGenerator(mySqlFieldGenerator);
        }
    }
}
