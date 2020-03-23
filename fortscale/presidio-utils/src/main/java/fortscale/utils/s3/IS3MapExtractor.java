package fortscale.utils.s3;

import java.io.IOException;
import java.util.Map;

public interface IS3MapExtractor {
    Map<String, Object> extract(String event) throws IOException;
}
