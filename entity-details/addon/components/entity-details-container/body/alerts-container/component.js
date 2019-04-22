import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSelectedAlertData, selectedAlertId, sortedAlertsData, sortBy, hasAlerts, alertError } from 'entity-details/reducers/alerts/selectors';
import { selectedIndicatorId } from 'entity-details/reducers/indicators/selectors';
import { selectAlert, updateSort } from 'entity-details/actions/alert-details';
import { initializeIndicator } from 'entity-details/actions/indicator-details';
import computed from 'ember-computed-decorators';
import { next } from '@ember/runloop';

const stateToComputed = (state) => ({
  selectedAlertId: selectedAlertId(state),
  selectedIndicatorId: selectedIndicatorId(state),
  alerts: sortedAlertsData(state),
  sortBy: sortBy(state),
  alertError: alertError(state),
  hasAlerts: hasAlerts(state),
  alertData: getSelectedAlertData(state)
});

const dispatchToActions = {
  selectAlert,
  initializeIndicator,
  updateSort
};

const AlertsContainerComponent = Component.extend({
  classNames: ['entity-details-container-body_alerts_list'],
  sortOptions: [
    'severity',
    'date'
  ],
  /**
   * Sometimes selected alert may be not in view port. This function will endure to have alert in view port.
   * @private
   */
  _scrollToAlert() {
    // Container div to set scroll Top.
    const containerDiv = this.element.querySelector('.entity-details-container-body_alerts_list_content');
    // Selected alert which need to be there in view port.
    const selectedAlertPill = this.element.querySelector('.entity-details-container-body_alerts_list_content_alert_details_pill.selectedAlert');
    // Reducing alert container's header height to take care of proper positioning.
    containerDiv.scrollTop = selectedAlertPill.offsetTop - 150;
  },
  /**
   * this computed property to move selcted alert in view port.
   * @private
   */
  @computed('selectedAlertId')
  selectedAlert(selectedAlertId) {
    if (selectedAlertId) {
      next(this, '_scrollToAlert');
      return selectedAlertId;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertsContainerComponent);