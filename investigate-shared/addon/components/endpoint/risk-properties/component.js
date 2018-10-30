import Component from '@ember/component';
import {
  riskScoreContext,
  riskScoreContextError,
  activeRiskSeverityTab,
  alertsError,
  selectedAlert,
  currentSeverityContext,
  riskType,
  riskScoringServerError,
  isRiskScoreContextEmpty,
  isRiskScoringServerOffline
} from 'investigate-shared/selectors/risk/selectors';
import layout from './template';
import computed from 'ember-computed-decorators';
import { next } from '@ember/runloop';

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
      risk: this.get('riskState'),
      riskType: this.get('riskType')
    };

    this.setProperties({
      activeRiskSeverityTab: activeRiskSeverityTab(state),
      riskScoreContext: riskScoreContext(state),
      riskScoreContextError: riskScoreContextError(state),
      alertsError: alertsError(state),
      selectedAlert: selectedAlert(state),
      contexts: currentSeverityContext(state),
      riskType: riskType(state),
      riskScoringServerError: riskScoringServerError(state),
      isRiskScoreContextEmpty: isRiskScoreContextEmpty(state),
      isRiskScoringServerOffline: isRiskScoringServerOffline(state)
    });
  },

  @computed('activeRiskSeverityTab', 'riskScoreContext')
  tabs(activeRiskSeverityTab, riskScoreContext) {
    const alertCount = riskScoreContext ? { ...riskScoreContext.distinctAlertCount } : [];

    // Count All alerts by adding alerts of critical, high and medium severities.
    alertCount.all = alertCount.critical + alertCount.high + alertCount.medium;

    const id = riskScoreContext ? (riskScoreContext.hash || riskScoreContext.id) : null;

    this.changeLandingSeverityTab(activeRiskSeverityTab, id, alertCount);

    return ALERT_TABS.map((tab) => ({
      ...tab,
      id,
      selected: tab.name === activeRiskSeverityTab,
      count: alertCount[tab.name]
    }));
  },

  /**
   * Change Landing Severity tab if severity for currentActiveTab is 0.
   * @public
   */
  changeLandingSeverityTab(activeRiskSeverityTab, id, alertCount) {

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
          this.get('defaultAction')(id, nextLandingTab, '0');
        }
      });
    }
  }
});
