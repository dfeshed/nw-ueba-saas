package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.rsa.netwitness.presidio.automation.domain.file.NetwitnessFileStoredData;

import java.time.Instant;

public interface NetwitnessFileStoredDataRepository extends MongoRepository<NetwitnessFileStoredData, String> {

    @CountQuery("{ 'netwitnessEvent.mongo_source_event_time': { $gte: ?0 }, $and: [ { 'netwitnessEvent.mongo_source_event_time': { $lt: ?1 } } ] }")
    long findByTime(Instant start, Instant end);
}
