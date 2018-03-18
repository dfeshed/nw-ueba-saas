package fortscale.web.beans;

public enum DataWarningsEnum {
	NON_CONCLUSIVE_DATA(1, "Data returned is not conclusive"),
	ITEM_NOT_FOUND(404, "Item not found");

	
	
	private int code;
	private String message;
	
	private DataWarningsEnum(int code, String message) { 
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
	
}
