import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getTopRiskyUsers, hasTopRiskyUsers, topUsersError, selectedEntityType } from 'investigate-users/reducers/users/selectors';
import { severityMap } from 'investigate-users/utils/column-config';
import { initiateUser, updateEntityType } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  topUsers: getTopRiskyUsers(state),
  topUsersError: topUsersError(state),
  hasTopRiskyUsers: hasTopRiskyUsers(state),
  entityType: selectedEntityType(state)

});

const dispatchToActions = {
  initiateUser,
  updateEntityType
};

const OverviewUserComponent = Component.extend({
  severityMap
});

export default connect(stateToComputed, dispatchToActions)(OverviewUserComponent);