package fortscale.services.dataqueries;

import fortscale.services.dataqueries.querydto.QuerySort;
import fortscale.services.dataqueries.querydto.SortDirection;

import java.util.*;

/**
 * Created by idanp on 3/4/2015.
 */
public class OrderByComarator implements Comparator<Map<String,Object>> {

	List<QuerySort> sortProeprties = new ArrayList<>();


	public OrderByComarator (List<QuerySort> sortProeprties)
	{
		this.sortProeprties = sortProeprties;
	}
	public int compare(Map<String,Object> row1, Map<String,Object> row2)
	{
		for (QuerySort querySort : sortProeprties)
		{
			Comparable o1 = (Comparable) row1.get(querySort.getField().getId());
			Comparable o2 = (Comparable)  row2.get(querySort.getField().getId());

			int compareValue = o1.compareTo(o2);

			//in case of reverse ordering
			if (querySort.getDirection() == SortDirection.DESC)
				compareValue*=-1;

			if (compareValue != 0 )
				return compareValue;



		}
		return 0;

	}

}
