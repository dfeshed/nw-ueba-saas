package fortscale.services.domain.analyst.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.analyst.dao.AnalystAuthRepository;
@Component("analystAuthRepository")
public class AnalystAuthRepositoryImpl implements AnalystAuthRepository {

	@Override
	public List<AnalystAuth> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AnalystAuth> findAll(Sort arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AnalystAuth> List<S> save(Iterable<S> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AnalystAuth> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AnalystAuth> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnalystAuth findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<AnalystAuth> findAll(Iterable<String> ids) {
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
	public void delete(AnalystAuth entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Iterable<? extends AnalystAuth> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public AnalystAuth findByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
