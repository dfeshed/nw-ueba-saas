package fortscale.service.domain.analyst.dao;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Analyst> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Analyst> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Analyst> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Analyst> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Analyst findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Analyst> findAll(Iterable<String> ids) {
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
	public void delete(Analyst entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends Analyst> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Analyst findByUserName(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Analyst> findByIsDisabled(boolean isDisabled) {
		// TODO Auto-generated method stub
		return null;
	}

}
