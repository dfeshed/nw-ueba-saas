package com.rsa.netwitness.presidio.automation.mongo;

import com.mongodb.client.DistinctIterable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testng.collections.Sets;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SmartEntitiesHelper {

    private MongoTemplate mongoTemplate;

    public SmartEntitiesHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Set<String> getAllEntities() {
        return Stream.of(
                getActiveDirectoryUsers().stream(),
                getAuthenticationUsers().stream(),
                getFileUsers().stream(),
                getProcessUsers().stream(),
                getRegistryUsers().stream(),
                getTlsSslSubject().stream(),
                getTlsJa3().stream()
        ).flatMap(e -> e).collect(Collectors.toSet());
    }

    public Set<String> getActiveDirectoryUsers() {
        return getDistinctEntityNames("output_active_directory_enriched_events", "userName");
    }

    public Set<String> getAuthenticationUsers() {
        return getDistinctEntityNames("output_authentication_enriched_events", "userName");
    }

    public Set<String> getFileUsers() {
        return getDistinctEntityNames("output_file_enriched_events", "userName");
    }

    public Set<String> getProcessUsers() {
        return getDistinctEntityNames("output_process_enriched_events", "userName");
    }

    public Set<String> getRegistryUsers() {
        return getDistinctEntityNames("output_registry_enriched_events", "userName");
    }

    public Set<String> getTlsSslSubject() {
        return getDistinctEntityNames("output_tls_enriched_events", "sslSubject");
    }

    public Set<String> getTlsJa3() {
        return getDistinctEntityNames("output_tls_enriched_events", "ja3");
    }


    private Set<String> getDistinctEntityNames(String collectionName, String entityFieldName) {
        Set<String> DistinctUserNamesList = Sets.newHashSet();

//        Query usersQuery = new Query();
//        usersQuery.addCriteria(Criteria.where("eventDate").lte(Date.from(lastInstant)).gte(Date.from(firstInstant)));


        DistinctIterable<String> userName = mongoTemplate.getCollection(collectionName).distinct(entityFieldName,String.class);
        userName.iterator().forEachRemaining(DistinctUserNamesList::add);
        return DistinctUserNamesList ;
    }



}
