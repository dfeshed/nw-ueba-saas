package fortscale.web.beans;

import java.util.ArrayList;
import java.util.List;

public class DataListWrapperBean<T> extends DataBean<List<T>>{
	
	public DataListWrapperBean(T data){
		List<T> tmp = new ArrayList<T>();
		tmp.add(data);
		setData(tmp);
	}
}
