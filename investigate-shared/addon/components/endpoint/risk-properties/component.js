import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

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

  @computed('activeAlertTab', 'alertsData')
  tabs(activeAlertTab, alertsData) {
    const alertCount = alertsData ? alertsData.alertCount : [];
    return ALERT_TABS.map((tab) => ({
      ...tab,
      selected: tab.name === activeAlertTab,
      count: alertCount[tab.name] ? alertCount[tab.name] : 0
    }));
  }
});
