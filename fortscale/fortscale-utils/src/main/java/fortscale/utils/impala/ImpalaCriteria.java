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
		return equalsTo(key, value, false);
	}
	
	public static ImpalaCriteria equalsTo(String key, String value, boolean encloseQuotes){
		if (encloseQuotes)
			return compare(key, "=", "'" + value + "'");
		else
			return compare(key, "=", value);
	}
	
	public static ImpalaCriteria neq(String key, String value) {
		return neq(key, value, false);
	}
	
	public static ImpalaCriteria neq(String key, String value, boolean encloseQuotes) {
		if (encloseQuotes)
			return compare(key, "!=", "'" + value + "'");
		else
			return compare(key, "!=", value);
	}
	
	public static ImpalaCriteria gte(String key, String value){
		return compare(key, ">=", value);
	}
	
	public static ImpalaCriteria lte(String key, String value){
		return compare(key, "<=", value);
	}
	
	public static ImpalaCriteria lt(String key, String value){
		return compare(key, "<", value);
	}
	
	public static ImpalaCriteria in(String key, String value){
		return compare(key, " in ", "(" +  value + ")");
	}
	
	public static ImpalaCriteria in(String key, Iterable<?> in){
		return compare(key, " in ", listToString(in));
	}
	
	public static ImpalaCriteria notIn(String key, Iterable<String> in){
		return compare(key, " not in ", listToString(in));
	}
	
	public static ImpalaCriteria notIn(String key, String value){
		return compare(key, " not in ", "(" + value + ")");
	}
	
	public static String lower(String key) {
		return String.format("lower(%s)", key);
	}
	

	public static String quote(String value) {
		return String.format("'%s'", value);
	}

	private static String listToString(Iterable<?> in) {	
		// convert in list to comma separated list
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (Object item : in) {
			if (sb.length()>1)
				sb.append(",");
			sb.append("'").append(item.toString().toLowerCase()).append("'");
		}
		sb.append(")");
		return sb.toString();
	}
	
	
	@Override
	public void appendTo(StringBuilder builder){
		builder.append(key).append(operator).append(value);
	}
}
