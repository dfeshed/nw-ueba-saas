package fortscale.streaming.service;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.PrevalanceModelBuilder;
import fortscale.ml.model.prevalance.UserTimeBarrier;
import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.LevelDbException;

/** 
 * Service class to maintain model lifecycle and persistence within samza tasks
 */
@Service("prevalanceModelStreamingService")
public class PrevalanceModelStreamingService implements ModelService{

	private static final Logger logger = LoggerFactory.getLogger(PrevalanceModelStreamingService.class);
	
	private KeyValueStore<String, PrevalanceModel> store;
	private PrevalanceModelBuilder modelBuilder;
	@Autowired
	private ModelService modelService;
	
	
	
	
	public void setStore(KeyValueStore<String, PrevalanceModel> store){
		checkNotNull(store);
		this.store = store;
	}
	
	public void setModelBuilder(PrevalanceModelBuilder modelBuilder){
		checkNotNull(modelBuilder);
		this.modelBuilder = modelBuilder;
	}
	
	/** Get the model for the user first from the samza store, if not exists look for it in the repository or build a new model */
	public PrevalanceModel getModelForUser(String username, String modelName) throws Exception {
		// lookup the model in the store
		PrevalanceModel model = store.get(username);
		if (model!=null)
			return model;
		
		// lookup the model in the repository
		model = modelService.getModelForUser(username, modelName);
		if (model!=null) 
			return model;
		
		// create a new model
		return modelBuilder.build();
	}
	
	/** Update the user model in samza store 
	 * @throws LevelDbException */
	public void updateUserModel(String username, PrevalanceModel model) throws LevelDbException {
		try{
			store.put(username, model);
		} catch(Exception exception){
        	logger.error(String.format("error storing value. username: %s", username), exception);
            throw new LevelDbException(String.format("error while trying to store user %s.", username), exception);
        }
	}
	
	/** sync all models in samza store with the models in the mongodb */ 
	public void syncModelsWithRepository() {
		KeyValueIterator<String, PrevalanceModel> iterator = store.all();
		try { 
			while (iterator.hasNext()) {
				Entry<String, PrevalanceModel> entry = iterator.next();
				String username = entry.getKey();
				PrevalanceModel model = entry.getValue();
				try {
					PrevalanceModel dbModel = modelService.getModelForUser(username, modelBuilder.getModelName());
					UserTimeBarrier dtoBarrier = dbModel.getBarrier();
					// check if the model in the repository is newer than the model in the store
					if (model.getBarrier().isEventAfterBarrier(dtoBarrier)) {
						// replace the model in store
						store.put(username, dbModel);
					}

				} catch (Exception e) {
					logger.error("error adding model {} for user {} into store", modelBuilder.getModelName(), username);
				}
			}
		} finally {
			if (iterator!=null)
				iterator.close();
		}
	}
	
	
	/** export all models to mongodb as a secondary backing store */
	public void exportModels() {
		// go over all users model in the store and persist them to mongodb
		KeyValueIterator<String, PrevalanceModel> iterator = store.all();
		try { 
			while (iterator.hasNext()) {
				Entry<String, PrevalanceModel> entry = iterator.next();
				String username = entry.getKey();
				PrevalanceModel model = entry.getValue();
				if (model!=null) {
					// model might be null in case of a serialization error, in that case
					// we don't want to fail here and the error is logged in the serde implementation 
					try {
						modelService.updateUserModel(username, model);
					} catch (Exception e) {
						logger.error("error persisting model {} for user {} into repository", modelBuilder.getModelName(), username, e);
						// propagate exception when connection to mongodb failed, so we won't process additional models 
						Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
					}
				}
			}
		} catch (Exception e) {
			// report error to log and swallow that exception as 
			logger.error("error exporting models to mongodb from streaming task", e);
		} finally {
			if (iterator!=null)
				iterator.close();
		}
	}	
}
