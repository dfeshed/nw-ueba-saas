package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.output.ScoredEntityEventNormalizedUsernameHourlyStoredData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface ScoredEntityEventNormalizedUsernameStoredDataHourlyRepository extends MongoRepository<ScoredEntityEventNormalizedUsernameHourlyStoredData, String> {

    @Query("{ 'contextId': ?0 }")
    List<ScoredEntityEventNormalizedUsernameHourlyStoredData> findByUser(String username);
}
