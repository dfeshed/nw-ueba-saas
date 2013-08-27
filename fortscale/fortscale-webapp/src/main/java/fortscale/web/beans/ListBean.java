package fortscale.web.beans;

import java.util.AbstractList;
import java.util.List;

public abstract class ListBean<E, B> extends AbstractList<B>{
	private List<E> list;

	public ListBean(List<E> list) {
		this.list = list;
	}

	@Override
	public B get(int index) {
		E item = list.get(index);
		return createBean(item);
	}
	@Override
	public int size() {
		return list.size();
	}

	protected abstract B createBean(E item);
}