import { createSelector } from 'reselect';

const severityMap = {
  Critical: 'danger',
  High: 'high',
  Medium: 'medium',
  Low: 'low'
};

const iconMap = {
  user: 'account-group-5'
};

export const entityId = (state) => state.entity.entityId;

export const entityType = (state) => state.entity.entityType;

export const entityDetails = (state) => state.entity.entityDetails;

export const entityFetchError = (state) => state.entity.entityFetchError;

export const entityDisplayName = createSelector(
  [entityDetails],
  (details) => {
    if (details) {
      return details.displayName;
    }
  });

export const entityScore = createSelector(
  [entityDetails],
  (details) => {
    if (details) {
      return details.score;
    }
  });

export const entitySeverity = createSelector(
  [entityDetails],
  (details) => {
    if (details && details.scoreSeverity) {
      return severityMap[details.scoreSeverity];
    }
  });

export const enityIcon = createSelector(
  [entityType],
  (type) => {
    return iconMap[type];
  });

export const alertsForEntity = createSelector(
  [entityDetails],
  (details) => {
    if (details) {
      return details.alerts;
    }
  });

export const isFollowed = createSelector(
  [entityDetails],
  (details) => {
    if (details) {
      return details.followed;
    }
    return false;
  });