package presidio.ade.test.utils.generators;

import fortscale.ml.model.Model;

/**
 * Created by barak_schuster on 9/10/17.
 */
public interface IModelGenerator<T extends Model > {
      T getNext();
}
