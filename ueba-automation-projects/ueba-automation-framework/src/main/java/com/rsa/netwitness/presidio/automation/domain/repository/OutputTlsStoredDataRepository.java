package com.rsa.netwitness.presidio.automation.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.rsa.netwitness.presidio.automation.domain.file.OutputFileEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.tls.OutputTlsEnrichStoredData;

import java.time.Instant;
import java.util.List;


public interface OutputTlsStoredDataRepository extends MongoRepository<OutputTlsEnrichStoredData, String> {

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lte : ?1 } }, { $and: [ { 'sslSubject': ?2 } ] } ] }")
    List<OutputFileEnrichStoredData> findByTimeAndSslSubject(Instant start, Instant end, String sslSubject);

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lte : ?1 } }, { $and: [ { 'ja3': ?2 } ] } ] }")
    List<OutputFileEnrichStoredData> findByTimeAndJa3(Instant start, Instant end, String ja3);

    @Query ("{ 'eventDate': { $gte: ?0 }, $and: [ { 'eventDate': { $lt: ?1 } } ] }")
    List<OutputFileEnrichStoredData> findByTime(Instant start, Instant end);

    @Query (value = "{}", count=true )
    Long countAll();
}
