package fortscale.utils.impala;

public class ImpalaCriteria implements ImpalaQueryElementInterface{
	private String key;
	private String operator;
	private String value;
	
	public ImpalaCriteria(String key, String operator, String value){
		this.key = key;
		this.operator = operator;
		this.value = value;
	}
	
	public static ImpalaCriteria equalsTo(String key, String value){
		return new ImpalaCriteria(key, "=", value);
	}
	
	@Override
	public void appendTo(StringBuilder builder){
		builder.append(key).append(operator).append(value);
	}
}
