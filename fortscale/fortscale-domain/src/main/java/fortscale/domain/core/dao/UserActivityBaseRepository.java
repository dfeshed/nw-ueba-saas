package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.List;

public abstract class UserActivityBaseRepository  {

	@Autowired
	protected MongoTemplate mongoTemplate;

	protected  <T extends UserActivityDocument> List<T> getUserActivityEntries(@Nullable String username, int timeRangeInDays, String collectionName, Class<T> documentType) {
		List<T> userActivityDocuments;
		if (mongoTemplate.collectionExists(collectionName)) {
			Criteria jointCriteria = Criteria.where(UserActivityLocationDocument.START_TIME_FIELD_NAME).gte(TimestampUtils.convertToSeconds(getStartTime(timeRangeInDays)));
			if (username != null) {
				Criteria idCriteria = Criteria.where(UserActivityLocationDocument.USER_NAME_FIELD_NAME).is(username);
				jointCriteria.andOperator(idCriteria);
			}
			else {
				getLogger().info("Argument 'username' is null. Querying by start time only");
			}
			Query query = new Query(jointCriteria);
			userActivityDocuments = mongoTemplate.find(query, documentType, collectionName);
		}
		else {
			final String errorMessage = String.format("Could not find collection '%s' in database", collectionName);
			getLogger().error(errorMessage);
			throw new RuntimeException(errorMessage);
		}

		return userActivityDocuments;
	}



	protected long getStartTime(int timeRangeInDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -timeRangeInDays);
		return TimestampUtils.toStartOfDay(calendar.getTime().getTime());
	}

	protected abstract Logger getLogger();

}
