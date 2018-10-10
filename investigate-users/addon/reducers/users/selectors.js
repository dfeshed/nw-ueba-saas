import { createSelector } from 'reselect';
import _ from 'lodash';

const _usersSeverity = (state) => state.users.usersSeverity;

const _existAnomalyTypes = (state) => state.users.existAnomalyTypes;

const _existAlertTypes = (state) => state.users.existAlertTypes;

const _sortField = (state) => state.users.filter.sortField;

export const riskyUserCount = (state) => state.users.riskyUserCount;

export const adminUserCount = (state) => state.users.adminUserCount;

export const watchedUserCount = (state) => state.users.watchedUserCount;

export const getTotalUsers = (state) => state.users.totalUsers;

export const getTopRiskyUsers = (state) => state.users.topUsers;

export const getFavorites = (state) => state.users.favorites;

export const getUsers = (state) => state.users.users;


export const getUserFilter = (state) => state.users.filter;

export const getSortField = createSelector(
  [_sortField],
  (sortField) => {
    return { id: sortField };
  });

export const allUsersReceived = createSelector(
  [getUsers, getTotalUsers],
  (users, _totalUsers) => {
    return _totalUsers && _totalUsers <= users.length;
  });


export const isRisky = createSelector(
  [getUserFilter],
  (filter) => {
    return filter.minScore === 0;
  });

export const getFilterSeverity = createSelector(
  [getUserFilter],
  (filter) => {
    return filter.severity;
  });

export const isAdmin = createSelector(
  [getUserFilter],
  (filter) => {
    return filter.userTags && filter.userTags.includes('admin');
  });

export const isWatched = createSelector(
  [getUserFilter],
  (filter) => {
    return filter.isWatched === true;
  });

export const getExistAlertTypes = createSelector(
  [_existAlertTypes],
  (existAlertTypes) => {
    return _.map(existAlertTypes, (value) => {
      return { id: value.alertTypes.getIn('0'), name: `${value.alertTypes.getIn('0')} (${value.count} Users)` };
    });
  });

export const getExistAnomalyTypes = createSelector(
  [_existAnomalyTypes],
  (existAnomalyTypes) => {
    return _.toArray((_.mapValues(existAnomalyTypes, (value, key) => ({ id: key, name: `${key} (${value} Users)` }))));
  });

export const getSelectedAlertTypes = createSelector(
  [getUserFilter, getExistAlertTypes],
  (filter, alertTypes) => {
    return _.filter(alertTypes, ({ id }) => filter.alertTypes && filter.alertTypes.includes(id));
  });

export const getSelectedAnomalyTypes = createSelector(
  [getUserFilter, getExistAnomalyTypes],
  (filter, anomalyTypes) => {
    return _.filter(anomalyTypes, ({ id }) => filter.indicatorTypes && filter.indicatorTypes.includes(id));
  });

export const getUsersSeverity = createSelector(
  [_usersSeverity],
  ([usersSeverity]) => {
    if (usersSeverity) {
      return {
        critical: usersSeverity.Critical.userCount,
        high: usersSeverity.High.userCount,
        medium: usersSeverity.Medium.userCount,
        low: usersSeverity.Low.userCount
      };
    }
  });