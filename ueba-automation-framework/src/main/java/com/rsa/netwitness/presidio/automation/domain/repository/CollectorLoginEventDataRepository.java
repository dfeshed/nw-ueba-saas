package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.rsa.netwitness.presidio.automation.domain.activedirectory.CollectorActiveDirectoryStoredData;

public interface CollectorLoginEventDataRepository extends MongoRepository<CollectorActiveDirectoryStoredData, String> {

}
