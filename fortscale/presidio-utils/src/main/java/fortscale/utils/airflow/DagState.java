package fortscale.utils.airflow;

/**
 * enum representing the different possibilities of Airflow DAG states
 * Created by barak_schuster on 9/13/17.
 */
public enum DagState {
    SUCCESS,
    RUNNING,
    FAILED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
