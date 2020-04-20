package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.dlpfile.DlpFileStoredData;

import java.time.Instant;
import java.util.List;


public interface DlpFileStoredDataRepository  extends MongoRepository<DlpFileStoredData, String> {

    @Query("{ 'dateTime' : { $gte: ?0, $lt: ?1} }")
    List<DlpFileStoredData> findByUsername(Instant start, Instant end, String username);
}
