package fortscale.utils.qradar.responses;

/**
 * Created by Amir Keren on 3/3/16.
 */
public class SearchResponse {

	public enum Status { EXECUTE, COMPLETED,WAIT }

	private int progress;
	private int query_execution_time;
	private int record_count;
	private int data_file_count;
	private String cursor_id;
	private boolean save_results;
	private int index_total_size;
	private Status status;
	private String search_id;
	private long data_total_size;
	private int index_file_count;
	private long desired_retention_time_msec;
	private long compressed_data_total_size;
	private int processed_record_count;
	private int compressed_data_file_count;

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getQuery_execution_time() {
		return query_execution_time;
	}

	public void setQuery_execution_time(int query_execution_time) {
		this.query_execution_time = query_execution_time;
	}

	public int getRecord_count() {
		return record_count;
	}

	public void setRecord_count(int record_count) {
		this.record_count = record_count;
	}

	public int getData_file_count() {
		return data_file_count;
	}

	public void setData_file_count(int data_file_count) {
		this.data_file_count = data_file_count;
	}

	public String getCursor_id() {
		return cursor_id;
	}

	public void setCursor_id(String cursor_id) {
		this.cursor_id = cursor_id;
	}

	public boolean isSave_results() {
		return save_results;
	}

	public void setSave_results(boolean save_results) {
		this.save_results = save_results;
	}

	public int getIndex_total_size() {
		return index_total_size;
	}

	public void setIndex_total_size(int index_total_size) {
		this.index_total_size = index_total_size;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = Status.valueOf(status);
	}

	public String getSearch_id() {
		return search_id;
	}

	public void setSearch_id(String search_id) {
		this.search_id = search_id;
	}

	public long getData_total_size() {
		return data_total_size;
	}

	public void setData_total_size(long data_total_size) {
		this.data_total_size = data_total_size;
	}

	public int getIndex_file_count() {
		return index_file_count;
	}

	public void setIndex_file_count(int index_file_count) {
		this.index_file_count = index_file_count;
	}

	public long getDesired_retention_time_msec() {
		return desired_retention_time_msec;
	}

	public void setDesired_retention_time_msec(long desired_retention_time_msec) {
		this.desired_retention_time_msec = desired_retention_time_msec;
	}

	public long getCompressed_data_total_size() {
		return compressed_data_total_size;
	}

	public void setCompressed_data_total_size(long compressed_data_total_size) {
		this.compressed_data_total_size = compressed_data_total_size;
	}

	public int getProcessed_record_count() {
		return processed_record_count;
	}

	public void setProcessed_record_count(int processed_record_count) {
		this.processed_record_count = processed_record_count;
	}

	public int getCompressed_data_file_count() {
		return compressed_data_file_count;
	}

	public void setCompressed_data_file_count(int compressed_data_file_count) {
		this.compressed_data_file_count = compressed_data_file_count;
	}

}