package fortscale.utils.mongodb.converter;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

/**
 * Created by barak_schuster on 12/4/16.
 */
public class FSMappingMongoConverter extends MappingMongoConverter {

    private String mapKeyDollarReplacement;

    public FSMappingMongoConverter(DbRefResolver dbRefResolver, MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {
        super(dbRefResolver, mappingContext);
    }

    /**
     * Potentially replaces dollar and dot in the given map key with the configured map key replacement if configured or aborts
     * conversion if none is configured.
     *
     * @see #setMapKeyDollarReplacement(String)
     * @param source
     * @return
     */
    @Override
    protected String potentiallyEscapeMapKey(String source)
    {
        String result = super.potentiallyEscapeMapKey(source);
        if (mapKeyDollarReplacement == null) {
            throw new MappingException(String.format(
                    "Map key %s contains dollars but no replacement was configured! Make "
                            + "sure map keys don't contain dollars in the first place or configure an appropriate replacement!",
                    source));
        }
        if(result.startsWith("$"))
        {
            StringBuilder sb = new StringBuilder();
            result = sb.append(mapKeyDollarReplacement).append(result.substring(1,result.length())).toString();
        }
        return result;
    }


    /**
     * Translates the map key replacements in the given key just read with a dot in case a map key replacement has been
     * configured.
     *
     * @param source
     * @return
     */
    @Override
    protected String potentiallyUnescapeMapKey(String source) {
        String result = super.potentiallyUnescapeMapKey(source);

        if (mapKeyDollarReplacement == null) {
            return result;
        }
        else {
            if(source.startsWith(mapKeyDollarReplacement))
            {
                StringBuilder sb = new StringBuilder();
                result = sb.append("$").append(result.substring(mapKeyDollarReplacement.length(),result.length())).toString();
            }
            return result;
        }
    }

    public void setMapKeyDollarReplacement(String mapKeyDollarReplacement) {
        this.mapKeyDollarReplacement = mapKeyDollarReplacement;
    }
}
