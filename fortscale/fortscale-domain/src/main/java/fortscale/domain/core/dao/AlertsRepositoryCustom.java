package fortscale.domain.core.dao;

import com.google.common.base.Optional;
import fortscale.domain.core.Alert;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface AlertsRepositoryCustom {


	List<Alert> findAll(PageRequest request, int maxPages);

	}
