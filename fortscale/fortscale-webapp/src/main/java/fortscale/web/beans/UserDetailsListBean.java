package fortscale.web.beans;

import java.util.List;

import fortscale.domain.core.User;
import fortscale.web.beans.ListBean;

public class UserDetailsListBean extends ListBean<User, UserDetailsBean> {

	public UserDetailsListBean(List<User> list) {
		super(list);
	}

	@Override
	protected UserDetailsBean createBean(User item) {
//		return new UserDetailsBean(item);
		return null;
	}

}
