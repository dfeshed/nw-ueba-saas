package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@Document(collection = Alert.COLLECTION_NAME)
@CompoundIndexes({
	@CompoundIndex(name="entity_type_entity_name_desc", def = "{'entity_type': 1, 'entity_name': -1}"),
	@CompoundIndex(name="ts_desc", def = "{'ts': -1}"),
	@CompoundIndex(name="dismissed_ts_desc", def = "{'dismissed': 1, 'ts': -1}", sparse=true)
})
public class Alert extends AbstractDocument implements Serializable {



	private static final long serialVersionUID = -8514041678913795872L;
	public static final String COLLECTION_NAME = "Alerts";

	@JsonIgnore
	@Indexed(unique=true)
	private String uuid;
	@Indexed(unique=false)
	private long ts_start;
	@Indexed(unique=false)
	private long ts_end;
	private EntityType entity_type;
	private String entity_name;
	private String rule;
	private Map<Long, String> evidences;
	private String cause;
	private int score;
	private AlertSeverity severity;
	private String status;
	private String comment;

	public Alert() {}

	public Alert(String uuid, long ts_start, long ts_end, EntityType entity_type, String entity_name, String rule, Map<Long, String> evidences, String cause, int score, AlertSeverity severity, String status, String comment) {
		this.uuid = uuid;
		this.ts_start = ts_start;
		this.ts_end = ts_end;
		this.entity_type = entity_type;
		this.entity_name = entity_name;
		this.rule = rule;
		this.evidences = evidences;
		this.cause = cause;
		this.score = score;
		this.severity = severity;
		this.status = status;
		this.comment = comment;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getTs_start() {
		return ts_start;
	}

	public void setTs_start(long ts_start) {
		this.ts_start = ts_start;
	}

	public long getTs_end() {
		return ts_end;
	}

	public void setTs_end(long ts_end) {
		this.ts_end = ts_end;
	}

	public EntityType getEntity_type() {
		return entity_type;
	}

	public void setEntity_type(EntityType entity_type) {
		this.entity_type = entity_type;
	}

	public String getEntity_name() {
		return entity_name;
	}

	public void setEntity_name(String entity_name) {
		this.entity_name = entity_name;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Map<Long, String> getEvidences() {
		return evidences;
	}

	public void setEvidences(Map<Long, String> evidences) {
		this.evidences = evidences;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public AlertSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(AlertSeverity severity) {
		this.severity = severity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}