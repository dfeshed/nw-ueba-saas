package com.rsa.netwitness.presidio.automation.domain.dlpfile;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;


@Document(collection = "enriched_dlpfile")
public class DlpFileEnrichStoredData {

    @Id
    private String id;

    @Field("startInstant")
    public Instant dateTime;

    @Expose
    private String normalizedUsername;

    @Expose
    private String normalizedSrcMachine;
    @Expose
    private String source_path;
    @Expose
    private String source_file_name;
    @Expose
    private String source_drive_type;
    @Expose
    private String destination_path;
    @Expose
    private String destination_file_name;
    @Expose
    private String destination_drive_type;
    @Expose
    private double file_size;
    @Expose
    private String event_type;
    @Expose
    private boolean was_blocked;
    @Expose
    private boolean was_classified;
    @Expose
    private String malware_scan_result;
    @Expose
    private String executing_application;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getNormalizedSrcMachine() {
        return normalizedSrcMachine;
    }

    public void setNormalizedSrcMachine(String normalizedSrcMachine) {
        this.normalizedSrcMachine = normalizedSrcMachine;
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

    public String toString() {
        return  normalizedUsername + ',' +
                normalizedSrcMachine + ',' +
                source_path + ',' +
                source_file_name + ',' +
                source_drive_type + ',' +
                destination_path + ',' +
                destination_file_name + ',' +
                destination_drive_type + ',' +
                file_size + ',' +
                event_type + ',' +
                was_blocked + ',' +
                was_classified + ',' +
                malware_scan_result + ',' +
                executing_application;
    }
}
