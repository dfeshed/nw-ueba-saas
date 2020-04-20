package fortscale.utils.mongodb.util;

public interface ToCollectionNameTranslator<T> {
    /**
     * translates arg to relevant mongodb collection name
     *
     * @param arg i.e. conf name
     * @return mongodb collection name
     */
    String toCollectionName(T arg);
}
