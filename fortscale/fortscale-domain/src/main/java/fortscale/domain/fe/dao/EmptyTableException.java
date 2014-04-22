package fortscale.domain.fe.dao;

public class EmptyTableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmptyTableException(String tableName){
		super(String.format("the table (%s) is empty", tableName));
	}
}
