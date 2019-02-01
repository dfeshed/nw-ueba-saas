import { createSelector } from 'reselect';
import moment from 'moment';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import { getMetaValue } from '../util';
import ENDPOINT_META_CONFIG from './meta-config';
import { EVENT_TYPES } from 'component-lib/constants/event-types';

/*
 * An array to store possible event types, currently just logs and network
 * This could eventually hold additional types, and it is used to look up
 * events by their medium value for forcing the recon view to be something
 * specific. Just forces to text view with logs right now.
 * - name {string} the name of the event type
 * - medium {int} the code for the event type
 * - forcedView {object} an entry from RECON_VIEW_TYPES_BY_NAME or null if you do not want to force a view
 */
const EVENT_VIEW_TYPES = [
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
EVENT_VIEW_TYPES.forEach((t) => EVENT_TYPES_BY_NAME[t.name] = t);
const DEFAULT_EVENT_TYPE = EVENT_TYPES_BY_NAME.NETWORK;
const HTTP_DATA = 80;

// Takes meta array directly rather than part of redux state object
const _metaDirect = (meta) => meta;

// ACCESSOR FUNCTIONS
const _endpointId = (state) => state.recon.data.endpointId;
const _eventMeta = (state) => state.recon.meta.meta || [];
const _eventType = (state) => state.data.eventType;
// TODO, once Immutable.find is a thing, remove this asMutable call
const _meta = (state) => state.meta.asMutable().meta || [];
const _queryInputs = (state) => state.recon.data.queryInputs || {};
const _queryNode = (state) => state.investigate ? state.investigate.queryNode : {};

const _determineEventType = (meta, eventType) => {
  let type = DEFAULT_EVENT_TYPE;
  if (!meta || meta.length === 0) {
    // No meta, see if eventType was defined and use that instead
    if (eventType === EVENT_TYPES.LOG) {
      type = EVENT_TYPES_BY_NAME.LOG;
    }
  } else {
    // Have meta, determine from that
    const medium = meta.find((entry) => entry[0] === 'medium');
    if (medium) {
      const matchedEventType = EVENT_VIEW_TYPES.findBy('medium', medium[1]);
      if (matchedEventType) {
        type = matchedEventType;
      }
    }
  }
  return type;
};

const findMetaValue = (fieldName, metas) => {
  const metaPair = metas.find((d) => d[0] === fieldName);
  return metaPair ? metaPair[1] : '';
};

const SCRIPT_FILES = ['cmd.exe', 'powershell.exe', 'wscript.exe', 'cscript.exe', 'rundll32.exe'];

// script file may come as filename.src. Make sure to ignore them, when multiple filename.src is available
const getSrcFilename = (metas) => {
  const metaPairs = metas.filter((d) => d[0] === 'filename.src');
  let srcFilename = '';
  if (metaPairs.length === 1) {
    srcFilename = metaPairs[0][1];
  } else if (metaPairs.length > 1) {
    srcFilename = metaPairs.filter((d) => !SCRIPT_FILES.includes(d[1]))[0][1];
  }
  return srcFilename;
};

const getSrcParam = (metas) => {
  const srcFilename = getSrcFilename(metas);
  const metaPair = metas.find((d) => d[0] === 'param.src' && !d[1].includes(srcFilename));
  return metaPair ? metaPair[1] : '';
};


export const isHttpData = createSelector(
  [_meta],
  (meta) => {
    const service = meta.find((d) => d[0] === 'service');
    return !!service && service[1] === HTTP_DATA;
  }
);

export const eventType = createSelector(
  [_meta, _eventType],
  _determineEventType
);

export const eventTypeFromMetaArray = createSelector(
  [_metaDirect],
  _determineEventType
);

export const isEndpointEvent = createSelector(
  [_eventType, _meta],
  (eventType, meta) => {
    return (eventType) ? eventType === EVENT_TYPES.ENDPOINT : meta.some((d) => d[0] === 'nwe.callback_id');
  }
);

export const nweCallbackId = createSelector(
  [isEndpointEvent, _meta],
  (isEndpointEvent, meta) => {
    if (isEndpointEvent) {
      const [ client ] = meta.filter((d) => d[0] === 'agent.id');
      return client ? client[1] : null;
    }
  }
);

export const isLogEvent = createSelector(
  [_eventType, _meta, isEndpointEvent],
  (eventType, meta, isEndpointEvent) => {
    if (eventType) {
      return eventType === EVENT_TYPES.LOG;
    }
    const type = _determineEventType(meta);
    return type === EVENT_TYPES_BY_NAME.LOG && !isEndpointEvent;
  }
);

/**
 * Creates a String that can be used for a URL query param fragment. Fallbacks
 * for serviceId and start/end time are in place if Recon is opened in Respond
 * versus Investigate Events.
 * @public
 */
export const processAnalysisQueryString = createSelector(
  [_eventMeta, _queryInputs, _queryNode, _endpointId],
  (eventMeta, queryInputs, queryNode, endpointId) => {
    const agentId = getMetaValue('agent.id', eventMeta);
    const checksumSha256 = getMetaValue('checksum.src', eventMeta);
    const fileName = getMetaValue('filename.src', eventMeta);
    const hostName = getMetaValue('alias.host', eventMeta);
    const osType = getMetaValue('OS', eventMeta);
    const pvid = getMetaValue('process.vid.src', eventMeta);
    // The serviceId could be in queryNode if Recon was spawned from
    // Investigate Events, or in recon.data.endpointId if spawned from Respond.
    const serviceId = (queryNode && queryNode.serviceId) ? queryNode.serviceId : endpointId;
    let timeStr;


    // If the time range is not defined in queryInputs, we'll set it to the
    // last 7 days
    if (queryInputs && !isNaN(queryInputs.startTime)) {
      timeStr = `st=${queryInputs.startTime}&et=${queryInputs.endTime}`;
    } else {
      const now = moment();
      const endDate = now.unix();
      const startDate = now.subtract(7, 'days').unix();
      timeStr = `st=${startDate}&et=${endDate}`;
    }
    return [
      `checksum=${checksumSha256}`,
      `serverId=${serviceId}`,
      `sid=${serviceId}`,
      `aid=${agentId}`,
      `hn=${hostName}`,
      `pn=${fileName}`,
      `osType=${osType}`,
      `vid=${pvid}`,
      `${timeStr}`
    ].join('&');
  }
);

export const agentId = createSelector(
  [_eventMeta],
  (eventMeta) => {
    return getMetaValue('agent.id', eventMeta);
  }
);

export const endpointServiceId = createSelector(
  [_eventMeta],
  (eventMeta) => {
    const sid = getMetaValue('nwe.callback_id', eventMeta) || '';
    return sid.substring(6);
  }
);

export const isProcessAnalysisDisabled = createSelector(
  [_eventMeta],
  (eventMeta) => {
    if (getMetaValue('process.vid.src', eventMeta)) {
      return false;
    }
    return true;
  }
);

export const eventTime = createSelector(
  [_meta],
  (meta) => findMetaValue('event.time', meta) || findMetaValue('starttime', meta)
);

export const eventCategory = createSelector(
  [_meta],
  (meta) => findMetaValue('category', meta)
);

export const hostName = createSelector(
  [_meta],
  (meta) => findMetaValue('alias.host', meta)
);

export const user = createSelector(
  [_meta],
  (meta) => findMetaValue('user.src', meta)
);
export const endpointMeta = createSelector(
  [_meta],
  (meta) => {
    const categoryConfig = ENDPOINT_META_CONFIG[findMetaValue('category', meta)];
    let requiredFields = {};
    if (categoryConfig) {
      const { fields } = categoryConfig;
      requiredFields = fields.map((field) => {
        let value = '';
        if (field.field === 'filename.src') {
          value = getSrcFilename(meta);
        } else if (field.field === 'param.src') {
          value = getSrcParam(meta);
        } else if (field.field) {
          value = findMetaValue(field.field, meta);
        }
        return { ...field, value };
      });
    }
    return requiredFields;
  }
);