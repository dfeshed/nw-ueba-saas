package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoPrintLogEvent extends DemoGenericEvent {

	private Computer srcMachine;
	private String[] dstMachines;
	private int fileSize;
	private int totalPages;
	private String fileName;
	private String status;

	public DemoPrintLogEvent() {}

	public DemoPrintLogEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
							 String[] dstMachines, int fileSize, int totalPages, String fileName, String status) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.fileSize = fileSize;
		this.totalPages = totalPages;
		this.fileName = fileName;
		this.status = status;
	}

	public DemoPrintLogEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
							 String dstMachine, int fileSize, int totalPages, String fileName, String status) {
		this(user, score, reason, srcMachine, new String[] { dstMachine }, fileSize, totalPages, fileName, status);
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String[] getDstMachines() {
		return dstMachines;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public String getFileName() {
		return fileName;
	}

	public String getStatus() {
		return status;
	}

	public void setSrcMachine(Computer srcMachine) {
		this.srcMachine = srcMachine;
	}

	public void setDstMachines(String[] dstMachines) {
		this.dstMachines = dstMachines;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case DEST: return dstMachines[0];
			case SOURCE: return srcMachine.getName();
			case FILE_SIZE: return fileSize + "";
			case TOTAL_PAGES: return totalPages + "";
			default: return null;
		}
	}

}