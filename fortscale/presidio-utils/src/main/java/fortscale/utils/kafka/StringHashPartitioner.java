package fortscale.utils.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Kafka producer partitioner that partition events between partitions according to the string key hash value.
 * The algorithm implementation cannot use java built in hashCode to generate int number from the input key, as
 * the default hashCode implementation can vary between executions or on different machines. To overcome this, we
 * resort to using MD5 algorithm, as it is consistent and performs better than SHA based variants
 */
public class StringHashPartitioner implements Partitioner {
    MessageDigest digest;

    public StringHashPartitioner(VerifiableProperties props) {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("error creating StringHashPartitioner", e);
        }
    }

    @Override
    public int partition(Object key, int numPartitions) {
        // save some time when there are no partitions
        if (numPartitions == 1)
            return 0;

        // convert key to string
        String stringKey = (key == null) ? "place-holder" : ((key instanceof String) ? (String)key : key.toString());

        // calculate hash value for the key
        BigInteger hashed = new BigInteger(1, digest.digest(stringKey.getBytes()));

        // ensure the hash within bounds of number of partitions
        return Math.abs(hashed.intValue() % numPartitions);
    }
}
