import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getTopRiskyUsers } from 'investigate-users/reducers/users/selectors';
import { severityMap } from 'investigate-users/utils/column-config';

const stateToComputed = (state) => ({
  topUsers: getTopRiskyUsers(state)
});

const OverviewUserComponent = Component.extend({
  severityMap
});

export default connect(stateToComputed)(OverviewUserComponent);