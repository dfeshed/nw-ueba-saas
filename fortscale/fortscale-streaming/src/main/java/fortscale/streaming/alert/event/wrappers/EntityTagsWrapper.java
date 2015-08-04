package fortscale.streaming.alert.event.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.EntityTags;
import fortscale.domain.core.EntityType;
import fortscale.streaming.alert.statement.decorators.DummyDecorator;
import fortscale.streaming.alert.statement.decorators.StatementDecorator;

import java.util.List;

/**
 * Created by danal on 04/08/2015.
 */
public class EntityTagsWrapper implements EventWrapper<EntityTags>  {


	/**
	 * JSON serializer
	 */
	protected ObjectMapper mapper = new ObjectMapper();

	public EntityTags convertEvent(String inputTopic, String userName,String tagMessageString) throws Exception{
		List<String> tags = mapper.readValue(tagMessageString, List.class);
		return new EntityTags(EntityType.User, userName, tags);
	}

	@Override public boolean shouldCreateDynamicStatements(EntityTags entityTags) {
		return false;
	}

	@Override public StatementDecorator getStatementDecorator() {
		return new DummyDecorator();
	}

	@Override public Object[] getDecoratorParams(EntityTags event) {
		return null;
	}

}
