package fortscale.domain.core.dao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

import fortscale.domain.ad.AdUser;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepositoryImpl.UserObjectGUIDWrapper;


@Component
public class UserGUIDReadConverter implements Converter<DBObject, UserObjectGUIDWrapper>{

	@Override
	public UserObjectGUIDWrapper convert(DBObject dbObject) {
		DBObject adInfoObj = (DBObject) dbObject.get(User.adInfoField);
		String userGUID = (String) adInfoObj.get(AdUser.objectGUIDField);
		return new UserObjectGUIDWrapper(userGUID);
	}
	
}