package fortscale.domain.core;


import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class AbstractDocument implements Serializable{
	private static final long serialVersionUID = 5043063490239869442L;

	public static final String ID_FIELD = "_id";

	@Id
	private String id;

	/**
	 * Returns the identifier of the document.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	//Only for testing!!!
	protected void setId(String id) {
		this.id = id;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (this.id == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
			return false;
		}

		AbstractDocument that = (AbstractDocument) obj;

		return this.id.equals(that.getId());
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
	
	@Override
    public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("id: ").append(getId());
    return sb.toString();
    }
}
