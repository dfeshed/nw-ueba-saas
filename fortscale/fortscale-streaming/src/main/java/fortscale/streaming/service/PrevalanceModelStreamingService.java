package fortscale.streaming.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.storage.kv.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.PrevalanceModelBuilder;
import fortscale.ml.model.prevalance.PrevalanceModelBuilderImpl;
import fortscale.ml.service.impl.ModelServiceImpl;
import fortscale.streaming.exceptions.LevelDbException;

/** 
 * Service class to maintain model lifecycle and persistence within samza tasks
 */
public class PrevalanceModelStreamingService extends ModelServiceImpl{

	private static final Logger logger = LoggerFactory.getLogger(PrevalanceModelStreamingService.class);
	private static final long MIN_DIFF_TO_UPDATE_MODEL_SERVICE = 3600;
	
	private KeyValueStore<String, PrevalanceModel> store = new NullKeyValueStore<>();
	private PrevalanceModelBuilder modelBuilder = new NullPrevalanceModelBuilder();
	private Map<PrevalanceModelKey, Long> changedModelsTimestampMap = new HashMap<>();
	
	
	
	public void setStore(KeyValueStore<String, PrevalanceModel> store){
		checkNotNull(store);
		this.store = store;
	}
	
	public void setModelBuilder(PrevalanceModelBuilderImpl modelBuilder){
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
		model = super.getModelForUser(username, modelName);
		if (model!=null) 
			return model;
		
		// create a new model
		return modelBuilder.build();
	}
	
	/** Update the user model in samza store 
	 * @throws LevelDbException */
	public void updateUserModel(String username, PrevalanceModel model) throws LevelDbException {
		try{
			PrevalanceModelKey key = buildModelKey(username, model);
			store.put(username, model);
			
			Long timestamp = changedModelsTimestampMap.get(key);
			if(timestamp == null){
				changedModelsTimestampMap.put(key, model.getBarrier().getTimestamp());
			} else if(model.getBarrier().getTimestamp() - timestamp > MIN_DIFF_TO_UPDATE_MODEL_SERVICE){
				super.updateUserModel(username, model);
				changedModelsTimestampMap.remove(key);
			}
		} catch(Exception exception){
        	logger.error(String.format("error storing value. username: %s", username), exception);
            throw new LevelDbException(String.format("error while trying to store user %s.", username), exception);
        }
	}
	
	private PrevalanceModelKey buildModelKey(String username, PrevalanceModel model){
		return new PrevalanceModelKey(model.getModelName(), username);
	}
	
	
	/** export all models to mongodb as a secondary backing store */
	public void exportModels() {
		try { 
			for(PrevalanceModelKey key: changedModelsTimestampMap.keySet()){
				String username = key.getEntityName();
				try {
					PrevalanceModel model = getModelForUser(username, key.getModelName());
					if (model!=null) {
						// model might be null in case of a serialization error, in that case
						// we don't want to fail here and the error is logged in the serde implementation 
						super.updateUserModel(username, model);
						changedModelsTimestampMap.remove(key);
					}
				} catch (Exception e) {
					logger.error("error persisting model {} for user {} into repository", modelBuilder.getModelName(), username, e);
					// propagate exception when connection to mongodb failed, so we won't process additional models 
					Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
				}
			}
		} catch (Exception e) {
			// report error to log and swallow that exception as 
			logger.error("error exporting models to mongodb from streaming task", e);
		}
	}
	
	class PrevalanceModelKey extends Object{
		private String modelName;
		private String entityName;
		
		public PrevalanceModelKey(String modelName, String entityName){
			this.modelName = modelName;
			this.entityName = entityName;
		}
		
		public String getModelName() {
			return modelName;
		}

		public String getEntityName() {
			return entityName;
		}
		
		@Override
		public boolean equals(Object key){
			if(!(key instanceof PrevalanceModelKey)){
				return false;
			}
			PrevalanceModelKey tmp = (PrevalanceModelKey) key;
			return tmp.modelName.equals(this.modelName) && tmp.entityName.equals(this.entityName);
		}
		
		@Override
		public int hashCode(){
			return entityName.hashCode();
		}
	}
}
