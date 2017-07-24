package presidio.output.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.Alert;
import presidio.output.domain.records.AlertQuery;

import java.util.List;

public interface AlertService {

    Alert save(Alert alert);

    Iterable<Alert> save(List<Alert> alerts);

    void delete(Alert alert);

    Alert findOne(String id);

    Iterable<Alert> findAll();

    Page<Alert> findByUserName(String userName, PageRequest pageRequest);

    Page<Alert> find(AlertQuery alertQuery);

}