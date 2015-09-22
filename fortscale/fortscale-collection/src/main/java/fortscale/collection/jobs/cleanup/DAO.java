package fortscale.collection.jobs.cleanup;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class DAO {

    public Class daoObject;
    public String queryField;

    public DAO(Class daoObject, String queryField) {
        this.daoObject = daoObject;
        this.queryField = queryField;
    }

}