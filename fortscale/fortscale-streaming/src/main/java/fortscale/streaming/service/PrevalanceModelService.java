package fortscale.streaming.service;

import static com.google.common.base.Preconditions.*;

import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.storage.kv.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fortscale.streaming.model.PrevalanceModel;
import fortscale.streaming.model.PrevalanceModelBuilder;
import fortscale.streaming.serialization.PrevalanceModelSerde;
import fortscale.streaming.service.dao.Model;
import fortscale.streaming.service.dao.ModelRepository;

/** 
 * Service class to maintain model lifecycle and persistence within samza tasks
 */
public class PrevalanceModelService {

	private static final Logger logger = LoggerFactory.getLogger(PrevalanceModelService.class);
	
	private KeyValueStore<String, PrevalanceModel> store;
	private PrevalanceModelBuilder modelBuilder;
	private ModelRepository repository;
	private PrevalanceModelSerde serializer;
	
	public PrevalanceModelService(KeyValueStore<String, PrevalanceModel> store, PrevalanceModelBuilder modelBuilder) {
		checkNotNull(store);
		checkNotNull(modelBuilder);
		this.store = store;
		this.modelBuilder = modelBuilder;
		this.serializer = new PrevalanceModelSerde();
		
		// get the repository from spring context implicitly, as I has a horrible 
		// experience using compile time weaving to work here....
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/streaming-context.xml");
		repository = context.getBean(ModelRepository.class);
	}
	
	/** Get the model for the user first from the samza store, if not exists look for it in the repository or build a new model */
	public PrevalanceModel getModelForUser(String username) throws Exception {
		// lookup the model in the store
		PrevalanceModel model = store.get(username);
		if (model!=null)
			return model;
		
		// lookup the model in the repository
		Model dto = repository.findByUserNameAndModelName(username, modelBuilder.getModelName());
		if (dto!=null) 
			return convertToPrevalanceModel(dto);
		
		// create a new model
		return modelBuilder.build();
	}
	
	/** Update the user model in samza store */
	public void updateUserModelInStore(String username, PrevalanceModel model) {
		store.put(username, model);
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
					Model dto = repository.findByUserNameAndModelName(username, modelBuilder.getModelName());
					if (dto.getHighTimeMark() > model.getTimeMark()) {
						// replace the model in store
						PrevalanceModel updatedModel = convertToPrevalanceModel(dto);
						store.put(username, updatedModel);
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
				try {
					Model dto = convertToDTO(username, model);
					repository.upsertModel(dto);
				} catch (Exception e) {
					logger.error("error persisting model {} for user {} into repository", modelBuilder.getModelName(), username, e);
				}
			}
		} finally {
			if (iterator!=null)
				iterator.close();
		}
	}
	
	
	private Model convertToDTO(String username, PrevalanceModel model) {
		String modelJson = serializer.toString(model);
		Model dto = new Model(modelBuilder.getModelName(), username, modelJson, model.getTimeMark());
		return dto;
	}
	
	private PrevalanceModel convertToPrevalanceModel(Model dto) {
		PrevalanceModel model = serializer.fromString(dto.getModelJson());
		return model;
	}
}
