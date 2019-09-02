package fortscale.utils.transform;

import fortscale.utils.logging.Logger;

public abstract class OperationTypeMappingTransformer extends AbstractJsonObjectTransformer {

    private static final Logger logger = Logger.getLogger(OperationTypeMappingTransformer.class);

  //  @Value("${operation.type.category.mapping.file.path}")
//    @Value("${operation.type.category.hierarchy.mapping.file.path}")
    public OperationTypeMappingTransformer(String name) {
        super(name);
    }
}
