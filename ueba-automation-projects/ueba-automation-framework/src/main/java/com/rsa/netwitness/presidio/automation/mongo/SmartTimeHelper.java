package com.rsa.netwitness.presidio.automation.mongo;

import com.rsa.netwitness.presidio.automation.domain.output.SmartJa3Hourly;
import com.rsa.netwitness.presidio.automation.domain.output.SmartSslSubjectHourly;
import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class SmartTimeHelper {

    private MongoTemplate mongoTemplate;

    public SmartTimeHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Instant getFirstSmartUserIdHourly() {
        return getUserIdRecord(getFirstSmartUserIdQuery()).getStartInstant();
    }

    public Instant getLastSmartUserIdHourly() {
        return getUserIdRecord(getLastSmartUserIdQuery()).getStartInstant();
    }

    public Instant getFirstSmartSslSubjectHourly() {
        return getSslSubjectRecord(getFirstSmartUserIdQuery()).getStartInstant();
    }

    public Instant getLastSmartSslSubjectHourly() {
        return getSslSubjectRecord(getLastSmartUserIdQuery()).getStartInstant();
    }

    public Instant getFirstSmartJa3Hourly() {
        return getJa3Record(getFirstSmartUserIdQuery()).getStartInstant();
    }

    public Instant getLastSmartJa3Hourly() {
        return getJa3Record(getLastSmartUserIdQuery()).getStartInstant();
    }



    private Query getFirstSmartUserIdQuery(){
        Query queryFirstDate = new Query();
        queryFirstDate.with(new Sort(Sort.Direction.ASC, "startInstant"));
        queryFirstDate.limit(1);
        return queryFirstDate;
    }

    private Query getLastSmartUserIdQuery(){
        Query queryLastDate = new Query();
        queryLastDate.with(new Sort(Sort.Direction.DESC, "startInstant"));
        queryLastDate.limit(1);
        return queryLastDate;
    }

    private SmartUserIdStoredRecored getUserIdRecord(Query query){
        SmartUserIdStoredRecored record =  mongoTemplate.findOne(query, SmartUserIdStoredRecored.class);
        assertThat(record).isNotNull();
        return record;
    }

    private SmartSslSubjectHourly getSslSubjectRecord(Query query){
        SmartSslSubjectHourly record =  mongoTemplate.findOne(query, SmartSslSubjectHourly.class);
        assertThat(record).isNotNull();
        return record;
    }

    private SmartJa3Hourly getJa3Record(Query query){
        SmartJa3Hourly record =  mongoTemplate.findOne(query, SmartJa3Hourly.class);
        assertThat(record).isNotNull();
        return record;
    }
}
