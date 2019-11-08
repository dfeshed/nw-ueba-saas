import { createSelector } from 'reselect';
import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';
import entityAnomalyMap from 'investigate-users/utils/entity-anomaly-map';

const _usersSeverity = (state) => state.users.usersSeverity;

const _existAnomalyTypes = (state) => state.users.existAnomalyTypes;

const _existAlertTypes = (state) => state.users.existAlertTypes;

const _sortField = (state) => state.users.filter.sortField;

const _topUsers = (state) => state.users.topUsers;

export const _favorites = (state) => state.users.favorites;

export const riskyUserCount = (state) => state.users.riskyUserCount;

export const watchedUserCount = (state) => state.users.watchedUserCount;

export const totalEntitiesCount = (state) => state.users.totalUsers;

export const usersError = (state) => state.users.usersError;

export const topUsersError = (state) => state.users.topUsersError;

export const allWatched = (state) => state.users.allWatched;

export const getUsers = (state) => state.users.users;

export const trendRange = (state) => state.users.trendRange;

export const sortOnTrending = (state) => state.users.sortOnTrending;

export const severityFilter = ['', 'Low', 'Medium', 'High', 'Critical'];

export const entityFilter = ['userId', 'ja3', 'sslSubject'];

export const getUserFilter = (state) => state.users.filter;

export const getTopRiskyUsers = createSelector(
  [_topUsers, trendRange, sortOnTrending],
  (users, { key }, sortTrending) => {
    return _.orderBy(_.map(users, (user) => {
      const filteredAlerts = _.filter(user.alerts, (alert) => alert.userScoreContribution !== 0);
      const alertGroup = _.groupBy(filteredAlerts, (alert) => alert.severity);
      return {
        id: user.id,
        displayName: user.displayName,
        followed: user.followed,
        scoreSeverity: user.scoreSeverity,
        score: user.score,
        trendingScore: user.trendingScore ? user.trendingScore[key] : 0,
        alertGroup: {
          Critical: alertGroup.Critical ? alertGroup.Critical.length : 0,
          High: alertGroup.High ? alertGroup.High.length : 0,
          Medium: alertGroup.Medium ? alertGroup.Medium.length : 0,
          Low: alertGroup.Low ? alertGroup.Low.length : 0
        }
      };
    }), [sortTrending ? 'trendingScore' : 'score'], ['desc']);
  });

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
    return [{ id: null, filterName: 'Select' }].concat(_.sortBy(favorites, ['filterName']));
  });

export const getSortField = createSelector(
  [_sortField],
  (sortField) => {
    return { id: sortField };
  });

export const allUsersReceived = createSelector(
  [getUsers, totalEntitiesCount],
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
    const i18n = lookup('service:i18n');
    return _.map(existAlertTypes, (value) => {
      return {
        id: value.alertTypes.getIn('0'),
        displayLabel: `${i18n.t(`investigateUsers.alerts.alertNames.${value.alertTypes.getIn('0')}.name`)} (${value.count} Entities)`
      };
    });
  });

export const getExistAnomalyTypes = createSelector(
  [selectedEntityType, _existAnomalyTypes],
  (entityType, existAnomalyTypes) => {
    const anomalyKeys = entityAnomalyMap[entityType];
    const i18n = lookup('service:i18n');
    const mappedArray = [];
    _.forEach(existAnomalyTypes, (value, key) => {
      if (anomalyKeys.includes(key)) {
        mappedArray.push({
          id: key,
          displayLabel: `${i18n.t(`investigateUsers.alerts.indicator.indicatorNames.${key}.name`)} (${value} ${i18n.t(`investigateUsers.entityTypes.${entityType}`)})`
        });
      }
    });
    return mappedArray;
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