package fortscale.collection.jobs.notifications;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.common.dataqueries.querydto.*;
import fortscale.common.dataqueries.querygenerators.DataQueryRunner;
import fortscale.common.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.ApplicationConfigurationService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Creds share notification does the following:
 * It queries the relevant data source for multiple concurrent session of the same user from different hostnames.
 * if more than X concurrent sessions have interception with the same session S1, then a notification would be created,
 * and its anomaly value would be the session S1.
 * In the supporting information field, the raw events would be written - those are the session S2, S3 ,... Sx that
 * intercepted with session S1.
 *
 * example:
 * S1: a vpn session from 14:00 till 23:00
 * S2: a vpn session from 14:05 till 15:00
 * S3: a vpn session from 15:05 till 16:00
 * S4: a vpn session from 15:10 till 16:10
 * S5: a vpn session from 20:00 till 22:00
 * X: 4 (number of concurrent sessions needed to create creds share notification)
 *
 * then a creds share notification would be created, and its anomaly value would be S1
 *
 * Created by galiar on 01/03/2016.
 */
public class VpnCredsShareNotificationJob extends FortscaleJob {

    private static Logger logger = LoggerFactory.getLogger(VpnCredsShareNotificationJob.class);
    private static final String LASTEST_TS = "creds_share_notification_latest_ts";
    private static final String MIN_DATE_TIME_FIELD = "min_ts";

    private static final int WEEK_IN_SECONDS = 604800;
    private static final int DAY_IN_SECONDS = 86400;
    @Autowired
    ApplicationConfigurationService applicationConfigurationService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    DataQueryHelper dataQueryHelper;
    @Autowired
    protected DataQueryRunnerFactory dataQueryRunnerFactory;


    String tableName;
    long latestTimestamp;
    long currentTimestamp;
    String sourceName;
    String jobName;

    @Override
    protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {

        // params: table names, field names: field names for the select phrase and also for the conditions phrase,
        // hostname manipulation condition, number of concurrent session. those parameters should be saved in mongo.

        //get table name

        //host name condition

        //number of concurrent sessions


        // get the job group name to be used using monitoring
        sourceName = context.getJobDetail().getKey().getGroup();
        jobName = context.getJobDetail().getKey().getName();


    }

    @Override
    protected int getTotalNumOfSteps() {
        //1. get the last run time. 2. query. 3. query supporting information. 4. send to kafka
        return 4;
    }

    @Override
    protected boolean shouldReportDataReceived() {
        return false; //TODO
    }


