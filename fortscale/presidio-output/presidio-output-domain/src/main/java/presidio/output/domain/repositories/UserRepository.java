package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.users.User;

/**
 * Created by efratn on 20/08/2017.
 */
public interface UserRepository extends ElasticsearchRepository<User, String> {

    Page<User> findByUserName(String userName, Pageable pageable);

    Page<User> findByUserId(String userId, Pageable pageable);

}
