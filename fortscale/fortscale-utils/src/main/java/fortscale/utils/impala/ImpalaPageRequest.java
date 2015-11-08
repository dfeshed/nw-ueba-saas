package fortscale.utils.impala;

import java.util.Iterator;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class ImpalaPageRequest extends PageRequest {
	private static final long serialVersionUID = 1L;

	public ImpalaPageRequest(int limit, Sort sort) {
		super(0, limit, sort);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(getSort() != null) {
			Iterator<Order> iter = getSort().iterator();
			boolean first = true;
			while(iter.hasNext()){
				if(first){
					first = false;
					sb.append(" order by ");
				} else {
					sb.append(", ");
				}
				Order order = iter.next();
				sb.append(order.getProperty()).append(" ").append(order.getDirection());
			}
		}
		
		sb.append(" ").append("limit ").append(getPageSize());
		
		return sb.toString();
	}
}
