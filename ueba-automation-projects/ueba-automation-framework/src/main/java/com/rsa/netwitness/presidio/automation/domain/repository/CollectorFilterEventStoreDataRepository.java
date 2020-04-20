package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.rsa.netwitness.presidio.automation.domain.filter.CollectorFilterStoredData;

public interface CollectorFilterEventStoreDataRepository extends MongoRepository<CollectorFilterStoredData, String> {

}
