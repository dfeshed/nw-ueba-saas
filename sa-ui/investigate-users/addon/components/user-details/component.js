import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { userId, alertId, indicatorId } from 'investigate-users/reducers/user/selectors';
import { resetUser } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  userId: userId(state),
  alertId: alertId(state),
  indicatorId: indicatorId(state)
});

const dispatchToActions = {
  resetUser
};
const UserDetailsComponent = Component.extend({
  tagName: '',

  @computed('userId', 'alertId', 'indicatorId')
  entityDetails(userId, alertId, indicatorId) {
    return {
      entityId: userId,
      entityType: 'user',
      alertId,
      indicatorId
    };
  }
});

export default connect(stateToComputed, dispatchToActions)(UserDetailsComponent);