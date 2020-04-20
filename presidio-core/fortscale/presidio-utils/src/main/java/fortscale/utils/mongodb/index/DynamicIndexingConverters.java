package fortscale.utils.mongodb.index;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.util.Assert;

/**
 * @author Lior Govrin
 */
public class DynamicIndexingConverters {
    public static IndexDefinition convertCompoundIndexToIndexDefinition(CompoundIndex compoundIndex) {
        Assert.notNull(compoundIndex, "compoundIndex cannot be null.");
        String def = compoundIndex.def();
        CompoundIndexDefinition compoundIndexDefinition = new CompoundIndexDefinition(Document.parse(def));
        if (compoundIndex.unique()) compoundIndexDefinition.unique();
        if (compoundIndex.sparse()) compoundIndexDefinition.sparse();
        //if (compoundIndex.dropDups()) compoundIndexDefinition.unique(Duplicates.DROP);
        if (!compoundIndex.useGeneratedName()) compoundIndexDefinition.named(compoundIndex.name());
        if (compoundIndex.background()) compoundIndexDefinition.background();
        return compoundIndexDefinition;
    }

    public static IndexInfo convertIndexDefinitionToIndexInfo(IndexDefinition indexDefinition) {
        Assert.notNull(indexDefinition, "indexDefinition cannot be null.");
        Document document = new Document();
        document.put("key", indexDefinition.getIndexKeys());
        document.putAll(indexDefinition.getIndexOptions());
        return IndexInfo.indexInfoOf(document);
    }

    public static IndexInfo convertToNamelessIndexInfo(IndexInfo indexInfo) {
        Assert.notNull(indexInfo, "indexInfo cannot be null.");
        return new IndexInfo(indexInfo.getIndexFields(), StringUtils.EMPTY, indexInfo.isUnique(),
                indexInfo.isSparse(), indexInfo.getLanguage());
    }
}
