import Component from '@ember/component';
import { connect } from 'ember-redux';
import { initiateUser } from 'investigate-users/actions/user-details';

const dispatchToActions = {
  initiateUser
};

const OverviewAlertComponent = Component.extend({
  alert: null,
  classNames: 'user-overview-tab_alerts_alerts_container_pill'
});

export default connect(null, dispatchToActions)(OverviewAlertComponent);