package fortscale.ml.scorer.config;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParetoScorerConfTest {

    static List<IScorerConf> scorerConfList = new ArrayList<>();
    static {
        scorerConfList.add(new IScorerConf() {
            @Override
            public String getName() {
                return "dummyScorer";
            }

            @Override
            public String getFactoryName() {
                return "dummyScorerFactoryName";
            }
        });
    }

    @Test
    public void constructor_good_test() {
        new ParetoScorerConf("name", 0.5, scorerConfList);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_name_test() {
        new ParetoScorerConf(null, 0.5, scorerConfList);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        new ParetoScorerConf("", 0.5, scorerConfList);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        new ParetoScorerConf("  ", 0.5, scorerConfList);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_null_scorerConfList_test() {
        new ParetoScorerConf("name", 0.5, null);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_empty_scorerConfList_test() {
        new ParetoScorerConf("name", 0.5, new ArrayList<>());
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_zero_hieghestScoreWeight_test() {
        new ParetoScorerConf("name", 0.0, scorerConfList);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_negative_hieghestScoreWeight_zero() {
        new ParetoScorerConf("name", -0.6, scorerConfList);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_1_hieghestScoreWeight_zero() {
        new ParetoScorerConf("name", 1.0, scorerConfList);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void constructor_larger_then_1_hieghestScoreWeight_zero() {
        new ParetoScorerConf("name", 1.6, scorerConfList);
    }
}
