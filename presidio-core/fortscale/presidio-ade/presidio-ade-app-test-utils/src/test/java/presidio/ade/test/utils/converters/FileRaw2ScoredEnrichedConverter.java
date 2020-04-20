package presidio.ade.test.utils.converters;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.core.convert.converter.Converter;
import presidio.ade.domain.record.enriched.file.AdeScoredFileRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.data.domain.event.file.FileEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by barak_schuster on 15/08/2017.
 */
public class FileRaw2ScoredEnrichedConverter implements Converter<FileEvent,AdeScoredFileRecord> {
    private FileRaw2EnrichedConverter fileRaw2EnrichedConverter;

    private String featureName;
    private Double score;

    public FileRaw2ScoredEnrichedConverter(FileRaw2EnrichedConverter fileRaw2EnrichedConverter) {
        this.fileRaw2EnrichedConverter = fileRaw2EnrichedConverter;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public AdeScoredFileRecord convert(FileEvent source) {
        EnrichedFileRecord enrichedFileRecord = fileRaw2EnrichedConverter.convert(source);
        List<FeatureScore> featureScoreList = null;
        AdeScoredFileRecord adeScoredFileRecord = new AdeScoredFileRecord(source.getDateTime(),featureName,"file", score, featureScoreList,enrichedFileRecord);
        adeScoredFileRecord.fillContext(enrichedFileRecord);
        return adeScoredFileRecord;
    }

    public List<AdeScoredFileRecord> convert(List<FileEvent> sources) {
        List<AdeScoredFileRecord> convertedList = new LinkedList<>();
        sources.forEach(source -> convertedList.add(this.convert(source)));
        return convertedList;
    }
}
