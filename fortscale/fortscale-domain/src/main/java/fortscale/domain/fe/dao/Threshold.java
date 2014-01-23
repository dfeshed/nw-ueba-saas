package fortscale.domain.fe.dao;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Threshold {
	private String name;
	private Integer value;
	private int count;
	
	public Threshold(String name, Integer value){
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
    public boolean equals(Object obj) {
            if(obj == null) return false;
            if(obj == this) return true;
            if(!(obj instanceof Threshold)) return false;
            Threshold threshold = (Threshold)obj;
            return new EqualsBuilder().append(threshold.getName(), getName()).append(threshold.getCount(), getCount()).append(threshold.getValue(), getValue()).isEquals();
    }
    @Override
    public int hashCode() {
            return new HashCodeBuilder().append(getName()).toHashCode();
    }
}
