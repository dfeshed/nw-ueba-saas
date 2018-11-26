import { createSelector } from 'reselect';

export const entityId = (state) => state.entity.entityId;

export const entityType = (state) => state.entity.entityType;

export const entityDetails = (state) => state.entity.entityDetails;

export const alerts = createSelector(
  [entityDetails],
  (details) => {
    return details.alerts;
  });