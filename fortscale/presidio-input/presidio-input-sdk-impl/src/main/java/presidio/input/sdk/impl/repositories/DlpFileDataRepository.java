package presidio.input.sdk.impl.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import presidio.sdk.api.domain.DlpFileDataDocument;

@Repository
public interface DlpFileDataRepository<T> extends MongoRepository<DlpFileDataDocument, String>, DlpFileDataRepositoryCustom {
    //todo: if we change our field names to be java like (i.e dateTimeUnix instead of date_time_unix we could have used findByDateTimeUnixBetween without the need for the custom interface)
    // TODO: we should have generic repository for all data sources, that receives the collection name and the dates and runs the logic
}
