import { UPDATE_ACTIVE_TAB } from './types';
import { getRiskyUserCount, getWatchedUserCount, getUserOverview } from './user-details';
import { resetAlerts, getTopTenAlerts, updateFilter as updateFilterForALerts, getExistAnomalyTypesForAlert, getAlertsForTimeline } from './alert-details';
import { resetUsers, getExistAnomalyTypes, getExistAlertTypess, getFavorites, updateFilter } from './user-tab-actions';

/**
 * This function is responsible to trigger all respective action for overview tab.
 * This will fetch top five risky user, top 10 alerts and (risky, watched, admin )users count.
 * @private
*/
const _overviewTabCreation = () => {
  return (dispatch) => {
    dispatch(getRiskyUserCount());
    dispatch(getWatchedUserCount());
    dispatch(getUserOverview());
    dispatch(getTopTenAlerts());
    dispatch(getAlertsForTimeline());
  };
};

/**
 * This function is responsible to trigger all respective action for users tab.
 * This will ensure clean state for users and then fetch users and severity count for given filter.
 * Along with users this will pull anomaly types, users counts and alets count for enriched filter.
 * Filter will be used for jumping from overview tab with severity filter selected.
 * @private
*/
const _usersTabCreation = (filter) => {
  return (dispatch) => {
    dispatch(resetUsers());
    dispatch(getRiskyUserCount());
    dispatch(getWatchedUserCount());
    dispatch(updateFilter(filter));
    dispatch(getExistAnomalyTypes());
    dispatch(getExistAlertTypess());
    dispatch(getFavorites());
  };
};

/**
 * This function is responsible to trigger all respective action for alerts tab.
 * This will ensure clean state for alerts and then fetch alerts and anomaly types for given filter.
 * Filter will be used for jumping from overview tab with severity filter selected.
 * @private
*/
const _alertsTabCreation = (filter) => {
  return (dispatch) => {
    dispatch(resetAlerts());
    dispatch(getExistAnomalyTypesForAlert());
    dispatch(updateFilterForALerts(filter));
  };
};

// This function is responsible to trigger all respective action calls based on active tab.
export const initTabs = (activeTab, filter) => {
  return (dispatch) => {
    if (activeTab === 'overview') {
      dispatch(_overviewTabCreation());
    } else if (activeTab === 'users') {
      dispatch(_usersTabCreation(filter));
    } else if (activeTab === 'alerts') {
      dispatch(_alertsTabCreation(filter));
    }
    dispatch({
      type: UPDATE_ACTIVE_TAB,
      payload: activeTab
    });
  };
};