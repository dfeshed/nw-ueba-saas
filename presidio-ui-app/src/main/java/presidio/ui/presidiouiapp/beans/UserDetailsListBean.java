package presidio.ui.presidiouiapp.beans;

import java.util.List;

import fortscale.domain.core.User;


public class UserDetailsListBean extends ListBean<User, UserDetailsBean> {

	public UserDetailsListBean(List<User> list) {
		super(list);
	}

	@Override
	protected UserDetailsBean createBean(User item) {

		return null;
	}

}
