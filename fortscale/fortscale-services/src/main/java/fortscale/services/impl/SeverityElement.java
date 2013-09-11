package fortscale.services.impl;

public class SeverityElement {
	private String name;
	private int value;
	
	public SeverityElement(String name, int value){
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}


	public int getValue() {
		return value;
	}

}
