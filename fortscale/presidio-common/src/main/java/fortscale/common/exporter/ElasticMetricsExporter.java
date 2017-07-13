package fortscale.common.exporter;

import fortscale.utils.logging.Logger;

public class ElasticMetricsExporter implements MetricsExporter {

    private final Logger logger=Logger.getLogger(ElasticMetricsExporter.class);
//    private final ElasticsearchTemplate elasticsearchTemplate;
//    private IndexQuery indexQuery;
//
//    public ElasticMetricsExporter(ElasticsearchTemplate elasticsearchTemplate,IndexQuery indexQuery) {
//        this.elasticsearchTemplate = elasticsearchTemplate;
//        this.indexQuery=indexQuery;
//    }
//
//    @Override
//    public void export() {
//        logger.debug("Exporting");
//        elasticsearchTemplate.index(indexQuery);
//    }

    @Override
    public void export() {}

}
