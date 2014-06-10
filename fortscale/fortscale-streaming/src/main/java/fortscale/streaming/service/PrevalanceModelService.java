package fortscale.streaming.service;

import static com.google.common.base.Preconditions.*;

import org.apache.samza.storage.kv.KeyValueStore;

import fortscale.streaming.model.PrevalanceModel;
import fortscale.streaming.model.PrevalanceModelBuilder;

/** 
 * Service class to maintain model lifecycle and persistence within samza tasks
 */
public class PrevalanceModelService {

	private KeyValueStore<String, PrevalanceModel> store;
	private PrevalanceModelBuilder modelBuilder;
	
	public PrevalanceModelService(KeyValueStore<String, PrevalanceModel> store, PrevalanceModelBuilder modelBuilder) {
		checkNotNull(store);
		checkNotNull(modelBuilder);
		this.store = store;
		this.modelBuilder = modelBuilder;
	}
	
	/** Get the model for the user first from the samza store, if not exists build a new model */
	public PrevalanceModel getModelForUser(String username) throws Exception {
		PrevalanceModel model = store.get(username);
		return (model!=null) ? model : modelBuilder.build();
	}
	
	/** Update the user model in samza store */
	public void updateUserModel(String username, PrevalanceModel model) {
		store.put(username, model);
	}
	
	/** import all models from mongodb as a secondary backing store */
	public void importModels() {
		
	}
	
	/** export all models to mongodb as a secondary backing store */
	public void exportModels() {
//		if (store==null) {
//			logger.error("store not initialized for {}", modelName);
//			return;
//		}
//		
//		// go over all users model in the store and persist them to mongodb
//		KeyValueIterator<String, PrevalanceModel> iterator = store.all();
//		try { 
//			while (iterator.hasNext()) {
//				Entry<String, PrevalanceModel> entry = iterator.next();
//				String username = entry.getKey();
//				PrevalanceModel model = entry.getValue();
//			}
//		} finally {
//			if (iterator!=null)
//				iterator.close();
//		}
		
	}
	
}
