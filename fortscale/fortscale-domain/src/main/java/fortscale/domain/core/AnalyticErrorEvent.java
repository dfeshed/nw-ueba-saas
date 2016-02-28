package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection=AnalyticErrorEvent.collectionName)
public class AnalyticErrorEvent extends AnalyticEvent {

    public static final String fileNameField = "fileName";
    public static final String lineNumberField = "lineNumber";
    public static final String columnNumberField = "columnNumber";
    public static final String messageField = "message";
    public static final String stackField = "stack";

    @Field(fileNameField)
    private String fileName;
    @Field(lineNumberField)
    private int lineNumber;
    @Field(columnNumberField)
    private int columnNumber;
    @Field(messageField)
    private String message;
    @Field(stackField)
    private String stack;

    public static String getFileNameField() {
        return fileNameField;
    }

    public static String getLineNumberField() {
        return lineNumberField;
    }

    public static String getColumnNumberField() {
        return columnNumberField;
    }

    public static String getMessageField() {
        return messageField;
    }

    public static String getStackField() {
        return stackField;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }


    public AnalyticErrorEvent () {}

    public AnalyticErrorEvent (AnalyticErrorEvent analyticErrorEvent) {
        super(analyticErrorEvent);

        this.fileName = analyticErrorEvent.fileName;
        this.lineNumber = analyticErrorEvent.lineNumber;
        this.columnNumber = analyticErrorEvent.columnNumber;
        this.message = analyticErrorEvent.message;
        this.stack = analyticErrorEvent.stack;
    }

    public AnalyticErrorEvent (
            @JsonProperty(AnalyticClickEvent.eventTypeField) String eventType,
            @JsonProperty(AnalyticClickEvent.computerIdField) String computerId,
            @JsonProperty(AnalyticClickEvent.tabIdField) String tabId,
            @JsonProperty(AnalyticClickEvent.stateNameField) String stateName,
            @JsonProperty(AnalyticClickEvent.timeStampField) long timeStamp,
            @JsonProperty(AnalyticErrorEvent.fileNameField) String fileName,
            @JsonProperty(AnalyticErrorEvent.lineNumberField) int lineNumber,
            @JsonProperty(AnalyticErrorEvent.columnNumberField) int columnNumber,
            @JsonProperty(AnalyticErrorEvent.messageField) String message,
            @JsonProperty(AnalyticErrorEvent.stackField) String stack) {
        super(eventType, computerId, tabId, stateName, timeStamp);

        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.message = message;
        this.stack = stack;
    }


}
