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
	
	public static ImpalaCriteria compare(String key, String op, String value){
		return new ImpalaCriteria(key, op, value);
	}
	
	public static ImpalaCriteria equalsTo(String key, String value){
		return compare(key, "=", value);
	}
	
	public static ImpalaCriteria gte(String key, String value){
		return compare(key, ">=", value);
	}
	
	public static ImpalaCriteria lte(String key, String value){
		return compare(key, "<=", value);
	}
	
	public static ImpalaCriteria in(String key, String value){
		return compare(key, " in ", "(" +  value + ")");
	}
	
	public static ImpalaCriteria in(String key, Iterable<String> in){
		return compare(key, " in ", listToString(in));
	}
	
	public static ImpalaCriteria notIn(String key, Iterable<String> in){
		return compare(key, " not in ", listToString(in));
	}
	
	public static ImpalaCriteria notIn(String key, String value){
		return compare(key, " not in ", "(" + value + ")");
	}
	
	private static String listToString(Iterable<String> in) {
		// convert in list to comma separated list
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String item : in) {
			if (sb.length()>0)
				sb.append(",");
			sb.append("'").append(item.toLowerCase()).append("'");
		}
		sb.append(")");
		return sb.toString();
	}
	
	
	@Override
	public void appendTo(StringBuilder builder){
		builder.append(key).append(operator).append(value);
	}
}
