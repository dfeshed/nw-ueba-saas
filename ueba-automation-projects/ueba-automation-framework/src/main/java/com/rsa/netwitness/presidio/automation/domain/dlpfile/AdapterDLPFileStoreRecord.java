package com.rsa.netwitness.presidio.automation.domain.dlpfile;

import presidio.data.domain.event.Event;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by presidio on 7/9/17.
 */
//TODO: see if can use DLPFileEvent class instead of this
public class AdapterDLPFileStoreRecord extends Event {

    private Instant date_time;
    private String event_type;
    private String event_id;
    private String username;
    private String first_name;
    private String last_name;
    private String src_machine;
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

    //@Override
    public String toCSV31() {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));

        return ",," +
                // For 3.1 date should be in format: 2017-06-30T10:29:00Z
                formatter.format(date_time) + ',' +
                executing_application + ',' +
                src_machine + ',' +
                ",,,,,," +
                first_name + ',' +
                last_name + ',' +
                "," +
                username + ',' +
                ",,,,,,,," +
                malware_scan_result + ',' +
                ",,,,,,,," +
                event_id + ',' +
                source_ip + ',' +
                ",,,," +
                event_type + ',' +
                ",,," +
                was_blocked + ',' +
                was_classified + ',' +
                ",,,,,,,,,,,,,,,,,,,,,,,,," +
                destination_path + ',' +
                destination_file_name + ',' +
                ",,," +
                file_size + ',' +
                ",,,,,,," +
                source_path + ',' +
                source_file_name + ',' +
                ",,,,,,,,,,," +
                source_drive_type + ',' +
                "," +
                destination_drive_type +
                ",,,,,,,";
    }

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

    public Instant getDateTime() {
        return date_time;
    }

    public void setDateTime(Instant date_time) {
        this.date_time = date_time;
    }

    public String getEventType() {
        return event_type;
    }

    public void setEventType(String event_type) {
        this.event_type = event_type;
    }

    public String getEventId() {
        return event_id;
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

    public String getSource_ip() {
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

    public boolean isWasClassified() {
        return was_classified;
    }

    public void setWasClassified(boolean was_classified) {
        this.was_classified = was_classified;
    }

    public boolean isWasBlocked() {
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
}
