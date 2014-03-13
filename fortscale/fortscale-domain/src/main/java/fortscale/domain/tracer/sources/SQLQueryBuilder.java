package fortscale.domain.tracer.sources;

public class SQLQueryBuilder {

	private StringBuilder where = new StringBuilder();
	private boolean hasWhere = false;
	private String select;
	private String from;
	
	public SQLQueryBuilder() {}
	
	public void select(String select) {
		if (select.toUpperCase().startsWith("SELECT"))
			this.select = select;
		else
			this.select = "SELECT " + select;
	}
	
	public void from(String from) {
		if (from.toUpperCase().startsWith("FROM"))
			this.from = from;
		else
			this.from = "FROM " + from;
	}
	
	public void where(String criteria, Object... params) {
		if (hasWhere) 
			where.append(" AND ");
		where.append(String.format(criteria, params));
		hasWhere = true;
	}
	
	public void where(String criteria, Iterable<String> in) {
		
		// convert in list to comma separated list
		StringBuilder sb = new StringBuilder();
		for (String item : in) {
			if (sb.length()>0)
				sb.append(",");
			sb.append("'").append(item.toLowerCase()).append("'");
		}
		
		
		where(criteria, sb.toString());
	}
	
	@Override
	public String toString() {
		if (hasWhere) {
			return String.format("%s %S WHERE %s", select, from, where.toString());
		} else {
			return String.format("%s %s", select, from);
		}
	}
}
