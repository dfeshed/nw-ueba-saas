package fortscale.services.dataqueries.querydto;

/**
 * Created by yox on 10/12/2014.
 */
public class DataQueryJoin {
    private DataQueryJoinField left;
    private DataQueryJoinField right;
    private String entity;
    private JoinType type;


	public DataQueryJoin(){}


	//copy constructor
	public DataQueryJoin(DataQueryJoin copy)
	{
		this.left = new DataQueryJoinField(copy.left);
		this.right = new DataQueryJoinField(copy.right);
		this.entity = copy.entity;
		this.type = copy.type;

	}

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }

    public DataQueryJoinField getLeft() {
        return left;
    }

    public void setLeft(DataQueryJoinField left) {
        this.left = left;
    }

    public DataQueryJoinField getRight() {
        return right;
    }

    public void setRight(DataQueryJoinField right) {
        this.right = right;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

	@Override
	public boolean equals (Object other)
	{
		if (other == null) return false;

		if (!(other instanceof DataQueryJoin))return false;
		DataQueryJoin toThat = (DataQueryJoin) other;
		return toThat.getEntity() == this.entity && toThat.getLeft().equals(this.getLeft()) && toThat.getRight().equals(this.getRight()) && toThat.getType() == this.type;
	}
}
