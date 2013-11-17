package fortscale.services.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.dao.AdGroupRepository;

@Component("adGroupRepository")
public class AdGroupRepositoryImpl implements AdGroupRepository{

	@Override
	public Iterable<AdGroup> findAll(Sort sort) {
		return null;
	}

	@Override
	public Page<AdGroup> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public <S extends AdGroup> S save(S entity) {
		return null;
	}

	@Override
	public <S extends AdGroup> Iterable<S> save(Iterable<S> entities) {
		return null;
	}

	@Override
	public AdGroup findOne(String id) {
		return null;
	}

	@Override
	public boolean exists(String id) {
		return false;
	}

	@Override
	public Iterable<AdGroup> findAll() {
		return null;
	}

	@Override
	public Iterable<AdGroup> findAll(Iterable<String> ids) {
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
	public void delete(AdGroup entity) {
		
	}

	@Override
	public void delete(Iterable<? extends AdGroup> entities) {
		
	}

	@Override
	public void deleteAll() {
		
	}

	@Override
	public AdGroup findByDistinguishedName(String distinguishedName) {
		return null;
	}

	@Override
	public List<AdGroup> findByLastModifiedExists(boolean exists) {
		// TODO Auto-generated method stub
		return null;
	}

}
