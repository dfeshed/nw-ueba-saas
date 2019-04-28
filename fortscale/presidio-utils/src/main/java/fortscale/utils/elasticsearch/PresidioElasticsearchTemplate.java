package fortscale.utils.elasticsearch;


import fortscale.utils.elasticsearch.mapping.DefaultAssociationsResolver;
import fortscale.utils.elasticsearch.services.TemplateExtractor;
import org.elasticsearch.client.Client;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;

import javax.annotation.PostConstruct;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class PresidioElasticsearchTemplate extends ElasticsearchTemplate {

    TemplateExtractor templateExtractor;

    public PresidioElasticsearchTemplate(Client client, TemplateExtractor templateExtractor, ResultsMapper resultsMapper) {
        super(client, resultsMapper);
        this.templateExtractor = templateExtractor;
    }


    @Override
    public <T> boolean putMapping(Class<T> clazz) {
        if (putMappingWithAnnotation(clazz)) {
            return true;
        }
        return super.putMapping(clazz);
    }

    private <T> boolean putMappingWithAnnotation(Class<T> clazz) {
        String mappings = templateExtractor.mappingConverting(clazz);
        if (isNotBlank(mappings)) {
            return putMapping(clazz, mappings);
        }
        return false;
    }


    @PostConstruct
    private void setAssociationResolver() {
        ((PresidioResultMapper)getResultsMapper()).setResolver(new DefaultAssociationsResolver(this));
    }
}