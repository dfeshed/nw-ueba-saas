import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';
import alertsColumn from './columnConfig';

const {
  Component
} = Ember;

export default Component.extend({
  classNames: 'rsa-respond-detail-grid',
  // default sorted field
  currentSort: 'alert.risk_score',

  alertsListConfig: [].concat(alertsColumn),

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
    sort(column, direction) {
      let { field } = column;
      column.set('isDescending', (direction === 'asc'));
      this.setProperties({
        currentSort: field,
        direction
      });
      // sorted filed will be alert.source, alert.risk_Score, etc.
      // back-end expects the sort field to be sent as 'score, risk-score'
      // so formatting accordingly and bubbline the action to route
      let index = field.indexOf('.');
      field = field.substring(index + 1, field.length);
      this.sendAction('sortAction', field, direction);
    }
  }
});
