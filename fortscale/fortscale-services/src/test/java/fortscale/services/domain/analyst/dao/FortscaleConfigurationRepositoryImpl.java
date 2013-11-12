package fortscale.services.domain.analyst.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fortscale.domain.analyst.FortscaleConfiguration;
import fortscale.domain.analyst.dao.FortscaleConfigurationRepository;

public class FortscaleConfigurationRepositoryImpl implements
		FortscaleConfigurationRepository {

	@Override
	public List<FortscaleConfiguration> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FortscaleConfiguration> findAll(Sort arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends FortscaleConfiguration> List<S> save(Iterable<S> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<FortscaleConfiguration> findAll(Pageable arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(FortscaleConfiguration arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Iterable<? extends FortscaleConfiguration> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exists(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<FortscaleConfiguration> findAll(Iterable<String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FortscaleConfiguration findOne(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends FortscaleConfiguration> S save(S arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FortscaleConfiguration> findByConfigId(String configId,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FortscaleConfiguration findByConfigIdAndCreatedById(String configId,
			String createdById) {
		// TODO Auto-generated method stub
		return null;
	}

}
