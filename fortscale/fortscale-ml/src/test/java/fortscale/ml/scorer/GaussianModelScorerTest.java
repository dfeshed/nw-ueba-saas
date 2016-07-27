package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class GaussianModelScorerTest {

    private GaussianModelScorer createScorer(List<String> additionalModelNames, int globalInfluence) {
        return new GaussianModelScorer(
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
        GaussianModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new CategoryRarityModel(), Collections.singletonList(new ContinuousDataModel()), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfNotGivenAdditionalModel() {
        GaussianModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new ContinuousDataModel(), Collections.emptyList(), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenWrongAdditionalModel() {
        GaussianModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new ContinuousDataModel(), Collections.singletonList(new CategoryRarityModel()), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfGivenTwoAdditionalModels() {
        GaussianModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new ContinuousDataModel(), Arrays.asList(new GaussianPriorModel(), new GaussianPriorModel()), new Feature("name", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToScoreIfNotGivenNumericFeature() {
        GaussianModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new ContinuousDataModel(), Collections.singletonList(new GaussianPriorModel()), new Feature("name", "a"));
    }

    @Test
    public void shouldGiveScoreWhenEverythingIsOk() {
        GaussianModelScorer scorer = createScorer(Collections.singletonList("additional model name"), 0);
        scorer.calculateScore(new ContinuousDataModel(), Collections.singletonList(new GaussianPriorModel()), new Feature("name", 1.0));
    }
}
