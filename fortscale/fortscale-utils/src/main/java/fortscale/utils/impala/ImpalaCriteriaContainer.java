package fortscale.utils.impala;

import java.util.ArrayList;
import java.util.List;

public class ImpalaCriteriaContainer implements ImpalaQueryElementInterface{
	
	private static final String IMPALA_CRITERIA_AND = "and";
	private static final String IMPALA_CRITERIA_OR = "or";
	
	private String operator;
	private List<ImpalaQueryElementInterface> elems;
	
	private ImpalaCriteriaContainer(String operator, ImpalaQueryElementInterface... elems){
		this.operator = operator;
		this.elems = new ArrayList<>();
		for(ImpalaQueryElementInterface elem: elems){
			this.elems.add(elem);
		}
	}
	
	public static ImpalaCriteriaContainer andWhere(ImpalaQueryElementInterface... elems){
		return new ImpalaCriteriaContainer(IMPALA_CRITERIA_AND, elems);
	}
	
	public static ImpalaCriteriaContainer orWhere(ImpalaQueryElementInterface... elems){
		return new ImpalaCriteriaContainer(IMPALA_CRITERIA_OR, elems);
	}
	
	public ImpalaCriteriaContainer and(ImpalaQueryElementInterface elem){
		ImpalaCriteriaContainer ret = this;
		if(operator.equals(IMPALA_CRITERIA_AND)){
			elems.add(elem);
		}else{
			ret = andWhere(this,elem);
		}
		return ret;
	}
	
	public ImpalaCriteriaContainer or(ImpalaQueryElementInterface elem){
		ImpalaCriteriaContainer ret = this;
		if(operator.equals(IMPALA_CRITERIA_OR)){
			elems.add(elem);
		}else{
			ret = orWhere(this,elem);
		}
		return ret;
	}

	@Override
	public void appendTo(StringBuilder builder) {
		if(elems.size() == 0){
			return;
		}

		if(elems.size() == 1){
			elems.get(0).appendTo(builder);
		} else{
			builder.append("(");
			elems.get(0).appendTo(builder);
			for(int i = 1; i < elems.size(); i++){
				builder.append(" ").append(operator).append(" ");
				elems.get(i).appendTo(builder);
			}
			builder.append(")");
		}
		
		
	}

}
