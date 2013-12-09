package fortscale.services.domain.analyst.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.analyst.AnalystFollowUser;
import fortscale.domain.analyst.dao.AnalystFollowUserRepository;


@Component("analystFollowUserRepository")
public class AnalystFollowUserRepositoryImpl implements AnalystFollowUserRepository {

	@Override
	public <S extends AnalystFollowUser> List<S> save(Iterable<S> entites) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AnalystFollowUser> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AnalystFollowUser> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AnalystFollowUser> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AnalystFollowUser> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnalystFollowUser findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<AnalystFollowUser> findAll(Iterable<String> ids) {
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
	public void delete(AnalystFollowUser entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Iterable<? extends AnalystFollowUser> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

}
