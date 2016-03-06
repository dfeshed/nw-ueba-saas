package fortscale.streaming.service.vpn;

import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.dao.EvidencesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by shays on 06/03/2016.
 */
public class MockEvidencesRepository implements EvidencesRepository {


    @Override
    public Evidence findById(String id) {
        return null;
    }

    @Override
    public List<Evidence> findByEntityNameAndEntityType(String entityName, EntityType entityType) {
        return null;
    }

    @Override
    public List<Evidence> findByEvidenceTypeAndAnomalyValueIn(EvidenceType evidenceType, String[] anomalyValues) {
        return null;
    }

    @Override
    public List<Evidence> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndEvidenceTypeAndEntityName(long startDate, long endDate, String evidenceType, String entityName) {
        return null;
    }

    @Override
    public List<Evidence> findByEndDateBetweenAndEvidenceTypeAndEntityName(long startDate, long endDate, String evidenceType, String entityName) {
        return null;
    }

    @Override
    public List<Evidence> findByStartDateBetweenAndAnomalyTypeFieldName(Long afterDate, Long beforeDate, String anomalyType) {
        return null;
    }

    @Override
    public List<Evidence> findByStartDateBetweenAndAnomalyTypeFieldNameAndEntityName(Long afterDate, Long beforeDate, String anomalyType, String entityName) {
        return null;
    }

    @Override
    public List<Evidence> findFeatureEvidences(EntityType entityType, String entityName, long startDate, long endDate, String dataEntities, String featureName) {
        return null;
    }

    @Override
    public long countWithParameters(long fromTime, long toTime) {
        return 0;
    }

    @Override
    public List<String> getDistinctAnomalyType() {
        return null;
    }

    @Override
    public List getDistinctByFieldName(String fieldName) {
        return null;
    }

    @Override
    public List<String> getEvidenceIdsByAnomalyTypeFiledNames(List<DataSourceAnomalyTypePair> anomalyTypesList) {
        return null;
    }

    @Override
    public int getVpnGeoHoppingCount(long time, String country1, String city1, String country2, String city2, String username) {
        return 0;
    }

    @Override
    public <S extends Evidence> List<S> save(Iterable<S> iterable) {
        return null;
    }

    @Override
    public <S extends Evidence> S save(S s) {
        return null;
    }

    @Override
    public Evidence findOne(String s) {
        return null;
    }

    @Override
    public boolean exists(String s) {
        return false;
    }

    @Override
    public List<Evidence> findAll() {
        return null;
    }

    @Override
    public Iterable<Evidence> findAll(Iterable<String> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void delete(Evidence evidence) {

    }

    @Override
    public void delete(Iterable<? extends Evidence> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Evidence> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Evidence> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Evidence> List<S> insert(Iterable<S> iterable) {
        return null;
    }

    @Override
    public <S extends Evidence> S insert(S s) {
        return null;
    }
}
