package presidio.ui.presidiouiapp.beans;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DataBean<T> {
	private T data;
	private int total = 1;
	private int offset = 0;
	private List<WarningMessage> warning = new java.util.ArrayList<>();
	private Map<String, Object> info;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Map<String, Object> getInfo() {
		return info;
	}

	public void setInfo(Map<String, Object> info) {
		this.info = info;
	}

	public void addInfo(String infoKey, Object infoValue) {

		if (this.info == null){
			this.info = new HashMap<>();
		}

		this.info.put(infoKey, infoValue);

	}

	public WarningMessage getWarning() {

		if (warning.size()>0)
			return warning.get(0);
		return null;
	}

	public void addWarning(WarningMessage warning) {
		WarningMessage warningMsg = new WarningMessage(warning.getCode(), warning.getMessage());
		this.warning.add(warningMsg);
	}
	
	public void addWarning(DataWarningsEnum warningType) {
		WarningMessage warningMsg = new WarningMessage(warningType.getCode(), warningType.getMessage());
		this.warning.add(warningMsg);
	}

	public void addWarning(DataWarningsEnum warningType, String warningMessage) {
		WarningMessage warningMsg = new WarningMessage(warningType.getCode(), String.format("%s - %s",warningType.getMessage(), warningMessage));
		this.warning.add(warningMsg);
	}

	public void setWarning(List<WarningMessage> warning) {
		this.warning = warning;
	}

	public class WarningMessage {
		private int code;
		private String message;
		
		public WarningMessage(int code, String message) {
			this.code = code;
			this.message = message;
		}
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}
}
