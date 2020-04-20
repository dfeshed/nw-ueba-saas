package presidio.monitoring.elastic.services;


import fortscale.utils.elasticsearch.annotations.Template;
import org.springframework.data.elasticsearch.annotations.Document;
import presidio.monitoring.records.MetricDocument;

@Document(indexName = MetricDocumentStaticIndexForTests.STATIC_INDEX_NAME, type = MetricDocument.TYPE)
@Template(mappingPath = "elasticsearch/indexes/presidio-monitoring/template.json")
public class MetricDocumentStaticIndexForTests extends MetricDocument {

    public static final String STATIC_INDEX_NAME = "<presidio-monitoring-2018-07-15>";



}
