package fortscale.domain.core.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;


public class ComputerRepositoryImpl implements ComputerRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	public Date getLatestWhenChanged() {
		Query query = new Query();
		query.fields().include(Computer.WHEN_CHANGED_FIELD);
		query.with(new Sort(Direction.DESC, Computer.WHEN_CHANGED_FIELD));
		query.limit(1);
		
		DateWrapper latest = mongoTemplate.findOne(query, DateWrapper.class, Computer.COLLECTION_NAME);
		return latest!=null ? latest.getLatest() : null;
	}
	
	class DateWrapper {
		private Date latest;

		public Date getLatest() {
			return latest;
		}
		public void setLatest(Date latest) {
			this.latest = latest;
		}	
	}

	@Override
	public void updateSensitiveMachine(Computer computer,
			boolean isSensitiveMachine) {

		mongoTemplate.updateFirst(query(where(Computer.NAME_FIELD).is(computer.getName())), update(Computer.SENSITIVE_MACHINE_FIELD, isSensitiveMachine), Computer.class);
	}

	@Override
	public List<Computer> getComputersFromNames(List<String> machineNames) {
		return mongoTemplate.find(query(where(Computer.NAME_FIELD).in(machineNames)), Computer.class);
	}

	@Override public List<Computer> getComputersOfType(ComputerUsageType type, int limit) {
		return mongoTemplate.find(query(where(Computer.getUsageClassfierField(ComputerUsageClassifier.
				USAGE_TYPE_FIELD)).is(type)).limit(limit), Computer.class);
	}

	@Override
	public List<String> findNameByIsSensitive(Boolean isSensitiveMachine) {
		Query query = new Query();
		Criteria criteria = where(Computer.SENSITIVE_MACHINE_FIELD).is(isSensitiveMachine);
		query.fields().include(Computer.NAME_FIELD).exclude(Computer.ID_FIELD);
		query.addCriteria(criteria);
		ArrayList<String> res = new ArrayList<String>();
		for(ComputerNameWrapper computerNameWrapper : mongoTemplate.find(query, ComputerNameWrapper.class, Computer.COLLECTION_NAME)){
			res.add(computerNameWrapper.getName());
		}
		return res;
	}
	
	class ComputerNameWrapper{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	@Override
	public long getNumberOfMachinesOfType(ComputerUsageType type){
		return mongoTemplate.count(query(where(Computer.getUsageClassfierField(ComputerUsageClassifier.USAGE_TYPE_FIELD)).is(type)), Computer.class);
	}
	@Override
	public long getNumberOfMachinesOfTypeBeforeTime(ComputerUsageType type, DateTime time){
		return mongoTemplate.count(query(where(Computer.getUsageClassfierField(ComputerUsageClassifier.USAGE_TYPE_FIELD)).is(type).andOperator(where(Computer.WHEN_CREATED_FIELD).lt(time))), Computer.class);
	}
	@Override
	public long getNumberOfMachinesBeforeTime(DateTime time){
		return mongoTemplate.count(query(where(Computer.WHEN_CREATED_FIELD).lt(time)), Computer.class);
	}
	@Override
	public long getNumberOfSensitiveMachinesBeforeTime(DateTime time){ 
		return mongoTemplate.count(query(where(Computer.SENSITIVE_MACHINE_FIELD).is(true).andOperator(where(Computer.WHEN_CREATED_FIELD).lt(time))), Computer.class);
	}
	@Override
	public long getNumberOfSensitiveMachines(){
		return mongoTemplate.count(query(where(Computer.SENSITIVE_MACHINE_FIELD).is(true)), Computer.class);
	}	
	
	public Computer getComputerWithPartialFields(String machineName, String... includeFields) {
		List<Computer> computers = getComputersWithPartialFields(Arrays.asList(machineName), includeFields);
		return (computers==null || computers.isEmpty())? null : computers.get(0);
	}
	
	public List<Computer> getComputersWithPartialFields(List<String> machineNames, String... includeFields) {
		Query query = new Query();
		query.addCriteria(where(Computer.NAME_FIELD).in(machineNames));
		query.fields().include(Computer.NAME_FIELD);
		for (String includeField : includeFields)
			query.fields().include(includeField);
		
		return mongoTemplate.find(query, Computer.class);
	}
	
	@Override
	public void updateSensitiveMachineByName(String machineName, boolean isSensitive) {
		mongoTemplate.updateFirst(query(where(Computer.NAME_FIELD).is(machineName)), update(Computer.SENSITIVE_MACHINE_FIELD, isSensitive), Computer.class);
	}
	
	public boolean findIfComputerExists(String computerName){
		Query query = new Query(where(Computer.NAME_FIELD).is(computerName));
		query.fields().include(Computer.ID_FIELD);
		return !(mongoTemplate.find(query, ComputerIdWrapper.class, Computer.COLLECTION_NAME).isEmpty());
	}

	private Query createFiltersQuery(String nameContains, String distinguishedNameContains, String fields, String usageTypes, String usageTypesAnd, Integer limit) {
		Query query = new Query();

		// Add name regex condition
		if (nameContains != null) {
			query.addCriteria(where(Computer.NAME_FIELD)
					.regex(Pattern.compile(nameContains, Pattern.CASE_INSENSITIVE)));
		}

		// Add distinguishedName regex condition
		if (distinguishedNameContains != null) {
			query.addCriteria(where(Computer.DISTINGUISHED_NAME_FIELD)
					.regex(Pattern.compile(distinguishedNameContains, Pattern.CASE_INSENSITIVE)));
		}

		// Add fields list
		if (fields != null) {
			List<String> fieldsList = Arrays.asList(fields.split(","));
			fieldsList.forEach(field -> query.fields().include(field));
		}

		// Add usage types filter
		if (usageTypes != null) {
			String[] usages = usageTypes.split(",");
			query.addCriteria(where(Computer.USAGE_CLASSIFIERS_FIELD + "." +
					ComputerUsageClassifier.USAGE_TYPE_FIELD).in(usages));
		}

		// Add usage types filter with an AND operator
		if (usageTypesAnd != null) {
			String[] usages = usageTypesAnd.split(",");
			List<Criteria> criteriasList = new ArrayList<>();
			Arrays.asList(usages).forEach(usageType -> {
				criteriasList.add(where(Computer.USAGE_CLASSIFIERS_FIELD + "." +
						ComputerUsageClassifier.USAGE_TYPE_FIELD).is(usageType));
			});

			Criteria[] criterias = new Criteria[criteriasList.size()];
			criteriasList.toArray(criterias);
			query.addCriteria(new Criteria().andOperator(criterias));
		}

		if (limit != null) {
			query.limit(limit);
		}

		return query;
	}

	@Override
	public List<Computer> findByFilters(String nameContains, String distinguishedNameContains, String fields,
			String usageTypes, String usageTypesAnd, Integer limit) {

		Query query = createFiltersQuery(nameContains, distinguishedNameContains, fields, usageTypes, usageTypesAnd, limit);
		return mongoTemplate.find(query, Computer.class, Computer.COLLECTION_NAME);
	}

	class ComputerIdWrapper{
		private String id;
		
		public String getId(){
			return id;
		}
		public void setId(String id){
			this.id = id;
		}
	}
	
}
