import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getTopRiskyUsers } from 'investigate-users/reducers/users/selectors';
import { severityMap } from 'investigate-users/utils/column-config';
import { initiateUser } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  topUsers: getTopRiskyUsers(state)
});

const dispatchToActions = {
  initiateUser
};

const OverviewUserComponent = Component.extend({
  severityMap
});

export default connect(stateToComputed, dispatchToActions)(OverviewUserComponent);