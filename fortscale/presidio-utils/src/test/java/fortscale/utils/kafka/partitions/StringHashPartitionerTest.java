package fortscale.utils.kafka.partitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StringHashPartitionerTest  {

    StringHashPartitioner partitioner;

    @Before
    public void setUp() {
        partitioner = new StringHashPartitioner(null);
    }

    @Test
    public void partitioner_should_return_a_valid_partition_for_null_key() {
        int actual = partitioner.partition(null, 9);
        assertTrue(actual>=0 && actual<9);
    }

    @Test
    public void partitioner_should_return_partitions_not_bigger_than_the_number_of_partitions() {
        int actual = partitioner.partition("moshe", 9);
        assertTrue(actual>=0 && actual<9);
    }

    @Test
    public void partitioner_should_convert_to_string_keys_not_as_given_as_string() {
        int actual = partitioner.partition(new Integer(12), 9);
        int expected = partitioner.partition("12", 9);
        assertEquals(expected, actual);
    }

    @Test
    public void partitioner_should_be_consistent_with_partitions_assignments() {
        int firstPartition = partitioner.partition("moshe", 9);
        for (int i=0;i<100;i++)
            partitioner.partition("moshe" + i, 9);
        int secondPartition = partitioner.partition("moshe", 9);
        assertTrue(firstPartition == secondPartition);
    }

}