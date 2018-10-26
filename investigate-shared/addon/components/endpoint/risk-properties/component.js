import Component from '@ember/component';
import {
  riskScoreContext, riskScoreContextError, activeRiskSeverityTab,
  alertsError, selectedAlert
} from 'investigate-shared/selectors/risk/selectors';
import layout from './template';
import computed from 'ember-computed-decorators';
import { next } from '@ember/runloop';
import _ from 'lodash';

const ALERT_TABS = [
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.critical',
    name: 'critical',
    color: 'red'
  },
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.high',
    name: 'high',
    color: 'orange'
  },
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.medium',
    name: 'medium',
    color: 'yellow'
  },
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.all',
    name: 'all'
  }
];

export default Component.extend({

  layout,

  classNames: ['risk-properties'],

  didReceiveAttrs() {
    this._super(...arguments);
    const state = {
      risk: this.get('riskState')
    };

    this.setProperties({
      activeRiskSeverityTab: activeRiskSeverityTab(state),
      riskScoreContext: riskScoreContext(state),
      riskScoreContextError: riskScoreContextError(state),
      alertsError: alertsError(state),
      selectedAlert: selectedAlert(state)
    });
  },

  @computed('activeRiskSeverityTab', 'riskScoreContext')
  tabs(activeRiskSeverityTab, riskScoreContext) {
    const alertCount = riskScoreContext ? { ...riskScoreContext.distinctAlertCount } : [];

    // Count All alerts by adding alerts of critical, high and medium severities.
    alertCount.all = alertCount.critical + alertCount.high + alertCount.medium;

    const checksum = riskScoreContext ? riskScoreContext.hash : null;

    this.changeLandingSeverityTab(activeRiskSeverityTab, checksum, alertCount);

    return ALERT_TABS.map((tab) => ({
      ...tab,
      checksum,
      selected: tab.name === activeRiskSeverityTab,
      count: alertCount[tab.name]
    }));
  },

  @computed('activeRiskSeverityTab', 'riskScoreContext')
  contexts(activeRiskSeverityTab, riskScoreContext) {
    const severity = _.upperFirst(activeRiskSeverityTab);
    const alertContext = riskScoreContext && riskScoreContext.categorizedAlerts ? riskScoreContext.categorizedAlerts[severity] : null;
    if (alertContext) {
      return Object.keys(alertContext).map((key) => ({
        alertName: key,
        alertCount: alertContext[key].alertCount,
        eventCount: alertContext[key].eventContexts.length,
        context: alertContext[key].eventContexts
      }));
    }
    return null;
  },

  /**
   * Change Landing Severity tab if severity for currentActiveTab is 0.
   * @public
   */
  changeLandingSeverityTab(activeRiskSeverityTab, checksum, alertCount) {

    let nextLandingTab = activeRiskSeverityTab;

    while (alertCount[nextLandingTab] === 0) {

        // Find index of current risk severity tab in ALERT_TABS array
      const index = ALERT_TABS.findIndex((tab) => tab.name === nextLandingTab);

      if (index <= 2) {
        nextLandingTab = ALERT_TABS[index + 1].name;
      } else {
        break;
      }
    }

    if (nextLandingTab !== activeRiskSeverityTab) {
      next(() => {
        if (this.get('defaultAction')) {
          this.get('defaultAction')(checksum, nextLandingTab);
        }
      });
    }
  }
});
