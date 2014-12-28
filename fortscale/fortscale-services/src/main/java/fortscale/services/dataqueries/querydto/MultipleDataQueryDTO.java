package fortscale.services.dataqueries.querydto;

import java.util.List;

/**
 * Created by Yossi on 22/12/2014.
 */
public class MultipleDataQueryDTO {
    private List<DataQueryDTO> dataQueries;
    private DataQueryCombineMethod combineMethod;

    public List<DataQueryDTO> getDataQueries() {
        return dataQueries;
    }

    public void setDataQueries(List<DataQueryDTO> dataQueries) {
        this.dataQueries = dataQueries;
    }

    public DataQueryCombineMethod getCombineMethod() {
        return combineMethod;
    }

    public void setCombineMethod(DataQueryCombineMethod combineMethod) {
        this.combineMethod = combineMethod;
    }
}
