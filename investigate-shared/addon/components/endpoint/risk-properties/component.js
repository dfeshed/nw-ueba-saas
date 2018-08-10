import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

const ALERT_TABS = [
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.critical',
    name: 'CRITICAL',
    color: 'red'
  },
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.high',
    name: 'HIGH',
    color: 'orange'
  },
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.medium',
    name: 'MEDIUM',
    color: 'yellow'
  },
  {
    label: 'investigateShared.endpoint.riskProperties.alerts.low',
    name: 'LOW',
    color: 'green'
  }
];

// Alert counts will come from parent soon, hardcoding for now

const ALERT_COUNT =
  {
    CRITICAL: 2,
    HIGH: 8,
    MEDIUM: 20,
    LOW: 30,
    TOTAL: 60
  };


export default Component.extend({

  layout,

  classNames: ['risk-properties'],

  @computed('activeAlertTab')
  tabs(activeAlertTab) {
    return ALERT_TABS.map((tab) => ({ ...tab, selected: tab.name === activeAlertTab, count: ALERT_COUNT[tab.name] }));
  }
});
