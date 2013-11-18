package fortscale.services.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.ad.dao.AdComputerRepository;


@Component("adComputerRepository")
public class AdComputerRepositoryImpl implements AdComputerRepository {

	@Override
	public <S extends AdComputer> List<S> save(Iterable<S> entites) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdComputer> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdComputer> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AdComputer> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AdComputer> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdComputer findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<AdComputer> findAll(Iterable<String> ids) {
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
	public void delete(AdComputer entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends AdComputer> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AdComputer> findByLastModifiedExists(boolean exists) {
		// TODO Auto-generated method stub
		return null;
	}

}
