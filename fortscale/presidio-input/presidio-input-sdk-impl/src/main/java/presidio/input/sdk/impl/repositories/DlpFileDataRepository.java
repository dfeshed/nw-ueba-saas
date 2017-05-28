package presidio.input.sdk.impl.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import presidio.sdk.api.domain.DlpFileDataDocument;


public interface DlpFileDataRepository<T> extends MongoRepository<DlpFileDataDocument, String> {
}
