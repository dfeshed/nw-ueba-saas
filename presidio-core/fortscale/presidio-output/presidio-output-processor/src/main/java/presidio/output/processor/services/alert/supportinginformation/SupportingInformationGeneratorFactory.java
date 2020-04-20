package presidio.output.processor.services.alert.supportinginformation;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.records.alerts.AlertEnums;

import java.util.List;

public class SupportingInformationGeneratorFactory {

    @Autowired
    private List<SupportingInformationGenerator> supporingInformationGenerators;

    public SupportingInformationGenerator getSupportingInformationGenerator(String indicatorType) {


        String type =  indicatorType.equals(AlertEnums.IndicatorTypes.STATIC_INDICATOR.name())? AlertEnums.IndicatorTypes.SCORE_AGGREGATION.name(): indicatorType;

        return supporingInformationGenerators.stream()
                                             .filter(generator -> type.equals(generator.getType()))
                                             .findFirst()
                                             .orElseThrow(IllegalArgumentException::new);

    }

}
