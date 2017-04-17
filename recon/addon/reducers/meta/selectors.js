import reselect from 'reselect';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

const HTTP_DATA = 80;

/*
 * An array to store possible event types, currently just logs and network
 * This could eventually hold additional types, and it is used to look up
 * events by their medium value for forcing the recon view to be something
 * specific. Just forces to text view with logs right now.
 * - name {string} the name of the event type
 * - medium {int} the code for the event type
 * - forcedView {object} an entry from RECON_VIEW_TYPES_BY_NAME or null if you do not want to force a view
 */
export const EVENT_TYPES = [
  {
    name: 'LOG',
    medium: 32,
    forcedView: RECON_VIEW_TYPES_BY_NAME.TEXT
  },
  {
    name: 'NETWORK',
    medium: 1,
    forcedView: null
  }
];

export const EVENT_TYPES_BY_NAME = {};
EVENT_TYPES.forEach((t) => EVENT_TYPES_BY_NAME[t.name] = t);

const { createSelector } = reselect;

// Takes meta array directly rather than part of redux state object
const metaDirect = (meta) => meta;
const meta = (state) => state.meta.meta || [];

const determineEventType = (meta) => {
  if (meta.length === 0) {
    // network event type is default
    return EVENT_TYPES_BY_NAME.NETWORK;
  }

  const medium = meta.find((entry) => {
    return entry[0] === 'medium';
  });

  return EVENT_TYPES.findBy('medium', medium[1]);
};

export const isHttpData = createSelector(
  meta,
  (meta) => {
    const service = meta.find((d) => d[0] === 'service');
    return !!service && service[1] === HTTP_DATA;
  }
);

export const isNotHttpData = createSelector(
  isHttpData,
  (isHttpData) => !isHttpData
);

export const eventType = createSelector(
  meta,
  determineEventType
);

export const eventTypeFromMetaArray = createSelector(
  metaDirect,
  determineEventType
);

export const isLogEvent = createSelector(
  eventType,
  (eventType) => eventType.name === EVENT_TYPES_BY_NAME.LOG.name
);

export const isNetworkEvent = createSelector(
  eventType,
  (eventType) => eventType.name === EVENT_TYPES_BY_NAME.NETWORK.name
);