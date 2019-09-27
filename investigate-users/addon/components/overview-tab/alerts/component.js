import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getTopAlerts, hasTopAlerts, topAlertsError, timeframesForDateTimeFilter, topAlertsEntity, topAlertsTimeFrame } from 'investigate-users/reducers/alerts/selectors';
import { entityFilter } from 'investigate-users/reducers/users/selectors';
import { getTopTenAlerts } from 'investigate-users/actions/alert-details';

const stateToComputed = (state) => ({
  topAlerts: getTopAlerts(state),
  topAlertsError: topAlertsError(state),
  hasTopAlerts: hasTopAlerts(state),
  topAlertsEntity: topAlertsEntity(state),
  topAlertsTimeFrame: topAlertsTimeFrame(state),
  entityFilter,
  timeframesForDateTimeFilter
});


const dispatchToActions = {
  getTopTenAlerts
};

const OverviewAlertComponent = Component.extend({
  classNames: 'user-overview-tab_alerts_alerts',
  actions: {
    updateEntityType(entityType) {
      this.send('getTopTenAlerts', entityType);
    },
    updateTimeRange(timeRange) {
      this.send('getTopTenAlerts', undefined, timeRange);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(OverviewAlertComponent);