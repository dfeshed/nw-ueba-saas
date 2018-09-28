import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
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
  }
];

export default Component.extend({

  layout,

  classNames: ['risk-properties'],

  @computed('activeRiskSeverityTab', 'riskScoreContext')
  tabs(activeRiskSeverityTab, riskScoreContext) {
    const alertCount = riskScoreContext ? riskScoreContext.alertCount : [];
    const checksum = riskScoreContext ? riskScoreContext.hash : null;
    return ALERT_TABS.map((tab) => ({
      ...tab,
      checksum,
      selected: tab.name === activeRiskSeverityTab,
      count: alertCount[tab.name] ? alertCount[tab.name] : 0
    }));
  },

  @computed('activeRiskSeverityTab', 'riskScoreContext')
  contexts(activeAlertTab, riskScoreContext) {
    const severity = _.upperFirst(activeAlertTab);
    const alertContext = riskScoreContext && riskScoreContext.categorizedAlerts ? riskScoreContext.categorizedAlerts[severity] : null;
    if (alertContext) {
      return Object.keys(alertContext).map((key) => ({
        alertName: key,
        alertCount: alertContext[key].alertCount,
        eventCount: alertContext[key].eventContexts.length
      }));
    }
    return null;
  }
});
