package fortscale.utils.transform;


public interface GenericTransformer<T> {
    T transform(T objToTransform);
}
