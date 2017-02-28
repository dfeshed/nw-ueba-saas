package fortscale.services;


import fortscale.domain.core.Alert;

/**
 * Created by Amir Keren on 18/01/16.
 */
public interface AlertPrettifierService<T> {

	T prettify(Alert alert);

	T prettify(Alert alert, boolean noEvidencePrettify);




}