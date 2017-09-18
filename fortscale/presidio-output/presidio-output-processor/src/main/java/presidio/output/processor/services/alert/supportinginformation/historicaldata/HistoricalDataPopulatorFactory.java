package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.processor.services.alert.supportinginformation.SupportingInformationGenerator;

import java.util.List;

/**
 * Factory class to create supporting information populators.
 * The creation of populator is based mostly on the aggregation function but can also take into consideration
 * the context type (i.e. user, source machine etc.) and feature to allow different populators with different characteristics
 *
 *
 * @author gils
 * Date: 05/08/2015
 */
public class HistoricalDataPopulatorFactory {

    @Autowired
    private List<HistoricalDataPopulator> historicalDataPopulators;

    public HistoricalDataPopulator createHistoricalDataPopulation(String contextType, String featureName, String aggregationType) {
        return historicalDataPopulators.stream()
                .filter(generator -> aggregationType.equals(generator.getType()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
