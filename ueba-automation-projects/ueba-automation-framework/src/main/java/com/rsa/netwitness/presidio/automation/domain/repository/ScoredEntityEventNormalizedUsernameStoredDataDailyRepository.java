package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.output.ScoredEntityEventNormalizedUsernameDailyStoredData;

import java.util.List;


public interface ScoredEntityEventNormalizedUsernameStoredDataDailyRepository extends MongoRepository<ScoredEntityEventNormalizedUsernameDailyStoredData, String> {

    @Query("{ 'contextId': ?0 }")
    List<ScoredEntityEventNormalizedUsernameDailyStoredData> findByUser(String username);
}
