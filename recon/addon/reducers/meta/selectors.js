import reselect from 'reselect';

const { createSelector } = reselect;

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
const DEFAULT_EVENT_TYPE = EVENT_TYPES_BY_NAME.NETWORK;

// Takes meta array directly rather than part of redux state object
const metaDirect = (meta) => meta;

// TODO, once Immutable.find is a thing, remove this asMutable call
const meta = (state) => {
  return state.meta.asMutable().meta || [];
};

const determineEventType = (meta) => {
  if (!meta || meta.length === 0) {
    // network event type is default
    return DEFAULT_EVENT_TYPE;
  }

  const medium = meta.find((entry) => {
    return entry[0] === 'medium';
  });

  // handle odd case where there just is no medium
  if (!medium) {
    return DEFAULT_EVENT_TYPE;
  }

  const eventType = EVENT_TYPES.findBy('medium', medium[1]);

  // Unknown event type? Just return the default.
  return eventType || DEFAULT_EVENT_TYPE;
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

export const isEndpointEvent = createSelector(
  meta,
  (meta) => meta.some((d) => d[0] === 'nwe.callback_id')
);

export const nweCallbackId = createSelector(
  isEndpointEvent,
  meta,
  (isEndpointEvent, meta) => {
    if (isEndpointEvent) {
      const [ client ] = meta.filter((d) => d[0] === 'client');
      return client ? client[1] : null;
    }
  }
);

export const isLogEvent = createSelector(
  eventType,
  isEndpointEvent,
  (eventType, isEndpointEvent) => eventType.name === EVENT_TYPES_BY_NAME.LOG.name && !isEndpointEvent
);

export const isNetworkEvent = createSelector(
  eventType,
  (eventType) => eventType.name === EVENT_TYPES_BY_NAME.NETWORK.name
);