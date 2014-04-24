package fortscale.utils.impala;

import java.util.Collection;

import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Pageable;

import com.google.common.base.Joiner;

public class ImpalaQuery {
	private String select[];
	private String table;
	private ImpalaCriteriaContainer whereCriteria;
	private Pageable pageable;
	private String[] groupBy;
	
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
	
	public ImpalaQuery andIn(String fieldName, Collection<?> elems){
		if(elems != null && !elems.isEmpty()){
			return andWhere(ImpalaCriteria.in(fieldName, elems));
		} else{
			return this;
		}
	}
	
	public ImpalaQuery andGte(String fieldName, int elem){
		return andWhere(ImpalaCriteria.gte(fieldName, Integer.toString(elem)));
	}
	
	public ImpalaQuery andEq(String fieldName, Long elem){
		return andWhere(ImpalaCriteria.equalsTo(fieldName, elem.toString()));
	}
	
	public ImpalaQuery andEq(String fieldName, String elem){
		return andWhere(ImpalaCriteria.equalsTo(fieldName, elem));
	}
	
	public ImpalaQuery andEqInQuote(String fieldName, String elem){
		return andWhere(ImpalaCriteria.equalsTo(fieldName, String.format("'%s'", elem)));
	}
	
	public ImpalaQuery limitAndSort(Pageable pageable){
		this.pageable = pageable;
		return this;
	}
	
	public ImpalaQuery groupBy(String... fields) {
		groupBy = fields;		
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
		if (groupBy != null) {
			builder.append(" group by ");
			Joiner.on(",").appendTo(builder, groupBy);
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
