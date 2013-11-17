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
		
		return null;
	}

	@Override
	public List<AnalystAuth> findAll(Sort arg0) {
		
		return null;
	}

	@Override
	public <S extends AnalystAuth> List<S> save(Iterable<S> arg0) {
		
		return null;
	}

	@Override
	public Page<AnalystAuth> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public <S extends AnalystAuth> S save(S entity) {
		
		return null;
	}

	@Override
	public AnalystAuth findOne(String id) {
		
		return null;
	}

	@Override
	public boolean exists(String id) {
		
		return false;
	}

	@Override
	public Iterable<AnalystAuth> findAll(Iterable<String> ids) {
		
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
	public void delete(AnalystAuth entity) {
		

	}

	@Override
	public void delete(Iterable<? extends AnalystAuth> entities) {
		

	}

	@Override
	public void deleteAll() {
		

	}

	@Override
	public AnalystAuth findByUsername(String username) {
		
		return null;
	}

}
