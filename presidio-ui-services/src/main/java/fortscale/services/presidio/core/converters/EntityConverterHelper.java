package fortscale.services.presidio.core.converters;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Entity;
import fortscale.domain.core.Severity;
import fortscale.domain.rest.EntityRestFilter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
//import presidio.output.client.model.JsonPatch;
//import presidio.output.client.model.PatchOperation;
import presidio.output.client.model.EntityQuery;
import presidio.output.client.model.JsonPatch;

import java.util.*;

/**
 * Created by shays on 07/09/2017.
 */
@Service
public class EntityConverterHelper {

    private PageConverter pageConverter = new PageConverter();

    
//    private StringConverter stringConverter = new StringConverter();
    private EnumConverter<presidio.output.client.model.Entity.SeverityEnum,Severity,EntityQuery.SeverityEnum> severityEnumConverter;
    private IndicatorTypesConverter indicatorTypesConverter = new IndicatorTypesConverter();
    private TagsConverter tagsConverter = new TagsConverter();
    private AlertConverterHelper alertConverterHelper = new AlertConverterHelper();

    public EntityConverterHelper(){
        Map<presidio.output.client.model.Entity.SeverityEnum,Severity> enumSeverityMap = new HashMap<>();
        enumSeverityMap.put(presidio.output.client.model.Entity.SeverityEnum.CRITICAL,Severity.Critical);
        enumSeverityMap.put(presidio.output.client.model.Entity.SeverityEnum.HIGH,Severity.High);
        enumSeverityMap.put(presidio.output.client.model.Entity.SeverityEnum.MEDIUM,Severity.Medium);
        enumSeverityMap.put(presidio.output.client.model.Entity.SeverityEnum.LOW,Severity.Low);

        severityEnumConverter = EnumConverter.createInstance(enumSeverityMap,EntityQuery.SeverityEnum.class);
    }

    public List<Entity> convertResponseToUiDto(List<presidio.output.client.model.Entity> responseEntities){
        if (CollectionUtils.isEmpty(responseEntities)) {
            return Collections.emptyList();
        }

        List<Entity> uiEntities = new ArrayList<>();
        responseEntities.forEach(responseEntity->{
            uiEntities.add(convertFromResponseToUi(responseEntity));
        });
        return uiEntities;
    }

    public Entity convertFromResponseToUi(presidio.output.client.model.Entity responseEntity){
        Entity uiEntity = new Entity();
        uiEntity.setMockId(responseEntity.getId());
        uiEntity.setUsername(responseEntity.getEntityName());
        uiEntity.setDisplayName(StringUtils.trimToNull(responseEntity.getEntityName()));
        uiEntity.setScore(responseEntity.getScore());
        uiEntity.setScoreSeverity(severityEnumConverter.convertResponseToUiDto(responseEntity.getSeverity()));
        uiEntity.setAlertsCount(responseEntity.getAlertsCount());

        if(CollectionUtils.isNotEmpty(responseEntity.getAlerts())){
            List<Alert> alerts = new ArrayList<>();
            responseEntity.getAlerts().forEach(alertFromBackend->{
                alerts.add(alertConverterHelper.convertResponseToUiDto(alertFromBackend));
            });
            uiEntity.setAlerts(alerts);
        }
        uiEntity.setFollowed(tagsConverter.isWatched(responseEntity.getTags()));





        return uiEntity;

    }

    public EntityQuery convertUiFilterToQueryDto(EntityRestFilter userRestFilter, PageRequest pageRequest,
                                                 Set<String> relevantUserIds, boolean expand){
        EntityQuery entityQuery = new EntityQuery();
        entityQuery.setPageNumber(pageConverter.convertUiFilterToQueryDtoPageNumber(pageRequest));
        entityQuery.setPageSize(pageConverter.convertUiFilterToQueryDtoPageSize(pageRequest));
        entityQuery.setSortFieldNames(pageConverter.convertUiFilterToQueryDtoUserSortFields(pageRequest));
        entityQuery.setSortDirection(pageConverter.convertUiFilterToQueryDtoSortDirectionForUser(pageRequest));
        entityQuery.setExpand(expand);

        if (userRestFilter!=null) {
            entityQuery.setAlertClassifications(userRestFilter.getAlertTypes());

            if (userRestFilter.getSeverity() != null) {
                entityQuery.setSeverity(severityEnumConverter.convertUiFilterToQueryDto(userRestFilter.getSeverity().name()));
            }
            entityQuery.setMaxScore(doubleToInteger(userRestFilter.getMaxScore()));


            entityQuery.setMinScore(convertMinScore(userRestFilter));

            if (StringUtils.isNotEmpty(userRestFilter.getSearchFieldContains())) {

                entityQuery.setFreeText(userRestFilter.getSearchFieldContains());
            }
            if (userRestFilter.getIndicatorTypes() != null) {
                entityQuery.setIndicatorsName(indicatorTypesConverter.convertUiFilterToQueryDto(userRestFilter.getIndicatorTypes()));
            }


            entityQuery.setTags(tagsConverter.convertUiFilterToQueryDto(userRestFilter.getEntityTags(),userRestFilter.getIsWatched()));
            entityQuery.setAlertClassifications(userRestFilter.getAlertTypes());

        }
        return entityQuery;
    }

    public JsonPatch createPatchOperation(Boolean watch) {

        JsonPatch jsonPatch = new JsonPatch();
        if (watch!=null){

            jsonPatch.addOperationsItem(tagsConverter.createWatchPatchOperation(watch.booleanValue()));

        }
        return jsonPatch;

    }

    private Integer convertMinScore(EntityRestFilter userRestFilter) {
        Double minScore = userRestFilter.getMinScore();
        if (userRestFilter.getEntityMinScore()!=null){
            minScore = userRestFilter.getEntityMinScore().doubleValue();
        }
        if (minScore == null){
            return null;
        }
        if (minScore==0){
            minScore++;
        }
        return doubleToInteger(minScore);
    }


//    private List<UserQuery.SeverityEnum> getSeverityEnums(EntityRestFilter userRestFilter) {
//        if (userRestFilter.getSeverity()==null){
//            return null;
//        }
//        List<presidio.output.client.model.Entity.SeverityEnum> SeverityEnumList  = severityEnumConverter.convertUiFilterToQueryDto(userRestFilter.getSeverity().name());
//        if (SeverityEnumList==null){
//            return null;
//        }
//        return SeverityEnumList.stream().map(SeverityEnum -> {
//            return UserQuery.SeverityEnum.valueOf(SeverityEnum.name());
//        }).collect(Collectors.toList());
//    }

    private Integer doubleToInteger(Double score) {
        if (score==null){
            return null;
        }
        return score.intValue();
    }





}
