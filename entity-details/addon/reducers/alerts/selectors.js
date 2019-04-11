import { createSelector } from 'reselect';
import _ from 'lodash';

const alertsData = (state) => state.alerts.alerts;

export const sortBy = (state) => state.alerts.sortBy;

export const selectedAlertId = (state) => state.alerts.selectedAlertId;

export const sortedAlertsData = createSelector(
  [sortBy, alertsData],
  (id, alerts) => {
    if (id && alerts) {
      id = id === 'date' ? 'startDate' : id;
      return _.sortBy(alerts, [id]);
    }
  });

export const getSelectedAlertData = createSelector(
  [selectedAlertId, alertsData],
  (id, alerts) => {
    if (id && alerts) {
      return _.find(alerts, { id });
    }
  });

/**
 * Alert is combination of different indicators. Indicators can be from different schemas(Ex. Active Directory, File, Authentication).
 * This selector will return unit list of schemas for given alert.
 *
 * @private
 */
export const alertSources = createSelector(
  [getSelectedAlertData],
  (alertDetails) => {
    if (alertDetails && alertDetails.evidences) {
      const sourceArray = _.map(alertDetails.evidences, (evidence) => evidence.dataEntitiesIds.join(','));
      return _.uniq(sourceArray).join(', ').toUpperCase();
    }
    return '';
  });

export const userScoreContribution = createSelector(
  [getSelectedAlertData],
  (alertDetails) => {
    if (alertDetails) {
      return alertDetails.userScoreContribution;
    }
    return 0;
  });