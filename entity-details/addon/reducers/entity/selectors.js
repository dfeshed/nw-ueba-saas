import { createSelector } from 'reselect';

export const entityId = (state) => state.entity.entityId;

export const entityType = (state) => state.entity.entityType;

export const entityDetails = (state) => state.entity.entityDetails;

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