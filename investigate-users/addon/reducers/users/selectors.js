import { createSelector } from 'reselect';
import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';

const _usersSeverity = (state) => state.users.usersSeverity;

const _existAnomalyTypes = (state) => state.users.existAnomalyTypes;

const _existAlertTypes = (state) => state.users.existAlertTypes;

const _sortField = (state) => state.users.filter.sortField;

export const _favorites = (state) => state.users.favorites;

export const riskyUserCount = (state) => state.users.riskyUserCount;

export const watchedUserCount = (state) => state.users.watchedUserCount;

export const getTotalUsers = (state) => state.users.totalUsers;

export const getTopRiskyUsers = (state) => state.users.topUsers;

export const usersError = (state) => state.users.usersError;

export const topUsersError = (state) => state.users.topUsersError;

export const allWatched = (state) => state.users.allWatched;

export const getUsers = (state) => state.users.users;

export const severityFilter = ['Low', 'Medium', 'High', 'Critical'];

export const entityFilter = ['userId', 'ja3', 'sslSubject'];

export const getUserFilter = (state) => state.users.filter;

export const hasTopRiskyUsers = createSelector(
  [getTopRiskyUsers],
  (users) => {
    return users !== null && users.length > 0;
  });

export const hasUsers = createSelector(
  [getUsers],
  (users) => {
    return users !== null && users.length > 0;
  });

export const selectedEntityType = createSelector(
  [getUserFilter],
  (filter) => {
    return filter.entityType;
  });

export const getFavorites = createSelector(
  [_favorites],
  (favorites) => {
    return _.sortBy(favorites, ['filterName']);
  });

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

export const getSelectedSeverity = createSelector(
  [getUserFilter],
  (filter) => {
    return filter.severity ? filter.severity.asMutable() : null;
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
    const i18n = lookup('service:i18n');
    return _.map(existAlertTypes, (value) => {
      return {
        id: value.alertTypes.getIn('0'),
        displayLabel: `${i18n.t(`investigateUsers.alerts.alertNames.${value.alertTypes.getIn('0')}.name`)} (${value.count} Users)`
      };
    });
  });

export const getExistAnomalyTypes = createSelector(
  [_existAnomalyTypes],
  (existAnomalyTypes) => {
    const i18n = lookup('service:i18n');
    return _.toArray((_.mapValues(existAnomalyTypes, (value, key) => ({
      id: key,
      displayLabel: `${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${key}.name`)} (${value} Users)`
    }))));
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
    if (usersSeverity && usersSeverity.Critical) {
      return {
        critical: usersSeverity.Critical.userCount,
        high: usersSeverity.High.userCount,
        medium: usersSeverity.Medium.userCount,
        low: usersSeverity.Low.userCount
      };
    }
    return {
      critical: 0,
      high: 0,
      medium: 0,
      low: 0
    };
  });