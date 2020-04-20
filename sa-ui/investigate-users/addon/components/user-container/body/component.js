import Component from '@ember/component';
import { connect } from 'ember-redux';
import { activeTabName } from 'investigate-users/reducers/tabs/selectors';
import { userId, alertId, indicatorId } from 'investigate-users/reducers/user/selectors';
import { resetUser } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  activeTabName: activeTabName(state),
  userId: userId(state),
  alertId: alertId(state),
  indicatorId: indicatorId(state)
});

const dispatchToActions = {
  resetUser
};
const BodyComponent = Component.extend({
  classNames: 'user-body'
});

export default connect(stateToComputed, dispatchToActions)(BodyComponent);