import { createSelector } from 'reselect';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import { getMetaValue } from '../util';


/*
 * An array to store possible event types, currently just logs and network
 * This could eventually hold additional types, and it is used to look up
 * events by their medium value for forcing the recon view to be something
 * specific. Just forces to text view with logs right now.
 * - name {string} the name of the event type
 * - medium {int} the code for the event type
 * - forcedView {object} an entry from RECON_VIEW_TYPES_BY_NAME or null if you do not want to force a view
 */
const EVENT_TYPES = [
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
const EVENT_TYPES_BY_NAME = {};
EVENT_TYPES.forEach((t) => EVENT_TYPES_BY_NAME[t.name] = t);
const DEFAULT_EVENT_TYPE = EVENT_TYPES_BY_NAME.NETWORK;
const HTTP_DATA = 80;

// Takes meta array directly rather than part of redux state object
const _metaDirect = (meta) => meta;

// TODO, once Immutable.find is a thing, remove this asMutable call
const _meta = (state) => {
  return state.meta.asMutable().meta || [];
};

// Takes meta from state
const _eventMeta = (state) => state.recon.meta.meta || [];

// Takes queryInputs from state
const _queryInputs = (state) => state.recon.data.queryInputs || {};

// Takes queryNode from state
const _queryNode = (state) => state.investigate ? state.investigate.queryNode : {};

const _determineEventType = (meta) => {
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
  _meta,
  (meta) => {
    const service = meta.find((d) => d[0] === 'service');
    return !!service && service[1] === HTTP_DATA;
  }
);

export const eventType = createSelector(
  _meta,
  _determineEventType
);

export const eventTypeFromMetaArray = createSelector(
  _metaDirect,
  _determineEventType
);

export const isEndpointEvent = createSelector(
  _meta,
  (meta) => meta.some((d) => d[0] === 'nwe.callback_id')
);

export const nweCallbackId = createSelector(
  isEndpointEvent,
  _meta,
  (isEndpointEvent, meta) => {
    if (isEndpointEvent) {
      const [ client ] = meta.filter((d) => d[0] === 'agent.id');
      return client ? client[1] : null;
    }
  }
);

export const isLogEvent = createSelector(
  eventType,
  isEndpointEvent,
  (eventType, isEndpointEvent) => eventType.name === EVENT_TYPES_BY_NAME.LOG.name && !isEndpointEvent
);

export const processAnalysisQueryString = createSelector(
  _eventMeta,
  _queryInputs,
  _queryNode,
  (eventMeta, queryInputs, queryNode) => {
    const timeStr = `st=${queryInputs.startTime}&et=${queryInputs.endTime}`;
    const osType = getMetaValue('OS', eventMeta);
    const agentId = getMetaValue('agent.id', eventMeta);
    const checksumSha256 = getMetaValue('checksum.src', eventMeta);
    const vpid = getMetaValue('process.vid.src', eventMeta);
    const fileName = getMetaValue('filename.src', eventMeta);
    const { serviceId } = queryNode;
    const osTypeParam = `osType=${osType}&vid=${vpid}`;
    const hostName = getMetaValue('alias.host', eventMeta);

    const queryParams = `checksum=${checksumSha256}&sid=${serviceId}&aid=${agentId}&hn=${hostName}&pn=${fileName}&${timeStr}&${osTypeParam}`;
    return queryParams;
  }
);

export const agentId = createSelector(
    _eventMeta,
    (eventMeta) => {
      return getMetaValue('agent.id', eventMeta);
    }
);

export const isProcessAnalysisDisabled = createSelector(
  _eventMeta,
  (eventMeta) => {
    if (getMetaValue('process.vid.src', eventMeta)) {
      return false;
    }
    return true;
  }
);