    //fetch the last time this job has run
    //query the creds share notification out of the relevant hdfs table (currently supporting only vpn_session)
    //query for additional information - all the raw events
    //send the notification to evidence creation task
    @Override
    protected void runSteps() throws Exception {

        logger.info("{} {} job started", jobName, sourceName);

        startNewStep("Get the latest run time");
         boolean tableHasData = figureLatestRunTime();
        if(!tableHasData){
            return;
        }

        finishStep();

            startNewStep("Query impala for creds share notifications");

        while(latestTimestamp <= currentTimestamp){

         //one day a time
         long upperLimit = latestTimestamp + DAY_IN_SECONDS;

         //create ConditionTerm for the hostname condition

         //create dataQuery for the overlapping sessions

         //run the query
        }
        /*

			hostname_condition = "CASE WHEN ((lpad(t1.hostname, instr(t1.hostname, '-')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '-')-1, '')) or (lpad(t1.hostname, instr(t1.hostname, '-')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '@')-1, '')) or (lpad(t1.hostname, instr(t1.hostname, '@')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '-')-1, '')) or (lpad(t1.hostname, instr(t1.hostname, '@')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '@')-1, ''))) is null then false else  (lpad(t1.hostname, instr(t1.hostname, '-')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '-')-1, '')) or (lpad(t1.hostname, instr(t1.hostname, '-')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '@')-1, '')) or (lpad(t1.hostname, instr(t1.hostname, '@')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '-')-1, '')) or (lpad(t1.hostname, instr(t1.hostname, '@')-1, '') !=  lpad(t2.hostname, instr(t2.hostname, '@')-1, '')) end"

			find_overlapping_sessions_query = "select username ,normalized_username,id, hostname, count(*) as cnt_session_with_overlap_sessions,min(start_session_time) start_time ,max(end_session_time) end_time from (select t1.username,t1.normalized_username,t1.hostname, u.id, unix_timestamp(seconds_sub(t1.date_time, t1.duration)) as start_session_time ,unix_timestamp(t1.date_time) as end_session_time  from vpnsessiondatares t1 inner join vpnsessiondatares t2 on t1.username = t2.username and t1.source_ip!=t2.source_ip  and  seconds_sub(t2.date_time,t2.duration) between seconds_sub(t1.date_time,t1.duration) and t1.date_time  inner join users u on t1.normalized_username = u.username where t1.source_ip !='' and t2.source_ip !='' and t1.country = 'Reserved Range' and t2.country='Reserved Range' and #{hostname_condition} and t1.date_time_unix >= #{@latest_ts} and t1.date_time_unix < #{upper_limit} group by t1.username,t1.normalized_username,t1.hostname,t1.source_ip ,seconds_sub(t1.date_time, t1.duration) ,t1.date_time,u.id having count(t2.source_ip) >=4  ) t group by username,normalized_username,hostname,id"

			connection.query(find_overlapping_sessions_query).each do |x|
				username = x[:username]
				sessions_cnt = x[:cnt_session_with_overlap_sessions]
				normalized_username = x[:normalized_username]
				userid = x[:id]
				start_time = x[:start_time]
				end_time = x[:end_time]

				raw_events = []


			@latest_ts = upper_limit
			coonIterator= coonIterator+1
		end

		stateful_write( { latest_ts: current_ts })
		results

		rescue Exception => ex
			puts "An error of type #{ex.class} happened, message is #{ex.message}"
		end
	end

         */

            finishStep();

            startNewStep("Query impala for supporting information - raw events");
            //do stuff
        /*
        add_supporting_information_query = "select * from vpnsessiondatares where username='#{username}' and date_time_unix>=#{start_time} and date_time_unix<=#{end_time}"
				connection.query(add_supporting_information_query).each do |y|
					raw_events << y
				end

				results << [{name: username,
					displayName: normalized_username,
					dataSource: "vpn_session",
					ts: @latest_ts,
					fsId: userid,
					attributes: { username: username,start_date: start_time,end_date: end_time ,sessions_cnt: sessions_cnt, raw_events: raw_events},
					index: "vpn_creds_share::#{username}::#{start_time}::#{end_time}"
					},
					:VPN_user_creds_share]

			end
         */
            finishStep();

            startNewStep("Sends the results to evidence creation task");
            //do stuff
            finishStep();


            logger.info("{} {} job finished", jobName, sourceName);


        }

    private boolean figureLatestRunTime() throws InvalidQueryException {
        //read latestTimestamp from mongo collection application_configuration
        latestTimestamp = Long.parseLong(applicationConfigurationService.getApplicationConfigurationByKey(LASTEST_TS).getValue());
        if (StringUtils.isEmpty(latestTimestamp)) {

            //create query to find the earliest event
            DataQueryDTO dataQueryDTO = dataQueryHelper.createDataQuery("vpn_session", "", null, null, -1, DataQueryDTOImpl.class);
            DataQueryField countField = dataQueryHelper.createMinFunc("date_time", MIN_DATE_TIME_FIELD);
            dataQueryHelper.setFuncFieldToQuery(countField, dataQueryDTO);
            DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
            String query = dataQueryRunner.generateQuery(dataQueryDTO);
            logger.info("Running the query: {}", query);
            // execute Query
            List<Map<String, Object>> queryList = dataQueryRunner.executeQuery(query);
            if (queryList.isEmpty()) {
                //no data in table
                logger.info("Table is empty. Quit...");
                return false;
            }

            long earliestEventTimestamp = extractEarliestEventFromDataQueryResult(queryList);
            currentTimestamp = System.currentTimeMillis();
            latestTimestamp = Math.min(earliestEventTimestamp, currentTimestamp - WEEK_IN_SECONDS);
            logger.info("latest run time was empty - setting latest timestamp to {}",latestTimestamp);
        }
        return true;
    }

    private long extractEarliestEventFromDataQueryResult(List<Map<String, Object>> queryList) {
        for(Map<String, Object>  resultPair: queryList){
            if(resultPair.get(MIN_DATE_TIME_FIELD) != null){
                return Long.parseLong(resultPair.get(MIN_DATE_TIME_FIELD).toString());
            }
        }

        return 0;
    }
}