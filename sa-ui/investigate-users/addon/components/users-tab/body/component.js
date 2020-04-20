import Component from '@ember/component';
import { connect } from 'ember-redux';
import { usersError, hasUsers } from 'investigate-users/reducers/users/selectors';

const stateToComputed = (state) => ({
  hasUsers: hasUsers(state),
  usersError: usersError(state)
});

const UsersTabComponent = Component.extend({
  classNames: 'users-tab_body'
});

export default connect(stateToComputed)(UsersTabComponent);