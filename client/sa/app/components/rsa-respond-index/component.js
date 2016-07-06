import Ember from 'ember';
import { incidentRiskThreshold } from 'sa/incident/constants';

export default Ember.Component.extend({
  model: null,

  respondMode: Ember.inject.service(),

  isCardMode: Ember.computed.equal('respondMode.selected', 'card'),

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  badgeStyle(riskScore) {
    if (riskScore < incidentRiskThreshold.LOW) {
      return 'low';
    } else if (riskScore < incidentRiskThreshold.MEDIUM) {
      return 'medium';
    } else if (riskScore < incidentRiskThreshold.HIGH) {
      return 'high';
    } else {
      return 'danger';
    }
  },

  /**
   * @name sourceShortName
   * @description returns the initials of the source
   * @public
   */
  sourceShortName(source) {
    return source.match(/\b\w/g).join('');
  },

  actions: {
    gotoIncidentDetail(...args) {
      this.sendAction('gotoIncidentDetail', ...args);
    },
    saveIncident(...args) {
      this.sendAction('saveIncident', ...args);
    }
  }
});
