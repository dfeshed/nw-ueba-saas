package fortscale.collection.jobs.cleanup;

/**
 * Created by Amir Keren on 01/10/15.
 */
public class MiniStep {

    private CleanJob.Technology technology;
    private CleanJob.Strategy strategy;
    private String dataSources;

    public CleanJob.Technology getTechnology() {
        return technology;
    }

    public void setTechnology(CleanJob.Technology technology) {
        this.technology = technology;
    }

    public CleanJob.Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(CleanJob.Strategy strategy) {
        this.strategy = strategy;
    }

    public String getDataSources() {
        return dataSources;
    }

    public void setDataSources(String dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Technology: " + technology + "\n");
        sb.append("Strategy: " + strategy + "\n");
        sb.append("Data Sources: " + dataSources);
        return sb.toString();
    }

}