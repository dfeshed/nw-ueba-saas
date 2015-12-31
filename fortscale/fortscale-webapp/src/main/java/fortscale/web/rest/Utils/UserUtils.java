package fortscale.web.rest.Utils;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.domain.historical.data.SupportingInformationKey;
import javafx.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserUtils {
//    public Map<String, Double> getListFromSupportingInformation (SupportingInformationGenericData<Double> supportingInformationData, Integer limit) {
//        Map<String, Double> machinesList = new HashMap<>();
//        Map<SupportingInformationKey, Double> supportingInformationMapData = supportingInformationData.getData();
//
//        Integer index = 0;
//
//        for (Map.Entry<SupportingInformationKey, Double> supportingInformationEntry :
//                supportingInformationMapData.entrySet()) {
//            String key = supportingInformationEntry.getKey().generateKey().get(0);
//            Double value = supportingInformationEntry.getValue();
//            machinesList.put(key, value);
//            index += 1;
//            if (index == limit) {
//                break;
//            }
//        }
//
//        return machinesList;
//    }

    public List<Pair<String, Double>> getListFromSupportingInformation (SupportingInformationGenericData<Double> supportingInformationData, Integer limit) {
        List<Pair<String, Double>> entitiesList = new ArrayList<>();

        if (supportingInformationData != null) {
            Map<SupportingInformationKey, Double> supportingInformationMapData = supportingInformationData.getData();

            Integer index = 0;

            for (Map.Entry<SupportingInformationKey, Double> supportingInformationEntry :
                    supportingInformationMapData.entrySet()) {
                String key = supportingInformationEntry.getKey().generateKey().get(0);
                Double value = supportingInformationEntry.getValue();
                entitiesList.add(key, value);
                index += 1;
                if (index == limit) {
                    break;
                }
            }
        }


        return entitiesList;
    }

}
