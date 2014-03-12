package fortscale.domain.tracer;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FilterSettings {

	private List<String> accounts = new LinkedList<String>();
	private ListMode accountsListMode = ListMode.Include;
	private List<String> machines = new LinkedList<String>();
	private ListMode machinesListMode = ListMode.Include;
	private long start;
	private long end;
	private List<String> sources = new LinkedList<String>();
	private ListMode sourcesListMode = ListMode.Include;
	private ScoreRange scoreRange = null;
	
	public List<String> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<String> accounts) {
		if (accounts==null)
			this.accounts.clear();
		else
			this.accounts = accounts;
	}
	public ListMode getAccountsListMode() {
		return accountsListMode;
	}
	public void setAccountsListMode(ListMode accountsListMode) {
		this.accountsListMode = accountsListMode;
	}
	public List<String> getMachines() {
		return machines;
	}
	public void setMachines(List<String> machines) {
		if (machines==null)
			this.machines.clear();
		else
			this.machines = machines;
	}
	public ListMode getMachinesListMode() {
		return machinesListMode;
	}
	public void setMachinesListMode(ListMode machinesListMode) {
		this.machinesListMode = machinesListMode;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public List<String> getSources() {
		return sources;
	}
	public void setSources(List<String> sources) {
		if (sources==null)
			this.sources.clear();
		else
			this.sources = sources;
	}
	public ListMode getSourcesListMode() {
		return sourcesListMode;
	}
	public void setSourcesListMode(ListMode sourcesListMode) {
		this.sourcesListMode = sourcesListMode;
	}
	public ScoreRange getScoreRange() {
		return scoreRange;
	}
	public void setScoreRange(ScoreRange scoreRange) {
		this.scoreRange = scoreRange;
	}
}
