package presidio.output.processor.services.alert.indicator;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IndicatorsGeneratorFactory {

    @Autowired
    private List<IndicatorsGenerator>indicatorsGenerators;

    public IndicatorsGenerator getIndicatorsGenerator(String indicatorType) {
                return indicatorsGenerators.stream()
               .filter(generator -> indicatorType.equals(generator.getType()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

    }
}
