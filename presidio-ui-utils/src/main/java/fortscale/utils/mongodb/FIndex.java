package fortscale.utils.mongodb;

import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public class FIndex extends Index {
	private long expire = -1;

	public FIndex expire(long value, TimeUnit unit) {
		Assert.notNull(unit, "TimeUnit for expiration must not be null.");
		this.expire = unit.toSeconds(value);
		return this;
	}

	@Override
	public DBObject getIndexOptions() {
		DBObject dbo = super.getIndexOptions();

		if (dbo != null && expire >= 0) {
			dbo.put("expireAfterSeconds", expire);
		}

		return dbo;
	}
}
