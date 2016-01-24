package fortscale.common.dataqueries.querydto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yossi on 22/12/2014.
 */
public class MultipleDataQueryDTO {
    private List<DataQueryDTOImpl> dataQueries;
    private DataQueryCombineMethod combineMethod;

	public MultipleDataQueryDTO (){}
	public MultipleDataQueryDTO (MultipleDataQueryDTO other)
	{
		this.dataQueries = new ArrayList<>(other.getDataQueries());
		this. combineMethod = other.combineMethod;
	}

    public List<DataQueryDTOImpl> getDataQueries() {
        return dataQueries;
    }

    public void setDataQueries(List<DataQueryDTOImpl> dataQueries) {
        this.dataQueries = dataQueries;
    }

    public DataQueryCombineMethod getCombineMethod() {
        return combineMethod;
    }

    public void setCombineMethod(DataQueryCombineMethod combineMethod) {
        this.combineMethod = combineMethod;
    }
}
