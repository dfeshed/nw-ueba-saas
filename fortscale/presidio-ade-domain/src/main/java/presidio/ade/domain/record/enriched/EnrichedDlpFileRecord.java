package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.scanning.AdeRecordMetadata;

import java.time.Instant;

/**
 * The enriched DLP file event POJO.
 *
 * Created by Lior Govrin on 05/06/2017.
 */
@Document
@AdeRecordMetadata(type ="dlp_file")
public class EnrichedDlpFileRecord extends EnrichedRecord {
	public static final String NORMALIZED_USERNAME_FIELD = "normalized_username";
	public static final String NORMALIZED_SRC_MACHINE_FIELD = "normalized_src_machine";
	public static final String SOURCE_PATH_FIELD = "source_path";
	public static final String SOURCE_FILE_NAME_FIELD = "source_file_name";
	public static final String SOURCE_DRIVE_TYPE_FIELD = "source_drive_type";
	public static final String DESTINATION_PATH_FIELD = "destination_path";
	public static final String DESTINATION_FILE_NAME_FIELD = "destination_file_name";
	public static final String DESTINATION_DRIVE_TYPE_FIELD = "destination_drive_type";
	public static final String FILE_SIZE_FIELD = "file_size";
	public static final String EVENT_TYPE_FIELD = "event_type";
	public static final String WAS_BLOCKED_FIELD = "was_blocked";
	public static final String WAS_CLASSIFIED_FIELD = "was_classified";
	public static final String MALWARE_SCAN_RESULT_FIELD = "malware_scan_result";
	public static final String EXECUTING_APPLICATION_FIELD = "executing_application";

	@Indexed @Field(NORMALIZED_USERNAME_FIELD)
	private String normalized_username;
	@Field(NORMALIZED_SRC_MACHINE_FIELD)
	private String normalized_src_machine;
	@Field(SOURCE_PATH_FIELD)
	private String source_path;
	@Field(SOURCE_FILE_NAME_FIELD)
	private String source_file_name;
	@Field(SOURCE_DRIVE_TYPE_FIELD)
	private String source_drive_type;
	@Field(DESTINATION_PATH_FIELD)
	private String destination_path;
	@Field(DESTINATION_FILE_NAME_FIELD)
	private String destination_file_name;
	@Field(DESTINATION_DRIVE_TYPE_FIELD)
	private String destination_drive_type;
	@Field(FILE_SIZE_FIELD)
	private double file_size;
	@Field(EVENT_TYPE_FIELD)
	private String event_type;
	@Field(WAS_BLOCKED_FIELD)
	private boolean was_blocked;
	@Field(WAS_CLASSIFIED_FIELD)
	private boolean was_classified;
	@Field(MALWARE_SCAN_RESULT_FIELD)
	private String malware_scan_result;
	@Field(EXECUTING_APPLICATION_FIELD)
	private String executing_application;

	/**
	 * C'tor.
	 *
	 * @param date_time The record's logical time
	 */
	public EnrichedDlpFileRecord(Instant date_time) {
		super(date_time);
	}

	@Override
	@Transient
	public String getDataSource() {
		return "dlpfile";
	}

	public String getNormalized_username() {
		return normalized_username;
	}

	public void setNormalized_username(String normalized_username) {
		this.normalized_username = normalized_username;
	}

	public String getNormalized_src_machine() {
		return normalized_src_machine;
	}

	public void setNormalized_src_machine(String normalized_src_machine) {
		this.normalized_src_machine = normalized_src_machine;
	}

	public String getSource_path() {
		return source_path;
	}

	public void setSource_path(String source_path) {
		this.source_path = source_path;
	}

	public String getSource_file_name() {
		return source_file_name;
	}

	public void setSource_file_name(String source_file_name) {
		this.source_file_name = source_file_name;
	}

	public String getSource_drive_type() {
		return source_drive_type;
	}

	public void setSource_drive_type(String source_drive_type) {
		this.source_drive_type = source_drive_type;
	}

	public String getDestination_path() {
		return destination_path;
	}

	public void setDestination_path(String destination_path) {
		this.destination_path = destination_path;
	}

	public String getDestination_file_name() {
		return destination_file_name;
	}

	public void setDestination_file_name(String destination_file_name) {
		this.destination_file_name = destination_file_name;
	}

	public String getDestination_drive_type() {
		return destination_drive_type;
	}

	public void setDestination_drive_type(String destination_drive_type) {
		this.destination_drive_type = destination_drive_type;
	}

	public double getFile_size() {
		return file_size;
	}

	public void setFile_size(double file_size) {
		this.file_size = file_size;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public boolean isWas_blocked() {
		return was_blocked;
	}

	public void setWas_blocked(boolean was_blocked) {
		this.was_blocked = was_blocked;
	}

	public boolean isWas_classified() {
		return was_classified;
	}

	public void setWas_classified(boolean was_classified) {
		this.was_classified = was_classified;
	}

	public String getMalware_scan_result() {
		return malware_scan_result;
	}

	public void setMalware_scan_result(String malware_scan_result) {
		this.malware_scan_result = malware_scan_result;
	}

	public String getExecuting_application() {
		return executing_application;
	}

	public void setExecuting_application(String executing_application) {
		this.executing_application = executing_application;
	}
}
