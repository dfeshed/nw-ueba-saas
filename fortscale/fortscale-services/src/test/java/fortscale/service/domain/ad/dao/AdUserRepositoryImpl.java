package fortscale.service.domain.ad.dao;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AdUser> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AdUser> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AdUser> Iterable<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdUser findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<AdUser> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<AdUser> findAll(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(AdUser entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends AdUser> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AdUser> findAdUsersAttrVals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLatestTimeStamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdUser findByEmailAddress(String emailAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdUser> findByTimestamp(String timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdUser> findByDistinguishedNameIgnoreCaseContaining(
			String distinguishedName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdUser findByDistinguishedName(String distinguishedName) {
		// TODO Auto-generated method stub
		return null;
	}

}
