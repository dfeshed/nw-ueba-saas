import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';

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
    return IncidentHelper.riskScoreToBadgeLevel(riskScore);
  },

  /**
   * @name sourceShortName
   * @description returns the source's defined short-name
   * @public
   */
  sourceShortName(source) {
    return IncidentHelper.sourceShortName(source);
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
