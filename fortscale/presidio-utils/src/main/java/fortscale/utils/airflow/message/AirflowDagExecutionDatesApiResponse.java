package fortscale.utils.airflow.message;

import java.util.List;

/**
 * response used at api: {@link <a href=http://localhost:8000/admin/rest_api/api?api=dag_execution_dates_for_state/>}
 * Created by barak_schuster on 9/13/17.
 */
public class AirflowDagExecutionDatesApiResponse extends AirflowApiResponse<List<DagToExecutionDates>> {
    public AirflowDagExecutionDatesApiResponse() {
    }
}
