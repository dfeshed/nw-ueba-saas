package com.rsa.netwitness.presidio.automation.mongo;

import com.rsa.netwitness.presidio.automation.domain.output.SmartJa3Hourly;
import com.rsa.netwitness.presidio.automation.domain.output.SmartSslSubjectHourly;
import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Date.from;
import static java.util.stream.Collectors.toSet;

public class SmartHourlyEntitiesHelper {

    private MongoTemplate mongoTemplate;
    private final Query QUERY;

    public SmartHourlyEntitiesHelper(MongoTemplate mongoTemplate, int queryStartDay, int queryEndDay) {
        this.mongoTemplate = mongoTemplate;
        this.QUERY = buildTimeQuery("startInstant", queryStartDay, queryEndDay);
    }

    public SmartHourlyEntitiesHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.QUERY = emptyQuery();
    }

    public Set<String> getAllEntities() {
        return Stream.of(
                getEntitiesUserId().stream(),
                getEntitiesSslSubject().stream(),
                getEntitiesJa3().stream()
        ).flatMap(e -> e).collect(Collectors.toSet());
    }


    public Set<String> getEntitiesUserId() {
        List<SmartUserIdStoredRecored> smartEntitiesRecords = mongoTemplate.find(QUERY, SmartUserIdStoredRecored.class);

        return smartEntitiesRecords.stream()
                .map(e -> e.getContext().getUserId())
                .collect(toSet());
    }

    public Set<String> getEntitiesSslSubject() {
        List<SmartSslSubjectHourly> smartEntitiesRecords = mongoTemplate.find(QUERY, SmartSslSubjectHourly.class);

        return smartEntitiesRecords.stream()
                .map(e -> e.getContext().getSslSubject())
                .collect(toSet());
    }

    public Set<String> getEntitiesJa3() {
        List<SmartJa3Hourly> smartEntitiesRecords = mongoTemplate.find(QUERY, SmartJa3Hourly.class);

        return smartEntitiesRecords.stream()
                .map(e -> e.getContext().getJa3())
                .collect(toSet());
    }

    public String getQuery(){
        return QUERY.toString();
    }

    private Query emptyQuery() {
        return new Query();
    }

    private Query buildTimeQuery(String fieldToSelect, int startDay, int endDay) {
        Query usersQuery = new Query();
        return usersQuery.addCriteria(Criteria.where(fieldToSelect)
                .lte(from(now().minus(endDay, DAYS)))
                .gte(from(now().minus(startDay, DAYS)))
        );
    }
}
