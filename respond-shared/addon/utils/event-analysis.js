import _ from 'lodash';
import moment from 'moment';
import { EVENT_TYPES } from 'component-lib/constants/event-types';

const lookupCoreDevice = (services, eventSource) => {
  if (!eventSource) {
    return null;
  }
  const pattern = new RegExp('(.*):(.*)');
  const patternMatch = pattern.exec(eventSource);
  if (!patternMatch) {
    return null;
  }
  const [ , eventHost, eventPort ] = patternMatch;
  const lookup = _.filter(services, (service) => {
    const { host, port } = service;
    const hostFound = host && host == eventHost;
    const portFound = port && port == eventPort;
    const portFallbackFound = port && port - 6000 == eventPort;
    return hostFound && (portFound || portFallbackFound);
  });
  return lookup && lookup.length === 1 && lookup[0].id;
};

const createProcessAnalysisLink = (item, services) => {

  // fetch service id of core device based on event source
  const sid = lookupCoreDevice(services, item.event_source);

  let processAnalysisLink;

  const { startTime, endTime } = defaultQueryTimeRange();

  if (item.device_type === 'nwendpoint' && item.agent_id && item.source.hash && item.source.filename && item.process_vid && item.operating_system && item.operating_system !== 'linux' && sid) {
    const processAnalysisQueryString = [
      `checksum=${item.source.hash}`,
      `sid=${sid}`,
      `aid=${item.agent_id}`,
      `pn=${item.source.filename}`,
      `osType=${item.operating_system}`,
      `vid=${item.process_vid}`,
      `st=${startTime}`,
      `et=${endTime}`
    ].join('&');

    processAnalysisLink = `/investigate/process-analysis?${processAnalysisQueryString}`;
  }

  return processAnalysisLink;
};

/**
  * Creates event analysis link from event item and list of services
  *
  * @param item event
  * @param services list of services
  * @returns {string}
  * @public
  */
const createEventAnalysisLink = (item, services) => {
  // fetch service id of core device based on event source
  const sid = lookupCoreDevice(services, item.event_source);

  const eid = item.event_source_id;

  let eventAnalysisLink;

  if (eid) {
    const eventAnalysisQueryString = [
      `eventId=${eid}`,
      `endpointId=${sid}`
    ].join('&');

    eventAnalysisLink = `/investigate/recon?${eventAnalysisQueryString}`;
  }

  return eventAnalysisLink;
};

const getEventType = (type, deviceType) => {
  if (deviceType === 'nwendpoint') {
    return EVENT_TYPES.ENDPOINT;
  } else if (type === 'Network') {
    return EVENT_TYPES.NETWORK;
  } else {
    return EVENT_TYPES.LOG;
  }
};

const defaultQueryTimeRange = () => {
  // default time range is of past 7 days
  const now = moment();
  const endTime = now.unix();
  const startTime = now.subtract(7, 'days').unix();
  return {
    startTime,
    endTime
  };
};

export {
  lookupCoreDevice,
  createProcessAnalysisLink,
  createEventAnalysisLink,
  getEventType
};
