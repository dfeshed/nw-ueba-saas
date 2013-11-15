package fortscale.services.domain.analyst.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.dao.AnalystRepository;


@Component("analystRepository")
public class AnalystRepositoryImpl implements AnalystRepository{

	@Override
	public <S extends Analyst> List<S> save(Iterable<S> entites) {
		
		return null;
	}

	@Override
	public List<Analyst> findAll() {
		
		return null;
	}

	@Override
	public List<Analyst> findAll(Sort sort) {
		
		return null;
	}

	@Override
	public Page<Analyst> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends Analyst> S save(S entity) {
		
		return null;
	}

	@Override
	public Analyst findOne(String id) {
		
		return null;
	}

	@Override
	public boolean exists(String id) {
		
		return false;
	}

	@Override
	public Iterable<Analyst> findAll(Iterable<String> ids) {
		
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
	public void delete(Analyst entity) {
		
		
	}

	@Override
	public void delete(Iterable<? extends Analyst> entities) {
		
		
	}

	@Override
	public void deleteAll() {
		
		
	}

	@Override
	public Analyst findByUserName(String userName) {
		
		return null;
	}

	@Override
	public List<Analyst> findByIsDisabled(boolean isDisabled) {
		
		return null;
	}

}
