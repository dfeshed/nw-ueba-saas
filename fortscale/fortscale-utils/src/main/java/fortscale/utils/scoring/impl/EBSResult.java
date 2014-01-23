package fortscale.utils.scoring.impl;

import java.util.List;
import java.util.Map;

import fortscale.utils.scoring.IEBSResult;

public class EBSResult implements IEBSResult{
	private final List<Map<String, Object>> resultsList;
	private final Double globalScore;
	private final int offset;
	private final int total;
	
	public EBSResult(List<Map<String, Object>> resultsList, Double globalScore, int offset, int total) {
		this.resultsList = resultsList;
		this.globalScore = globalScore;
		this.offset = offset;
		this.total = total;
	}

	public List<Map<String, Object>> getResultsList() {
		return resultsList;
	}

	public Double getGlobalScore() {
		return globalScore;
	}

	public int getOffset() {
		return offset;
	}

	public int getTotal() {
		return total;
	}
}
