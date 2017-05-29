package presidio.input.sdk.impl.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import presidio.sdk.api.domain.DlpFileDataDocument;


public interface DlpFileDataRepository extends MongoRepository<DlpFileDataDocument, String>, DlpFileDataRepositoryCustom {
    //todo: if we change our field names to be java like (i.e dateTimeUnix instead of date_time_unix we could have used findByDateTimeUnixBetween without the need for the custom interface)
}
