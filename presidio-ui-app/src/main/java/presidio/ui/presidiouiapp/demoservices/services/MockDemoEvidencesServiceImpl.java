package presidio.ui.presidiouiapp.demoservices.services;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.common.dataentity.DataSourceType;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Events;
import fortscale.services.EvidencesService;
import presidio.ui.presidiouiapp.demoservices.DemoBuilder;
import presidio.ui.presidiouiapp.demoservices.DemoEventsFactory;
import presidio.ui.presidiouiapp.rest.Utils.ResourceNotFoundException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Services for managing the evidences
 *
 * Date: 6/23/2015.
 */
//@Service("evidencesService")
//@Profile("mock")
public class MockDemoEvidencesServiceImpl implements EvidencesService, InitializingBean {

	final String TAG_ANOMALY_TYPE_FIELD_NAME = "tag";
	final String TAG_DATA_ENTITY ="active_directory";

	/**
	 * Mongo repository for evidences
	 */



	private DemoBuilder demoBuilder;
	// Severity thresholds for evidence
	@Value("${evidence.severity.medium:80}")
	protected int medium;
	@Value("${evidence.severity.high:90}")
	protected int high;
	@Value("${evidence.severity.critical:95}")
	protected int critical;
	@Value("${collection.evidence.tag.score:50}")
	protected double tagScore;

	/**
	 * Keeps mapping between score and severity
	 */
	private NavigableMap<Integer,Severity> scoreToSeverity = new TreeMap<>();

	@Autowired
	public MockDemoEvidencesServiceImpl(DemoBuilder demoBuilder){
		this.demoBuilder=demoBuilder;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// init scoring to severity map
		scoreToSeverity.put(0, Severity.Low);
		scoreToSeverity.put(medium, Severity.Medium);
		scoreToSeverity.put(high, Severity.High);
		scoreToSeverity.put(critical, Severity.Critical);
	}

	@Override
	public void saveEvidenceInRepository(Evidence evidence) {
		saveEvidence(evidence);
	}

	@Override
	public SupportingInformationData getSupportingInformationIndicatorId(String indicatorId) {
		return null;
	}

	private List<Evidence> getEvidencesMocks() {
		return demoBuilder.getIndicators();
	}

	@Override
	public Evidence findById(String id) {
		return demoBuilder.getIndicators().
				stream().
				filter(indicator -> indicator.getId().equals(id)).
				findAny().
				orElse(null);
	}


	/**
	 * Saves evidence in Mongo
	 * @param evidence the evidence to save
	 * @return the saved evidence
	 */
	private Evidence saveEvidence(Evidence evidence){
			demoBuilder.getIndicators().removeIf(e -> evidence.getId().equals(e.getId()));
			demoBuilder.getIndicators().add(evidence);
			return  evidence;
	}

	public long count(long fromTime, long toTime){
		return demoBuilder.getIndicators().stream()
				.filter(evidence -> evidence.getStartDate()>=fromTime && evidence.getStartDate()<=toTime)
				.collect(Collectors.toSet())
				.size();
	}

	@Override
	public List getDistinctByFieldName(String fieldName) {

		return null;
	}


	@Override
	public List<Evidence> getEvidencesById(List<String> evidenceIds) {
		return this.demoBuilder.getIndicators()
				.stream()
				.filter(indicator->evidenceIds.contains(indicator.getId()))
				.collect(Collectors.toList());

	}

	public Events getListOfEvents(boolean requestTotal, boolean useCache, Integer page,
								  Integer size, String sortField, String sortDirection, String evidenceId) {

		Evidence evidence = findById(evidenceId);
		if (evidence == null || evidence.getId() == null) {
			throw new ResourceNotFoundException("Can't get evidence of id: " + evidenceId);
		}

		DemoEventsFactory eventsFactory = new DemoEventsFactory();
		if (evidence.getDataEntitiesIds().size() == 0) {
			return null;
		}

		DataSourceType dataSourceType = DataSourceType.getEnum(evidence.getDataEntitiesIds().get(0));


		try {
			switch (dataSourceType) {
				case LOGON:
					return
							toEvents(eventsFactory.getLogonEvents(
									evidence.getEntityName(),
									evidence.getStartDate(),
									evidence.getEndDate(),
									evidence.getId()));

				case ACTIVE_DIRECTORY:
					return
							toEvents(eventsFactory.getActiveDirectoryEvents(
									evidence.getEntityName(),
									evidence.getStartDate(),
									evidence.getEndDate(),
									evidence.getId()));

				case File:
					return
							toEvents(eventsFactory.getFileEvents(
									evidence.getEntityName(),
									evidence.getStartDate(),
									evidence.getEndDate(),
									evidence.getId()));

			}
		} catch (Exception e) {
			String s = "";
			e.getMessage();
			return null;
		}
		return  null;
	}

	private Events toEvents(List<Map<String,Object>> events){
		if (events==null){
			return null;
		}
		return new Events(events,events.size());
	}
}
