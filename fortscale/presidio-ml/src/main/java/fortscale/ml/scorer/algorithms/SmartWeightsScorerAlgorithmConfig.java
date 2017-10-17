package fortscale.ml.scorer.algorithms;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmartWeightsScorerAlgorithmConfig {

    @Value("${presidio.ade.model.smart.weights.score.fractional.power:0.4}")
    private Double fractionalPower;

    @Bean
    public SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm(){
        return new SmartWeightsScorerAlgorithm(fractionalPower);
    }
}
