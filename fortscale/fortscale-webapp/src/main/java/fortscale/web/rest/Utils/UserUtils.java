package fortscale.web.rest.Utils;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationPopulatorFactory;
import fortscale.aggregation.feature.services.historicaldata.populators.SupportingInformationCountPopulator;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.web.beans.DataBean;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserUtils {

    @Autowired
    private SupportingInformationPopulatorFactory supportingInformationPopulatorFactory;

    /**
     *
     * @param timePeriodInDays Time period in days
     * @param limit            List limit
     */

    /**
     * Validates any top_related routes.
     *
     * @param dataEntitiesCSV  A CSV of required data entities
     * @param featureName      The name of the required feature
     * @param timePeriodInDays Time period in days
     * @param limit            List limit
     */
    public void validateRelatedEntitiesArguments(String dataEntitiesCSV, String featureName, Integer timePeriodInDays, Integer limit) {

        if (timePeriodInDays <= 0) {
            throw new BadRequestException("time_range param must be greater then 0.");
        }

        if (limit <= 0) {
            throw new BadRequestException("limit param must be greater then 0.");
        }

        if (dataEntitiesCSV.isEmpty()) {
            throw new BadRequestException("data_entities must contain at least on data entity.");
        }

        if (featureName.isEmpty()) {
            throw new BadRequestException("feature_name must contain a feature name.");
        }

    }

    /**
     * Creates SupportingInformationGenericData from userName and time period.
     *
     * @param normalized_username Normalized Username key
     * @param timePeriodInDays    Time period in days
     * @return SupportingInformationGenericData
     */
    public SupportingInformationGenericData createSupportingInformationData(String dataEntity, String normalized_username, Integer timePeriodInDays, String featureName) {
        // Create populator and get supporting information data
        SupportingInformationGenericData<Double> supportingInformationData;
        try {
            SupportingInformationCountPopulator supportingInformationCountPopulator = supportingInformationPopulatorFactory.createSupportingInformationPopulator("normalized_username", dataEntity, featureName, "Count");
            supportingInformationData = supportingInformationCountPopulator.createSupportingInformationData(normalized_username, new Date().getTime(), timePeriodInDays);
        } catch (RuntimeException e) {
            supportingInformationData = null;
        }

        return supportingInformationData;
    }

    /**
     * Converts SupportingInformationGenericData into a limited List<Pair<String, Double>>
     *
     * @param supportingInformationDataList Supporting information data
     * @param limit                         The limit of the returned list
     * @return List<Pair>
     */
    public List<Pair<String, Double>> getListFromSupportingInformation(List<SupportingInformationGenericData<Double>> supportingInformationDataList, Integer limit) {
//        List<Pair<String, Double>> entitiesList = new ArrayList<>();
        HashMap<String, Double> entitiesList = new HashMap<>();

        //populate entitiesList
        for (SupportingInformationGenericData<Double> supportingInformationData : supportingInformationDataList) {
            if (supportingInformationData != null) {

                // Convert mapData to List<Pair<String, Double>>
                Map<SupportingInformationKey, Double> supportingInformationMapData = supportingInformationData.getData();
                for (Map.Entry<SupportingInformationKey, Double> supportingInformationEntry :
                        supportingInformationMapData.entrySet()) {
                    String key = supportingInformationEntry.getKey().generateKey().get(0);
                    Double value = supportingInformationEntry.getValue();

                    // Add or sum
//                    if (entitiesList.contains())
//                    entitiesList.add(new Pair<>(key, value));
                    if(entitiesList.containsKey(key)) {
                        entitiesList.replace(key, entitiesList.get(key), entitiesList.get(key) + value);
                    } else {
                        entitiesList.put(key, value);
                    }
                }
            }
        }


        // get unsorted list
        ArrayList<Pair<String, Double>> list = new ArrayList<>();
        for (String key : entitiesList.keySet()) {
            list.add(new Pair<>(key, entitiesList.get(key)));
        }

        // Sort list
        list.sort(new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });

        // Limit list
        ArrayList<Pair<String, Double>> entitiesLimitedList = new ArrayList<>();
        for (int i = 0; i < entitiesList.size(); i += 1) {
            if (i >= limit) {
                break;
            }
            entitiesLimitedList.add(list.get(i));
        }

        return entitiesLimitedList;
    }

    private List<String> getListFromCSV(String csv) {
        return Arrays.asList(csv.split(","));
    }

    public DataBean<List<Pair<String, Double>>> getRelatedEntitiesResponse(String dataEntitiesCSV, String normalized_username, Integer limit, Integer timePeriodInDays, String featureName) {

        // Validations
        validateRelatedEntitiesArguments(dataEntitiesCSV, featureName, timePeriodInDays, limit);

        // Create supportingInformationData
        List<SupportingInformationGenericData<Double>> supportingInformationDataList = new ArrayList<>();
        List<String> dataEntitiesList = getListFromCSV(dataEntitiesCSV);
        for (String dataEntity : dataEntitiesList) {
            SupportingInformationGenericData supportingInformationData = createSupportingInformationData(dataEntity, normalized_username, timePeriodInDays, featureName);
            if (supportingInformationData != null) {
                supportingInformationDataList.add(supportingInformationData);
            }
        }

        // Convert supporting information data into a list.
        DataBean<List<Pair<String, Double>>> response = new DataBean<>();
        response.setData(getListFromSupportingInformation(supportingInformationDataList, limit));
        return response;
    }


}
