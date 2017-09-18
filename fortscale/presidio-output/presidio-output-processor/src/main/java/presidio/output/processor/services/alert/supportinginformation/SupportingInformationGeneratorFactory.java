package presidio.output.processor.services.alert.supportinginformation;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SupportingInformationGeneratorFactory {

    @Autowired
    private List<SupportingInformationGenerator> supporingInformationGenerators;

    public SupportingInformationGenerator getSupportingInformationGenerator(String indicatorType) {
        return supporingInformationGenerators.stream()
                                             .filter(generator -> indicatorType.equals(generator.getType()))
                                             .findFirst()
                                             .orElseThrow(IllegalArgumentException::new);

    }

}
