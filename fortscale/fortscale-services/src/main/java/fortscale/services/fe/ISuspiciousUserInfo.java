package fortscale.services.fe;

import java.util.Comparator;


public interface ISuspiciousUserInfo {
	public String getClassifierId();
	public String getUserId();
	public String getUserName();
	public String getDisplayName();
	public int getScore();
	public double getTrend();
	Boolean getIsUserFollowed();
	
	public static class OrderByTrendDesc implements Comparator<ISuspiciousUserInfo>{
		
		@Override
		public int compare(ISuspiciousUserInfo o1, ISuspiciousUserInfo o2) {
			return o2.getTrend() > o1.getTrend() ? 1 : (o2.getTrend() < o1.getTrend() ? -1 : 0);
		}
		
	}
}
