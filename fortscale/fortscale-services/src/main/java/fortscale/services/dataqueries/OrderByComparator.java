package fortscale.services.dataqueries;

import fortscale.services.dataqueries.querydto.QuerySort;
import fortscale.services.dataqueries.querydto.SortDirection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by idanp on 3/4/2015.
 */
public class OrderByComparator implements Comparator<Map<String,Object>> {

	List<QuerySort> sortProeprties = new ArrayList<>();


	public OrderByComparator(List<QuerySort> sortProeprties)
	{
		this.sortProeprties = sortProeprties;
	}
	public int compare(Map<String,Object> row1, Map<String,Object> row2)
	{
		for (QuerySort querySort : sortProeprties)
		{
			Comparable o1 = (Comparable) (querySort.getField().getId() != null ? row1.get(querySort.getField().getId()) : row1.get(querySort.getField().getAlias())) ;
			Comparable o2 = (Comparable) (querySort.getField().getId() != null ? row2.get(querySort.getField().getId()) : row2.get(querySort.getField().getAlias()));
			int compareValue = 0;
			if (o1 != null){
				compareValue = o1.compareTo(o2);
			}
			else if (o1 == null && o2 != null){
				compareValue = -1*o2.compareTo(o1);
			}

			//in case of reverse ordering
			if (querySort.getDirection() == SortDirection.DESC)
				compareValue*=-1;

			if (compareValue != 0 )
				return compareValue;



		}
		return 0;

	}

}
