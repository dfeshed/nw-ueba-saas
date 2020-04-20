package com.rsa.netwitness.presidio.automation.domain.file;

import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.data.domain.event.Event;

import java.time.Instant;

@Document(collection = "netwitness_file_events")
public class NetwitnessFileStoredData extends Event {

    @Id
    private String id;
    private String className;

    @Expose
    private NetwitnessEvent netwitnessEvent;

    @Override
    public String toString() {
        return "NetwitnessFileStoredData{" +
                "id='" + id + '\'' +
                ", className='" + className + '\'' +
                ", netwitnessEvent=" + netwitnessEvent.toString() +
                '}';
    }

    @Override
    public Instant getDateTime() {
        return netwitnessEvent.mongo_source_event_time;
    }

    private NetwitnessFileStoredData(Builder builder) {
        this.id = builder.id;
        this.netwitnessEvent = builder.netwitnessEvent;
    }


    class NetwitnessEvent {
        @Expose
        private Instant mongo_source_event_time;

        @Expose
        private String event_source_id;

        @Expose
        private String user_dst;

        @Expose
        private String event_type;

        @Expose
        private String reference_id;

        @Expose
        private String obj_name;

        @Expose
        private String result_code;

        @Expose
        private String device_type;

        @Expose
        private String category;

        @Expose
        private String accesses;

        @Expose
        private String event_time;

        public Instant getMongo_source_event_time() {
            return mongo_source_event_time;
        }

        public void setMongo_source_event_time(Instant mongo_source_event_time) {
            this.mongo_source_event_time = mongo_source_event_time;
        }

        public String getEvent_source_id() {
            return event_source_id;
        }

        public void setEvent_source_id(String event_source_id) {
            this.event_source_id = event_source_id;
        }

        public String getUser_dst() {
            return user_dst;
        }

        public void setUser_dst(String user_dst) {
            this.user_dst = user_dst;
        }

        public String getEvent_type() {
            return event_type;
        }

        public void setEvent_type(String event_type) {
            this.event_type = event_type;
        }

        public String getReference_id() {
            return reference_id;
        }

        public void setReference_id(String reference_id) {
            this.reference_id = reference_id;
        }

        public String getObj_name() {
            return obj_name;
        }

        public void setObj_name(String obj_name) {
            this.obj_name = obj_name;
        }

        public String getResult_code() {
            return result_code;
        }

        public void setResult_code(String result_code) {
            this.result_code = result_code;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getAccesses() {
            return accesses;
        }

        public void setAccesses(String accesses) {
            this.accesses = accesses;
        }

        public String getEvent_time() {
            return event_time;
        }

        public void setEvent_time(String event_time) {
            this.event_time = event_time;
        }

        @Override
        public String toString() {
            return "NetwitnessEvent{" +
                    "mongo_source_event_time=" + mongo_source_event_time +
                    ", event_source_id='" + event_source_id + '\'' +
                    ", user_dst='" + user_dst + '\'' +
                    ", event_type='" + event_type + '\'' +
                    ", reference_id='" + reference_id + '\'' +
                    ", obj_name='" + obj_name + '\'' +
                    ", result_code='" + result_code + '\'' +
                    ", device_type='" + device_type + '\'' +
                    ", category='" + category + '\'' +
                    ", accesses='" + accesses + '\'' +
                    ", event_time='" + event_time + '\'' +
                    '}';
        }
    }

    public static class Builder {

        private String id;
        private NetwitnessEvent netwitnessEvent;

        public Builder() {

        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }


        public Builder additionalInfo(NetwitnessEvent netwitnessEvent) {
            this.netwitnessEvent = netwitnessEvent;
            return this;
        }

        public NetwitnessFileStoredData build() {
            return new NetwitnessFileStoredData(this);
        }
    }
}
