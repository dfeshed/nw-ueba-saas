package fortscale.ml.scorer.config;

/**
 * Created by barak_schuster on 6/14/17.
 */
public class ScorerConfServiceImpl extends ScorerConfService{
    private String scorerConfigurationsLocationPath;
    private String scorerConfigurationsOverridingPath;
    private String scorerConfigurationsAdditionalPath;

    public ScorerConfServiceImpl(String scorerConfigurationsLocationPath, String scorerConfigurationsOverridingPath, String scorerConfigurationsAdditionalPath) {
        this.scorerConfigurationsLocationPath = scorerConfigurationsLocationPath;
        this.scorerConfigurationsOverridingPath = scorerConfigurationsOverridingPath;
        this.scorerConfigurationsAdditionalPath = scorerConfigurationsAdditionalPath;
    }

    @Override
    protected String getBaseConfJsonFilesPath() {
        return scorerConfigurationsLocationPath;
    }

    @Override
    protected String getBaseOverridingConfJsonFolderPath() {
        return scorerConfigurationsOverridingPath;
    }

    @Override
    protected String getAdditionalConfJsonFolderPath() {
        return scorerConfigurationsAdditionalPath;
    }
}
