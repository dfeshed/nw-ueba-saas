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
     * Validates any top_related routes.
     *
     * @param timePeriodInDays Time period in days
     * @param limit List limit
     */
    public void validateRelatedEntitiesArguments (Integer timePeriodInDays, Integer limit) {

        if (timePeriodInDays <= 0) {
            throw new BadRequestException("time_range param must be greater then 0.");
        }

        if (limit <= 0) {
            throw new BadRequestException("limit param must be greater then 0.");
        }

    }

    /**
     * Creates SupportingInformationGenericData from userName and time period.
     * @param normalized_username Normalized Username key
     * @param timePeriodInDays Time period in days
     * @return SupportingInformationGenericData
     */
    public SupportingInformationGenericData createSupportingInformationData(String normalized_username, Integer timePeriodInDays, String featureName) {
        // Create populator and get supporting information data
        SupportingInformationGenericData<Double> supportingInformationData;
        try {
            SupportingInformationCountPopulator supportingInformationCountPopulator = supportingInformationPopulatorFactory.createSupportingInformationPopulator("normalized_username", "kerberos_logins", featureName, "Count");
            supportingInformationData = supportingInformationCountPopulator.createSupportingInformationData(normalized_username, new Date().getTime(), timePeriodInDays);
        } catch(SupportingInformationException e) {
            supportingInformationData = null;
        }

        return supportingInformationData;
    }

    /**
     * Converts SupportingInformationGenericData into a limited List<Pair<String, Double>>
     *
     * @param supportingInformationData Supporting information data
     * @param limit The limit of the returned list
     * @return List<Pair>
     */
    public List<Pair<String, Double>> getListFromSupportingInformation (SupportingInformationGenericData<Double> supportingInformationData, Integer limit) {
        List<Pair<String, Double>> entitiesList = new ArrayList<>();

        if (supportingInformationData != null) {
            Map<SupportingInformationKey, Double> supportingInformationMapData = supportingInformationData.getData();

            Integer index = 0;

            for (Map.Entry<SupportingInformationKey, Double> supportingInformationEntry :
                    supportingInformationMapData.entrySet()) {
                String key = supportingInformationEntry.getKey().generateKey().get(0);
                Double value = supportingInformationEntry.getValue();
                entitiesList.add(new Pair<>(key, value));
                index += 1;
                if (index == limit) {
                    break;
                }
            }
        }




        return entitiesList;
    }

    public DataBean<List<Pair<String, Double>>> getRelatedEntitiesResponse (String normalized_username, Integer limit, Integer timePeriodInDays, String featureName) {

        // Validations
        validateRelatedEntitiesArguments(timePeriodInDays, limit);

        // Create supportingInformationData
        SupportingInformationGenericData supportingInformationData = createSupportingInformationData(normalized_username, timePeriodInDays, featureName);

        // Convert supporting information data into a list.
        DataBean<List<Pair<String, Double>>> response = new DataBean<>();
        response.setData(getListFromSupportingInformation(supportingInformationData, limit));
        return response;
    }


}
