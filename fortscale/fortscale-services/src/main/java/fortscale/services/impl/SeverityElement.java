package fortscale.services.impl;

import java.util.Comparator;

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
	
	public static class OrderByValueDesc implements Comparator<SeverityElement>{
		
		@Override
		public int compare(SeverityElement o1, SeverityElement o2) {
			return o2.getValue() > o1.getValue() ? 1 : (o2.getValue() < o1.getValue() ? -1 : 0);
		}
		
	}

}
