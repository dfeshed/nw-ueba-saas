package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.rsa.netwitness.presidio.automation.domain.dlpfile.DlpFileStoredData;


public interface DlpFileBaseDataRepository extends MongoRepository<DlpFileStoredData, String> {
}
