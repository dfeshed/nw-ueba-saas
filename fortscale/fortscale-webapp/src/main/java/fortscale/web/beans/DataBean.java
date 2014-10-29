package fortscale.web.beans;

import java.util.Map;

public class DataBean<T> {
	private T data;
	private int total = 1;
	private int offset = 0;
	private WarningMessage warning;
    public Map<String, Object> info;

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

	
	public WarningMessage getWarning() {
		return warning;
	}

	public void setWarning(WarningMessage warning) {
		this.warning = warning;
	}
	
	public void setWarning(DataWarningsEnum warning) {
		this.warning = new WarningMessage(warning.getCode(), warning.getMessage());
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
