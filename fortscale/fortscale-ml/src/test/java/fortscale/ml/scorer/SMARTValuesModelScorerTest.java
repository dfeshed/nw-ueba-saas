package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.SMARTValuesModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class SMARTValuesModelScorerTest {

    private SMARTValuesModelScorer createScorer(List<String> additionalModelNames, int globalInfluence) {
        return new SMARTValuesModelScorer(
                "scorerName",
                "modelName",
                additionalModelNames,
                Collections.singletonList("contextFieldName"),
                additionalModelNames.stream()
                        .map(additionalModelName -> Collections.singletonList("contextFieldName"))
                        .collect(Collectors.toList()),
                "featureName",
                1,
                1,
                false,
                globalInfluence);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCreateIfNotGivenAdditionalModelName() {
        createScorer(Collections.emptyList(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCreateIfGivenTwoAdditionalModelNames() {
        createScorer(Arrays.asList("model 1", "model 2"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenWrongModelType() {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new CategoryRarityModel(), Collections.singletonList(new SMARTValuesModel()), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfNotGivenAdditionalModel() {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new SMARTValuesModel(), Collections.emptyList(), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenWrongAdditionalModel() {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new SMARTValuesModel(), Collections.singletonList(new CategoryRarityModel()), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenTwoAdditionalModels() {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new SMARTValuesModel(), Arrays.asList(new SMARTValuesModel(), new SMARTValuesModel()), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfNotGivenNumericFeature() {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new SMARTValuesModel(), Collections.singletonList(new SMARTValuesModel()), new Feature("name", "a"));
    }

    @Test
    public void shouldGiveScoreWhenEverythingIsOk() {
        SMARTValuesModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new SMARTValuesModel(), Collections.singletonList(new SMARTValuesModel()), new Feature("name", 1.0));
    }
}
