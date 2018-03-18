package fortscale.services.presidio.core.converters;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.client.model.AlertQuery;
//import presidio.output.client.model.JsonPatch;
//import presidio.output.client.model.PatchOperation;
import presidio.output.client.model.JsonPatch;
import presidio.output.client.model.UserQuery;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by shays on 07/09/2017.
 */
@Service
public class UserConverterHelper {

    private PageConverter pageConverter = new PageConverter();

    
//    private StringConverter stringConverter = new StringConverter();
    private EnumConverter<presidio.output.client.model.User.SeverityEnum,Severity,UserQuery.SeverityEnum> severityEnumConverter;
    private IndicatorTypesConverter indicatorTypesConverter = new IndicatorTypesConverter();
    private TagsConverter tagsConverter = new TagsConverter();
    private AlertConverterHelper alertConverterHelper = new AlertConverterHelper();

    public UserConverterHelper(){
        Map<presidio.output.client.model.User.SeverityEnum,Severity> enumSeverityMap = new HashMap<>();
        enumSeverityMap.put(presidio.output.client.model.User.SeverityEnum.CRITICAL,Severity.Critical);
        enumSeverityMap.put(presidio.output.client.model.User.SeverityEnum.HIGH,Severity.High);
        enumSeverityMap.put(presidio.output.client.model.User.SeverityEnum.MEDIUM,Severity.Medium);
        enumSeverityMap.put(presidio.output.client.model.User.SeverityEnum.LOW,Severity.Low);

        severityEnumConverter = EnumConverter.createInstance(enumSeverityMap,UserQuery.SeverityEnum.class);
    }

    public List<User> convertResponseToUiDto(List<presidio.output.client.model.User> responseUsers){
        if (CollectionUtils.isEmpty(responseUsers)) {
            return Collections.emptyList();
        }

        List<User> uiUsers = new ArrayList<>();
        responseUsers.forEach(responseUser->{
            uiUsers.add(convertFromResponseToUi(responseUser));
        });
        return uiUsers;
    }

    public User convertFromResponseToUi(presidio.output.client.model.User responseUser){
        User uiUser = new User();
        uiUser.setMockId(responseUser.getId());
        uiUser.setUsername(responseUser.getUsername());
        uiUser.setDisplayName(StringUtils.trimToNull(responseUser.getUserDisplayName()));
        uiUser.setScore(responseUser.getScore());
        uiUser.setScoreSeverity(severityEnumConverter.convertResponseToUiDto(responseUser.getSeverity()));
        uiUser.setAlertsCount(responseUser.getAlertsCount());

        if(CollectionUtils.isNotEmpty(responseUser.getAlerts())){
            List<Alert> alerts = new ArrayList<>();
            responseUser.getAlerts().forEach(alertFromBackend->{
                alerts.add(alertConverterHelper.convertResponseToUiDto(alertFromBackend));
            });
            uiUser.setAlerts(alerts);
        }
        uiUser.setFollowed(tagsConverter.isWatched(responseUser.getTags()));





        return uiUser;

    }

    public UserQuery convertUiFilterToQueryDto(UserRestFilter userRestFilter, PageRequest pageRequest,
                                        Set<String> relevantUserIds, boolean expand){
        UserQuery userQuery = new UserQuery();
        userQuery.setPageNumber(pageConverter.convertUiFilterToQueryDtoPageNumber(pageRequest));
        userQuery.setPageSize(pageConverter.convertUiFilterToQueryDtoPageSize(pageRequest));
        userQuery.setSortFieldNames(pageConverter.convertUiFilterToQueryDtoUserSortFields(pageRequest));
        userQuery.setSortDirection(pageConverter.convertUiFilterToQueryDtoSortDirectionForUser(pageRequest));
        userQuery.setExpand(expand);

        if (userRestFilter!=null) {
            userQuery.setAlertClassifications(userRestFilter.getAlertTypes());

            if (userRestFilter.getSeverity() != null) {
                userQuery.setSeverity(severityEnumConverter.convertUiFilterToQueryDto(userRestFilter.getSeverity().name()));
            }
            userQuery.setMaxScore(doubleToInteger(userRestFilter.getMaxScore()));


            userQuery.setMinScore(convertMinScore(userRestFilter));

            if (StringUtils.isNotEmpty(userRestFilter.getSearchFieldContains())) {

                userQuery.setFreeText(userRestFilter.getSearchFieldContains());
            }
            if (userRestFilter.getIndicatorTypes() != null) {
                userQuery.setIndicatorsName(indicatorTypesConverter.convertUiFilterToQueryDto(userRestFilter.getIndicatorTypes()));
            }


            userQuery.setTags(tagsConverter.convertUiFilterToQueryDto(userRestFilter.getUserTags(),userRestFilter.getIsWatched()));
            userQuery.setAlertClassifications(userRestFilter.getAlertTypes());

        }
        return userQuery;
    }

    public JsonPatch createPatchOperation(Boolean watch) {

        JsonPatch jsonPatch = new JsonPatch();
        if (watch!=null){

            jsonPatch.addOperationsItem(tagsConverter.createWatchPatchOperation(watch.booleanValue()));

        }
        return jsonPatch;

    }

    private Integer convertMinScore(UserRestFilter userRestFilter) {
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


//    private List<UserQuery.SeverityEnum> getSeverityEnums(UserRestFilter userRestFilter) {
//        if (userRestFilter.getSeverity()==null){
//            return null;
//        }
//        List<presidio.output.client.model.User.SeverityEnum> SeverityEnumList  = severityEnumConverter.convertUiFilterToQueryDto(userRestFilter.getSeverity().name());
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
