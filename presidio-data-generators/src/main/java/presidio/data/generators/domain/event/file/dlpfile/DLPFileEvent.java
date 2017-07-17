package presidio.data.generators.domain.event.file.dlpfile;

import presidio.data.generators.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

/**
 * Events generator domain, contains all possible fields for all components
 */
public class DLPFileEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant date_time;
    private String event_type;
    private String event_id;
    private String username;
    private String normalized_username;
    private String first_name;
    private String last_name;
    private String src_machine;
    private String normalized_src_machine;
    private String source_ip;
    private String executing_application;
    private String source_path;
    private String destination_path;
    private String source_file_name;
    private String destination_file_name;
    private long file_size;
    private String source_drive_type;
    private String destination_drive_type;
    private boolean was_classified;
    private boolean was_blocked;
    private String malware_scan_result;

    public String getNormalized_src_machine() {
        return normalized_src_machine;
    }

    public void setNormalized_src_machine(String normalized_src_machine) {
        this.normalized_src_machine = normalized_src_machine;
    }

    /****
     * Constructors
     ****/


    public DLPFileEvent (Instant evTime, String username) {
        super();
        this.date_time = evTime;
        this.username = username;
    }

    public String getNormalizedUsername() {
        return normalized_username;
    }

    public void setNormalizedUsername(String normalized_username) {
        this.normalized_username = normalized_username;
    }

    public Instant getDateTime() {
        return date_time;
    }

    public void setDateTime(Instant date_time) {
        this.date_time = date_time;
    }

    public String getEventType() {
        return event_type;
    }

    public String getEventId() {
        return event_id;
    }

    /**
     * All getters and setters
    **/
    
    public void setEventType(String event_type) {
        this.event_type = event_type;
    }

    public void setEventId(String event_id) {
        this.event_id = event_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getSrcMachine() {
        return src_machine;
    }

    public void setSrcMachine(String src_machine) {
        this.src_machine = src_machine;
    }

    public String getSourceIp() {
        return source_ip;
    }

    public void setSourceIp(String source_ip) {
        this.source_ip = source_ip;
    }

    public String getExecutingApplication() {
        return executing_application;
    }

    public void setExecutingApplication(String executing_application) {
        this.executing_application = executing_application;
    }

    public String getSourcePath() {
        return source_path;
    }

    public void setSourcePath(String source_path) {
        this.source_path = source_path;
    }

    public String getDestinationPath() {
        return destination_path;
    }

    public void setDestinationPath(String destination_path) {
        this.destination_path = destination_path;
    }

    public String getSourceFileName() {
        return source_file_name;
    }

    public void setSourceFileName(String source_file_name) {
        this.source_file_name = source_file_name;
    }

    public String getDestinationFileName() {
        return destination_file_name;
    }

    public void setDestinationFileName(String destination_file_name) {
        this.destination_file_name = destination_file_name;
    }

    public long getFileSize() {
        return file_size;
    }

    public void setFileSize(long file_size) {
        this.file_size = file_size;
    }

    public String getSourceDriveType() {
        return source_drive_type;
    }

    public void setSourceDriveType(String source_drive_type) {
        this.source_drive_type = source_drive_type;
    }

    public String getDestinationDriveType() {
        return destination_drive_type;
    }

    public void setDestinationDriveType(String destination_drive_type) {
        this.destination_drive_type = destination_drive_type;
    }

    public boolean getWasClassified() {
        return was_classified;
    }

    public void setWasClassified(boolean was_classified) {
        this.was_classified = was_classified;
    }

    public boolean getWasBlocked() {
        return was_blocked;
    }

    public void setWasBlocked(boolean was_blocked) {
        this.was_blocked = was_blocked;
    }

    public String getMalwareScanResult() {
        return malware_scan_result;
    }

    public void setMalwareScanResult(String malware_scan_result) {
        this.malware_scan_result = malware_scan_result;
    }

    public boolean isWasClassified() {
        return was_classified;
    }

    public boolean isWasBlocked() {
        return was_blocked;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    @Override
    public String toString() {
        return date_time.toString() + ',' +
                event_type + ',' +
                executing_application + ',' +
                src_machine + ',' +
                first_name + ',' +
                last_name + ',' +
                username + ',' +
                malware_scan_result + ',' +
                event_id + ',' +
                source_ip + ',' +
                was_blocked + ',' +
                was_classified + ',' +
                destination_path + ',' +
                destination_file_name + ',' +
                file_size + ',' +
                source_path + ',' +
                source_file_name + ',' +
                source_drive_type + ',' +
                destination_drive_type ;
    }

}
