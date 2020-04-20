package fortscale.utils.pool;

import java.io.StringWriter;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;

public class StringWriterPool {
	private static final StringWriterPool globalPool = new StringWriterPool();
	
	private StackObjectPool pool;
	
	public static final StringWriter newStringWriter(){
		return globalPool.borrowObject();
	}
	public static final void returnStringWriter(StringWriter writer){
		globalPool.returnObject(writer);
	}
	
	
	public StringWriterPool(){
		pool = new StackObjectPool(new BasePoolableObjectFactory() {
			@Override
			public Object makeObject() throws Exception {
				return new StringWriter();
			}
			@Override
			public void passivateObject(Object obj) throws Exception {
				((StringWriter)obj).getBuffer().setLength(0);
			} 
		});
	}
	
	public StringWriter borrowObject(){
		try {
			return (StringWriter)pool.borrowObject();
		} catch (Exception e) {
			throw new RuntimeException("StringWriterPool.getWriter failed", e);
		}
	}
	public void returnObject(StringWriter writer){
		try {
			pool.returnObject(writer);
		} catch (Exception e) {
			throw new RuntimeException("StringWriterPool.returnObject failed", e);
		}
	}
}
