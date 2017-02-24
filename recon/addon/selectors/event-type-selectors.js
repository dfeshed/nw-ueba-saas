import reselect from 'reselect';

import { EVENT_TYPES_BY_NAME } from 'recon/utils/event-types';

const { createSelector } = reselect;

const eventTypeName = (recon) => recon.data.eventType.name;

export const isLogEvent = createSelector(
  eventTypeName,
  (eventTypeName) => eventTypeName === EVENT_TYPES_BY_NAME.LOG.name
);

export const isNetworkEvent = createSelector(
  eventTypeName,
  (eventTypeName) => eventTypeName === EVENT_TYPES_BY_NAME.NETWORK.name
);
