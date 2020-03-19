package fortscale.utils.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * An iterator that steps through events in a given s3 objects iterator. It makes the following assumptions.
 *
 * <ul>
 * <li>The files are gzipped new-line delimited JSON files</li>
 * </ul>
 *
 * @author Yael Berger
 */
public class S3DataIterator implements Iterator<Map<String, Object>>, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(S3DataIterator.class);
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {
//    };
    private IMapExtractor mapExtractor;
    private final AmazonS3 s3;
    private final String bucket;

    private BufferReaderIterator lineIterator;
    private Iterator<S3ObjectSummary> fileIterator;


    /**
     * Internal constructor.
     *
     * @param s3           an AmazonS3Client that is set up with access to the bucket paths provided.
     * @param bucket       the S3 bucket to read from
     * @param objects      this s3 objects to iterate on.
     */
    public S3DataIterator(AmazonS3 s3, String bucket, Iterator<S3ObjectSummary> objects) {
        this.s3 = s3;
        this.bucket = bucket;
        this.mapExtractor = new MapExtractor();

        lineIterator = BufferReaderIterator.empty();
        fileIterator = objects;
        nextFile();
    }

    /**
     * returns true, if there are any events left to iterate through. This method is also responsible for logic to jump
     * through files.
     *
     * @return true, if any events left
     */
    @Override
    public boolean hasNext() {
        try {
            // if current file is empty
            if (!lineIterator.hasNext() && (fileIterator.hasNext())) {
                // but we still have remaining files, so iterate to a non-empty file, or to the end
                nextFile();
            }
            return lineIterator.hasNext();
        } catch (Exception e) {
            logger.error("S3 hasNext failure");
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is responsible for handling the cross-file boundaries regarding iterator setup.
     *
     * @return the next line of the file iterator
     */
    @Override
    public Map<String, Object> next() {
        String event = lineIterator.next();
        try {
            return mapExtractor.extract(event);
        } catch (Exception e) {
            logger.error("Failed to deserialize JSON string {}.", event, e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("S3DataIterator{bucket='%s'}", bucket);
    }

    @Override
    public void close() {
        if (lineIterator != null) {
            lineIterator.close();
        }
    }

    /**
     * recurse to the next non-empty file.
     */
    private void nextFile() {
        // remaining files
        if (fileIterator.hasNext()) {
            lineIterator = getS3Reader(fileIterator.next().getKey());
            // if this file is empty, recurse !
            if (!lineIterator.hasNext()) {
                nextFile();
            }
        }
    }

    /**
     * Read a file from S3, a provide access to them as an iterator of lines.
     *
     * @param filePath the key of the file to read
     * @return An iterator to a List containing the lines of the file as {@link String}s
     */
    private BufferReaderIterator getS3Reader(String filePath) {
        InputStream s3ObjectInputStream;
        try {
            S3Object s3Object = s3.getObject(bucket, filePath);
            s3ObjectInputStream = s3Object.getObjectContent();
        } catch (Exception e) {
            logger.error("Failed to get object key: {}, from S3 bucket: {}.", filePath, bucket, e);
            throw new RuntimeException(e);
        }

        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(s3ObjectInputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream));
            return new BufferReaderIterator(reader);
        } catch (Exception e) {
            logger.error("Failed to open file with key: {}.", filePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This class iterates through lines of a BufferedReader and allows the reader to be closed on completion.
     */
    private static final class BufferReaderIterator implements Iterator<String>, Closeable {

        private Iterator<String> iter;
        private BufferedReader reader;

        private BufferReaderIterator(BufferedReader reader) {
            if (reader != null) {
                this.iter = reader.lines().iterator();
                this.reader = reader;
                if (!iter.hasNext()) {
                    close();
                }
            } else {
                iter = Collections.emptyIterator();
            }
        }

        private static BufferReaderIterator empty() {
            return new BufferReaderIterator(null);
        }

        @Override
        public void close() {
            iter = Collections.emptyIterator();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Could not close iterator", e);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public String next() {
            try {
                String next = iter.next();
                if (!iter.hasNext()) {
                    close();
                }
                return next;
            } catch (Exception ex) {
                logger.error("Failed to fetch next record", ex);
                throw new RuntimeException(ex);
            }
        }
    }
}
