package fortscale.utils.impala;

import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Pageable;

import com.google.common.base.Joiner;

public class ImpalaQuery {
	private String select[];
	private String table;
	private ImpalaCriteriaContainer whereCriteria;
	private Pageable pageable;
	
	public ImpalaQuery(){
		
	}
	
	public ImpalaQuery select(String... argArray){
		select = argArray;
		return this;
	}
	
	public ImpalaQuery from(String tableName){
		this.table = tableName;
		return this;
	}
	
	public ImpalaQuery where(ImpalaQueryElementInterface elem){
		return andWhere(elem);
	}
	
	public ImpalaQuery andWhere(ImpalaQueryElementInterface elem){
		if(whereCriteria == null){
			whereCriteria = ImpalaCriteriaContainer.andWhere(elem);
		} else{
			whereCriteria = whereCriteria.and(elem);
		}
		return this;
	}
	
	public ImpalaQuery andWhere(String criteria){
		if(whereCriteria == null){
			whereCriteria = ImpalaCriteriaContainer.andWhere(new ImpalaCriteriaString(criteria));
		} else{
			whereCriteria = whereCriteria.and(new ImpalaCriteriaString(criteria));
		}
		return this;
	}
	
	public ImpalaQuery limitAndSort(Pageable pageable){
		this.pageable = pageable;
		return this;
	}
	
	public String toSQL(){
		if(table == null){
			throw new InvalidDataAccessResourceUsageException("the query does not contain the table.");
		}
		StringBuilder builder = new StringBuilder(64);
		builder.append("select ");
		if(select == null){
			builder.append("*");
		} else{
			Joiner.on(",").appendTo(builder, select);
		}
		builder.append(" from ").append(table);
		if(whereCriteria != null){
			builder.append(" where ");
			whereCriteria.appendTo(builder);
		}
		if(pageable != null) {
			builder.append(" ").append(pageable.toString());
		}
		
		
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return toSQL();
	}
}
