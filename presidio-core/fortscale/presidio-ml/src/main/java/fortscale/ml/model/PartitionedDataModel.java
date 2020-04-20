package fortscale.ml.model;

/**
 * Created by barak_schuster on 10/22/17.
 */
public interface PartitionedDataModel extends Model {
    long getNumOfPartitions();

}
