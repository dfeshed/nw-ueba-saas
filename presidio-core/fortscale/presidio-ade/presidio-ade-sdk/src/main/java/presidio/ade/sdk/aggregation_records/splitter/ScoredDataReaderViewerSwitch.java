package presidio.ade.sdk.aggregation_records.splitter;

import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;

import java.util.HashMap;
import java.util.Map;

public class ScoredDataReaderViewerSwitch {
    private final Map<Class<? extends AdeScoredRecord>, ScoredDataReaderViewer<? extends AdeRecord>> classToViewerMap;

    public ScoredDataReaderViewerSwitch() {
        classToViewerMap = new HashMap<>();
    }

    public <T extends AdeRecord & AdeScoredRecord> void add(Class<T> clazz, ScoredDataReaderViewer<T> viewer) {
        Assert.notNull(clazz, "clazz cannot be null.");
        Assert.notNull(viewer, "viewer cannot be null.");
        classToViewerMap.put(clazz, viewer);
    }

    public ScoredDataReaderViewer<? extends AdeRecord> get(Class<? extends AdeScoredRecord> clazz) {
        Assert.notNull(clazz, "clazz cannot be null.");
        Assert.isAssignable(AdeRecord.class, clazz, "clazz must be a subclass of AdeRecord.");
        return classToViewerMap.computeIfAbsent(clazz, key -> {
            String s = String.format("Scored record class %s is not supported.", clazz.getSimpleName());
            throw new IllegalArgumentException(s);
        });
    }
}
