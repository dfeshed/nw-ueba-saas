package presidio.ui.presidiouiapp.beans;

import java.util.List;

import fortscale.domain.core.Entity;


public class UserDetailsListBean extends ListBean<Entity, UserDetailsBean> {

	public UserDetailsListBean(List<Entity> list) {
		super(list);
	}

	@Override
	protected UserDetailsBean createBean(Entity item) {

		return null;
	}

}
