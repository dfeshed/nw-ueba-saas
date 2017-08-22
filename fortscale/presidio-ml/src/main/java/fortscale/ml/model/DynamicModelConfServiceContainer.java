package fortscale.ml.model;

/**
 * Created by barak_schuster on 21/08/2017.
 */
public class DynamicModelConfServiceContainer {
    private static ModelConfService modelConfService;

    public static ModelConfService getModelConfService() {
        return modelConfService;
    }

    public static void setModelConfService(ModelConfService modelConfService) {
        DynamicModelConfServiceContainer.modelConfService = modelConfService;
    }
}
