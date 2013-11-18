package fortscale.services.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;

@Component("adUserRepository")
public class AdUserRepositoryImpl implements AdUserRepository{

	@Override
	public Iterable<AdUser> findAll(Sort sort) {
		return null;
	}

	@Override
	public Page<AdUser> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends AdUser> S save(S entity) {
		return null;
	}

	@Override
	public <S extends AdUser> Iterable<S> save(Iterable<S> entities) {
		
		return null;
	}

	@Override
	public AdUser findOne(String id) {
		
		return null;
	}

	@Override
	public boolean exists(String id) {
		
		return false;
	}

	@Override
	public Iterable<AdUser> findAll() {
		
		return null;
	}

	@Override
	public Iterable<AdUser> findAll(Iterable<String> ids) {
		
		return null;
	}

	@Override
	public long count() {
		
		return 0;
	}

	@Override
	public void delete(String id) {
		
		
	}

	@Override
	public void delete(AdUser entity) {
		
		
	}

	@Override
	public void delete(Iterable<? extends AdUser> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public List<AdUser> findAdUsersAttrVals() {
		
		return null;
	}

	@Override
	public String getLatestTimeStamp() {
		
		return null;
	}

	@Override
	public AdUser findByEmailAddress(String emailAddress) {
		
		return null;
	}

	@Override
	public List<AdUser> findByTimestamp(String timestamp) {
		
		return null;
	}

	@Override
	public List<AdUser> findByDistinguishedNameIgnoreCaseContaining(
			String distinguishedName) {
		
		return null;
	}

	@Override
	public AdUser findByDistinguishedName(String distinguishedName) {
		
		return null;
	}

	@Override
	public List<AdUser> findByLastModifiedExists(boolean exists) {
		// TODO Auto-generated method stub
		return null;
	}

}
