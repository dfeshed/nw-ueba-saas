package fortscale.services;

import java.util.Date;

public interface IUserScoreHistoryElement {
	public Date getDate();
	public int getScore();
	public int getAvgScore();
}
