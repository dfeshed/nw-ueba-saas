package fortscale.streaming.alert.event.wrappers;

import fortscale.streaming.alert.statement.decorators.StatementDecorator;

/**
 * Created by danal on 04/08/2015.
 */
public interface EventWrapper<T> {


	public T convertEvent(String inputTopic, String userName,String tagMessageString) throws Exception;

	public boolean shouldCreateDynamicStatements(T event);

	public StatementDecorator getStatementDecorator();

	public Object[] getDecoratorParams(T event);

}
