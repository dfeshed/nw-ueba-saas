package presidio.data.domain.event.dlpfile;

/**
 * Events generator domain, contains all fields for DLPFileOperation generator
 */
public class DLPFileOperation {
    private String source_file_name;
    private String destination_file_name;
    private String source_path;
    private String destination_path;
    private long file_size;
    private String event_type;

    public DLPFileOperation(String source_file_name, String destination_file_name, String source_path, String destination_path, long file_size, String event_type) {
        this.source_file_name = source_file_name;
        this.destination_file_name = destination_file_name;
        this.source_path = source_path;
        this.destination_path = destination_path;
        this.file_size = file_size;
        this.event_type = event_type;
    }

    public String getSource_file_name() {
        return source_file_name;
    }

    public void setSource_file_name(String source_file_name) {
        this.source_file_name = source_file_name;
    }

    public String getDestination_file_name() {
        return destination_file_name;
    }

    public void setDestination_file_name(String destination_file_name) {
        this.destination_file_name = destination_file_name;
    }

    public String getSource_path() {
        return source_path;
    }

    public void setSource_path(String source_path) {
        this.source_path = source_path;
    }

    public String getDestination_path() {
        return destination_path;
    }

    public void setDestination_path(String destination_path) {
        this.destination_path = destination_path;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public void setFile_size(long file_size) { this.file_size = file_size;}

    public String getEvent_type() { return event_type; }

    public void setEvent_type(String event_type) { this.event_type = event_type; }

    @Override
    public String toString() {
        return
                "," + source_file_name +
                "," + destination_file_name +
                "," + source_path +
                "," + destination_path +
                "," + file_size +
                "," + event_type;
    }
}
