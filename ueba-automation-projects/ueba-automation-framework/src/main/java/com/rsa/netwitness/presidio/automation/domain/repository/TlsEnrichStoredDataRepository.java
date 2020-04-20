package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.tls.TlsEnrichStoredData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;


public interface TlsEnrichStoredDataRepository extends MongoRepository<TlsEnrichStoredData, String> {

    @Query ("{ 'startInstant': { $gte: ?0 }, $and: [ { 'startInstant': { $lt: ?1 } } ] }")
    List<TlsEnrichStoredData> findByTime(Instant start, Instant end);

    @Query("{'eventId': {$regex: ?0 }})")
    List<TlsEnrichStoredData> findByIdContains(String substring);

    @Query("{'dstOrg.name': {$regex: ?0 }})")
    List<TlsEnrichStoredData> findByDstOrgContains(String substring);

}
