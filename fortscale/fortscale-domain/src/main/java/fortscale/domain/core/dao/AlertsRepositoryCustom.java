package fortscale.domain.core.dao;

import fortscale.domain.core.dao.rest.Alert;
import fortscale.domain.core.dao.rest.Alerts;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletRequest;

public interface AlertsRepositoryCustom {


	Alerts findAll(PageRequest request, HttpServletRequest httpRequest);
	void add(Alert alert);

	}
