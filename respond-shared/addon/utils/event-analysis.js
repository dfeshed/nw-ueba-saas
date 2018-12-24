import _ from 'lodash';
import moment from 'moment';

export const lookupCoreDevice = (services, eventSource) => {
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

export const createProcessAnalysisLink = (item, services) => {

  // fetch service id of core device based on event source
  const sid = lookupCoreDevice(services, item.event_source);

  let processAnalysisLink;

  // time range is of past 7 days
  const now = moment();
  const endTime = now.unix();
  const startTime = now.subtract(7, 'days').unix();

  if (item.device_type === 'nwendpoint' && item.agent_id && item.source.hash && item.source.filename && item.process_vid && sid) {
    processAnalysisLink = `/investigate/process-analysis?checksum=${item.source.hash}&sid=${sid}&aid=${item.agent_id}&pn=${item.source.filename}&st=${startTime}&et=${endTime}&vid=${item.process_vid}`;
  }

  return processAnalysisLink;
};