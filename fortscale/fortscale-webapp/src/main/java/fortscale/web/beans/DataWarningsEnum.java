package fortscale.web.beans;

public enum DataWarningsEnum {
	NonCoclusiveData(1, "Data returned is not conslusive");
	
	
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
