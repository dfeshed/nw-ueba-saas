package fortscale.services.domain.fe.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.fe.EventResult;
import fortscale.domain.fe.dao.EventResultRepository;


@Component("eventResultRepository")
public class EventResultRepositoryImpl implements EventResultRepository {

	@Override
	public <S extends EventResult> List<S> save(Iterable<S> entites) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventResult> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventResult> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<EventResult> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventResult> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventResult findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<EventResult> findAll(Iterable<String> ids) {
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
	public void delete(EventResult entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Iterable<? extends EventResult> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<EventResult> findEventResultsBySqlQuery(String sqlQuery,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateLastRetrieved(String sqlQuery) {
		// TODO Auto-generated method stub

	}

}
