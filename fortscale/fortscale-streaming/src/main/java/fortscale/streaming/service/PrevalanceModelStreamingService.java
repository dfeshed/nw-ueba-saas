package fortscale.streaming.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.storage.kv.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Throwables;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.PrevalanceModelBuilder;
import fortscale.ml.service.impl.ModelServiceImpl;
import fortscale.streaming.exceptions.LevelDbException;

/** 
 * Service class to maintain model lifecycle and persistence within samza tasks
 */
@Configurable(preConstruction=true)
public class PrevalanceModelStreamingService extends ModelServiceImpl{

	private static final Logger logger = LoggerFactory.getLogger(PrevalanceModelStreamingService.class);
	private static final long MIN_DIFF_TO_UPDATE_MODEL_SERVICE = 3600;
	
	private KeyValueStore<String, PrevalanceModel> store;
	private PrevalanceModelBuilder modelBuilder;
	private Map<PrevalanceModelKey, Long> changedModelsTimestampMap = new HashMap<>();
	


	public PrevalanceModelStreamingService(KeyValueStore<String, PrevalanceModel> store, PrevalanceModelBuilder modelBuilder){
		checkNotNull(store);
		checkNotNull(modelBuilder);
		
		this.store = store;
		this.modelBuilder = modelBuilder;
	}
	
	
	
	/** Get the model for the user first from the samza store, if not exists look for it in the repository or build a new model */
	public PrevalanceModel getModel(String context) throws Exception {
		String modelName = modelBuilder.getModelName();
		// lookup the model in the store
		PrevalanceModel model = store.get(buildStoreKey(modelName, context));
		if (model!=null)
			return model;
		
		// lookup the model in the repository
		model = super.getModel(context, modelName);
		if (model!=null) 
			return model;
		
		// create a new model
		return modelBuilder.build();
	}
	
	private String buildStoreKey(String modelName, String context){
		return String.format("%s%s",modelName,context);
	}
	
	
	/** Update the user model in samza store 
	 * @throws LevelDbException */
	public void updateModel(String context, PrevalanceModel model) throws LevelDbException {
		try{
			PrevalanceModelKey key = buildModelKey(context, model);
			store.put(buildStoreKey(key.getModelName(),key.getContext()), model);
			
			Long timestamp = changedModelsTimestampMap.get(key);
			if(timestamp == null){
				changedModelsTimestampMap.put(key, model.getBarrier().getTimestamp());
			} else if(model.getBarrier().getTimestamp() - timestamp > MIN_DIFF_TO_UPDATE_MODEL_SERVICE){
				super.updateModel(context, model);
				changedModelsTimestampMap.remove(key);
			}
		} catch(Exception exception){
			String errorMessage =String.format("error storing value. model name: %s, username: %s", model.getModelName(), context); 
        	logger.error(errorMessage, exception);
            throw new LevelDbException(errorMessage, exception);
        }
	}
	
	private PrevalanceModelKey buildModelKey(String context, PrevalanceModel model){
		return new PrevalanceModelKey(model.getModelName(), context);
	}
	
	
	/** export all models to mongodb as a secondary backing store */
	public void exportModels() {
		try { 
			for(PrevalanceModelKey key: changedModelsTimestampMap.keySet()){
				String context = key.getContext();
				try {
					PrevalanceModel model = getModel(context);
					if (model!=null) {
						// model might be null in case of a serialization error, in that case
						// we don't want to fail here and the error is logged in the serde implementation
						super.updateModel(context, model);
					}
				} catch (Exception e) {
					logger.error("error persisting model {} for context {} into repository", key.getModelName(), context, e);
					// propagate exception when connection to mongodb failed, so we won't process additional models 
					Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
				}
			}
		} catch (Exception e) {
			// report error to log and swallow that exception as 
			logger.error("error exporting models to mongodb from streaming task", e);
		}
		changedModelsTimestampMap.clear();
	}
	
	class PrevalanceModelKey extends Object{
		private String modelName;
		private String context;
		
		public PrevalanceModelKey(String modelName, String context){
			this.modelName = modelName;
			this.context = context;
		}
		
		public String getModelName() {
			return modelName;
		}

		public String getContext() {
			return context;
		}
		
		@Override
		public boolean equals(Object key){
			if(!(key instanceof PrevalanceModelKey)){
				return false;
			}
			PrevalanceModelKey tmp = (PrevalanceModelKey) key;
			return tmp.modelName.equals(this.modelName) && tmp.context.equals(this.context);
		}
		
		@Override
		public int hashCode(){
			return context.hashCode();
		}
	}
}
