package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.Instant;

@NoRepositoryBean
public interface AdapterAbstractStoredDataRepository<T, ID>  extends MongoRepository<T, ID> {

    String getName();
    Instant maxDateTimeBetween(Instant start, Instant end);
    long countByTime(Instant start, Instant end);

}
