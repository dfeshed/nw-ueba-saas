import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSelectedAlertData, selectedAlertId, sortedAlertsData, sortBy } from 'entity-details/reducers/alerts/selectors';
import { initializeAlert, updateSort } from 'entity-details/actions/alert-details';
import { initializeIndicator } from 'entity-details/actions/indicator-details';

const stateToComputed = (state) => ({
  selectedAlertId: selectedAlertId(state),
  alerts: sortedAlertsData(state),
  sortBy: sortBy(state),
  alertData: getSelectedAlertData(state)
});

const dispatchToActions = {
  initializeAlert,
  initializeIndicator,
  updateSort
};

const AlertsContainerComponent = Component.extend({
  tagName: '',
  sortOptions: [
    'severity',
    'date'
  ]
});

export default connect(stateToComputed, dispatchToActions)(AlertsContainerComponent);