package fortscale.services.dataqueries.querydto;

/**
 * Created by yox on 10/12/2014.
 */
public class DataQueryJoinField {
    private String entity;
    private String field;

	public DataQueryJoinField(){}

	public DataQueryJoinField (DataQueryJoinField other){

		this.entity=other.getEntity();
		this.field=other.getField();
	}

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

	@Override
	public boolean equals (Object other)
	{
		if (other == null) return false;

		if (!(other instanceof DataQueryJoinField))return false;

		DataQueryJoinField toThat = (DataQueryJoinField) other;

		return toThat.getEntity().equals(this.getEntity()) && toThat.getField().equals(this.getField());
	}
}
