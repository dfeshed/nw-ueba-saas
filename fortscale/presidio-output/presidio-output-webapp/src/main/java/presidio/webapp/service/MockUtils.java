package presidio.webapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import presidio.webapp.model.Indicator;

import java.util.ArrayList;
import java.util.List;

// TEMPORARY CODE FOR THIS DROP - DO NOT REVIEW
public class MockUtils {

    public static Indicator mockIndicator(boolean expand) {
        Indicator restIndicator = new Indicator();
        TypeReference indicatorTypeReference = new TypeReference<Indicator>() {};
        if (expand) {
            restIndicator = (Indicator) mockJsonResponse("indicator_with_historical_data.json", indicatorTypeReference);
        } else {
            restIndicator = (Indicator) mockJsonResponse("indicator_without_historical_data.json", indicatorTypeReference);
        }
        return restIndicator;
    }

    public static List<Indicator> mockIndicators(boolean expand) {
        List<Indicator> restIndicators = new ArrayList<Indicator>();
        TypeReference indicatorsListTypeReference = new TypeReference<List<Indicator>>() {};
        if (expand) {
            restIndicators = (List<presidio.webapp.model.Indicator>) mockJsonResponse("indicators_with_historical_data.json", indicatorsListTypeReference);
        } else {
            restIndicators = (List<presidio.webapp.model.Indicator>) mockJsonResponse("indicators_without_historical_data.json", indicatorsListTypeReference);
        }
        return restIndicators;
    }

    // TEMPORARY CODE FOR THIS DROP - DO NOT REVIEW !!!!
    public static Object mockJsonResponse (String fileName, TypeReference reference) {
        Object object = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            object = mapper.readValue(new ClassPathResource(fileName).getInputStream(), reference);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return  object;
    }

}
