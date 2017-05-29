package presidio.input.sdk.impl.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import presidio.sdk.api.domain.DlpFileDataDocument;

@Repository
public interface DlpFileDataRepository<T> extends MongoRepository<DlpFileDataDocument, String> {
}
