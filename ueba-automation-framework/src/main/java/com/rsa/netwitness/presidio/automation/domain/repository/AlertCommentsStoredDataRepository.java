package com.rsa.netwitness.presidio.automation.domain.repository;

import com.rsa.netwitness.presidio.automation.domain.output.AlertCommentRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AlertCommentsStoredDataRepository  extends MongoRepository<AlertCommentRecord, String> {

    @Query("{ '_class' : { $eq: ?0} }")
    List<AlertCommentRecord> findByClass(String className);

    @Query("{ '_class' : { $eq: ?0}, $and: [ { 'alertId': { $eq: ?1 } } ] }")
    List<AlertCommentRecord> findByClassAndAlertId(String className, String alertId);

    @Query("{ 'alertId': { $eq: ?0 } }")
    List<AlertCommentRecord> findByAlertId(String alertId);

    //@Query("{ 'Id' : { $eq: ?0}, $and: [ { 'alertId': { $eq: ?1 } } ] }")
    @Query("{ 'alertId': { $eq: ?1}, $and: [ { '_id': { $eq: ?0} } ] }")
    List<AlertCommentRecord> findCommentIdAndAlertId(Object commentId, String alertId);

    @Query("{ '_id': { $eq: ?0} }")
    List<AlertCommentRecord> findByCommentId(Object commentId);

}
