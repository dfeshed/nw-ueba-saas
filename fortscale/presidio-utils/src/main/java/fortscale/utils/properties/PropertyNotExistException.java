package fortscale.utils.properties;

public class PropertyNotExistException extends Exception{
	private static final long serialVersionUID = 2449385756631510880L;
	
	
	public PropertyNotExistException(String property) {
		super(String.format("the property %s does not exist", property));
	}

}
