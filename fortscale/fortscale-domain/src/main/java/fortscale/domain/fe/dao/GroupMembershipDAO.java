package fortscale.domain.fe.dao;

import fortscale.domain.impala.ImpalaDAO;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by idanp on 7/27/2014.
 */
public class GroupMembershipDAO extends ImpalaDAO<Map<String, Object>> {

    private static Logger logger = Logger.getLogger(AccessDAO.class);

    protected static final String EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME = "day";
    protected static final String EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME = "status";
    protected static final String EVENT_LOGIN_DAY_COUNT_COUNT_FIELD_NAME = "eventcount";
    protected static final String EVENT_SOURCE_COALESCED_FIELD_NAME = "source";

    @Value("${impala.ldap.group.membership.scores.table.name}")
    public String NORMALIZED_USERNAME;

    private Date lastRunDate = null;

    @Value("${impala.ldap.group.membership.scores.table.name}")
    private String tableName;

    @Value("${impala.ldap.group.membership.scores.table.fields.timeStamp}")
    private String timeStamp;

    @Value("${impala.ldap.group.membership.scores.table.fields}")
    private String impalaMemberShipTableFields;



    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getInputFileHeaderDesc() {
        return impalaMemberShipTableFields;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Long getLastRuntime() {
        if (lastRunDate == null) {
            Calendar tmp = Calendar.getInstance();
            tmp.add(Calendar.DAY_OF_MONTH, -1);
            lastRunDate = new Date(tmp.getTimeInMillis());
        }
        String query = String.format("select max(%s) from %s",
                getTimeStamp(), getTableName());
        String queryWithHint = String.format("%s where %s >=' %s'", query,
                getTimeStamp(), new Timestamp(lastRunDate.getTime() / 1000));
        Long lastRun = impalaJdbcTemplate.queryForObject(queryWithHint,
                Long.class);
        if (lastRun == null) {
            lastRun = impalaJdbcTemplate.queryForObject(query, Long.class);
        }

        return lastRun;
    }


}
