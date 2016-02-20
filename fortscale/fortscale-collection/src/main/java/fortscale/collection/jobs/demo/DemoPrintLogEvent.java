package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

import java.util.Random;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoPrintLogEvent extends DemoGenericEvent {

	private static final String DEFAULT_STATUS = "spooling";

	private Computer srcMachine;
	private String[] dstMachines;
	private int minFileSize;
	private int maxFileSize;
	private int minTotalPages;
	private int maxTotalPages;
	private String status;
	private String fileName;
	private int fileSize;
	private int totalPages;

	public DemoPrintLogEvent() {}

	private DemoPrintLogEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
							  String[] dstMachines, int minFileSize, int maxFileSize, int minTotalPages,
							  int maxTotalPages, String status) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.minFileSize = minFileSize;
		this.maxFileSize = maxFileSize;
		this.minTotalPages = minTotalPages;
		this.maxTotalPages = maxTotalPages;
		this.status = status;
		this.fileName = user.getUsername().split("@")[0] + ".txt";
	}

	public static DemoPrintLogEvent createBaseLineConfiguration(User user, Computer srcMachine, String[] dstMachines,
																int minFileSize, int maxFileSize, int minTotalPages,
																int maxTotalPages) {
		return new DemoPrintLogEvent(user, DemoUtils.DEFAULT_SCORE, DemoUtils.EventFailReason.NONE, srcMachine,
				dstMachines, minFileSize, maxFileSize, minTotalPages, maxTotalPages, DEFAULT_STATUS);
	}

	public static DemoPrintLogEvent createAnomalyConfiguration(User user, Computer srcMachine, String[] dstMachines,
															   int minFileSize, int maxFileSize, int minTotalPages,
															   int maxTotalPages, DemoUtils.EventFailReason reason,
															   int score) {
		return new DemoPrintLogEvent(user, score, reason, srcMachine, dstMachines, minFileSize, maxFileSize,
				minTotalPages, maxTotalPages, DEFAULT_STATUS);
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String[] getDstMachines() {
		return dstMachines;
	}

	public String getDstMachine() {
		return dstMachines[0];
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

	@Override
	protected DemoGenericEvent generateEvent() {
		Random random = new Random();
		DemoPrintLogEvent demoGenericEvent = new DemoPrintLogEvent(user, score, reason, srcMachine,
				dstMachines, minFileSize, maxFileSize, minTotalPages, maxTotalPages, status);
		if (minFileSize == maxFileSize) {
			fileSize = minFileSize;
		} else {
			fileSize = random.nextInt(maxFileSize - minFileSize) + minFileSize;
		}
		if (minTotalPages == maxTotalPages) {
			totalPages = minTotalPages;
		} else {
			totalPages = random.nextInt(maxTotalPages - minTotalPages) + minTotalPages;
		}
		demoGenericEvent.setFileSize(fileSize);
		demoGenericEvent.setTotalPages(totalPages);
		return demoGenericEvent;
	}

	public int getMinFileSize() {
		return minFileSize;
	}

	public void setMinFileSize(int minFileSize) {
		this.minFileSize = minFileSize;
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public int getMinTotalPages() {
		return minTotalPages;
	}

	public void setMinTotalPages(int minTotalPages) {
		this.minTotalPages = minTotalPages;
	}

	public int getMaxTotalPages() {
		return maxTotalPages;
	}

	public void setMaxTotalPages(int maxTotalPages) {
		this.maxTotalPages = maxTotalPages;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

}