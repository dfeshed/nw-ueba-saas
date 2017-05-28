package presidio.ade.domain.store.translators;

import presidio.ade.domain.store.input.ADEInputCleanupParams;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;

import java.util.Collection;

/**
 *
 * Created by barak_schuster on 5/18/17.
 */
public class ADEInputDataToCollectionNameTranslator implements DataCollectionTranslator<ADEInputRecordsMetaData> {

    public static final String ADE_INPUT_COLLECTION_PREFIX = "ade_input";

    @Override
    public String toCollectionName(ADEInputRecordsMetaData arg) {
        return String.format(ADE_INPUT_COLLECTION_PREFIX + "_%s",arg.getDataSource());
    }

    @Override
    public Collection<String> toCollectionNames(ADEInputCleanupParams cleanupParams) {
        return null;
    }
}
