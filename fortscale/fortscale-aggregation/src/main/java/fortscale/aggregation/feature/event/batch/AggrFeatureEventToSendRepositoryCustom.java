package fortscale.aggregation.feature.event.batch;

import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by YaronDL on 12/31/2015.
 */
public interface AggrFeatureEventToSendRepositoryCustom {

    public List<AggrFeatureEventToSend> findByEndTimeGtAndEndTimeLte(Long lowerTimeSec, Long upperTimeSec, Pageable pageable);
    public void deleteByEndTimeGtAndEndTimeLte(Long lowerTimeSec, Long upperTimeSec);
}
