package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.filter.AdaprerFilteredRawEventStoredData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdapterInvalidRawEventStoreDataRepository extends MongoRepository<AdaprerFilteredRawEventStoredData, String> {

}
