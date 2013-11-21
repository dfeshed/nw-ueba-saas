package fortscale.services.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.dao.AdOURepository;




@Component("adOURepository")
public class AdOURepositoryImpl implements AdOURepository {

	@Override
	public <S extends AdOU> List<S> save(Iterable<S> entites) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdOU> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdOU> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AdOU> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AdOU> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdOU findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<AdOU> findAll(Iterable<String> ids) {
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
	public void delete(AdOU entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends AdOU> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AdOU> findByLastModifiedExists(boolean exists) {
		// TODO Auto-generated method stub
		return null;
	}

}
