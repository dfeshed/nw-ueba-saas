import moment from 'moment';
import { windowProxy } from 'component-lib/utils/window-proxy';

const processOrLinkFields = ['process_name_link', 'machine_name_link', 'dst_process_link', 'src_process_link'];

const SCHEMA_FILTER = {
  active_directory: "(reference.id = '4741','4742','4733','4734','4740','4794','5376','5377','5136','4764','4743','4739','4727','4728','4754','4756','4757','4758','4720','4722','4723','4724','4725','4726','4738','4767','4717','4729','4730','4731','4732')",
  authentication: "((reference.id = '4624','4625','4769','4648') || (device.type = 'rsaacesrv' && ec.activity = 'Logon') || ((action = '/usr/sbin/sshd' || action='/usr/bin/login') && device.type = 'rhlinux'))",
  file: "(reference.id = '4663','4660','4670','5145')",
  process: "(category='Process Event' AND device.type='nwendpoint')",
  registry: "(category='Registry Event' AND device.type='nwendpoint')"
};

const entityFilter = (entityType, entityValue) => {
  const ENTITY_QUERY = {
    IP: '',
    User: `username='${entityValue}'||user.dst='${entityValue}'||user.src='${entityValue}'`
  };
  return ENTITY_QUERY[entityType];
};

/**
 * Returns the time-range converting relative time ex: 2days.
 * @param value
 * @param unit
 * @returns {{startTime: *, endTime: *}}
 * @public
 */
const buildTimeRange = (eventTime) => {
  return {
    endTime: moment(eventTime * 1000).endOf('minute').format('X'),
    startTime: moment(eventTime * 1000).subtract(1, 'hours').add(1, 'minutes').startOf('minute').format('X')
  };
};
/**
 * Creates a serialized representation of an array suitable for use in a URL query string.
 * @public
 */
const serializeQueryParams = (qp = {}) => {
  return Object.entries(qp)
    .filter((d) => d[1] !== undefined) // filter out undefined values of properties
    .map((d) => `${d[0]}=${d[1]}`).join('&');
};

/**
 * Opens the investigate page with events query
 * @private
 */
const navigateToInvestigateEventsAnalysis = (entityType, entityValue, indicatorSchema, eventTime, serviceId, additionalFilter) => {
  const { startTime, endTime } = buildTimeRange(eventTime);
  const queryParams = {
    sid: serviceId, // Service Id
    mf: encodeURIComponent(`${SCHEMA_FILTER[indicatorSchema]} && (${entityFilter(entityType, entityValue)})${additionalFilter}`), // Meta filter
    st: startTime, // Stat time
    et: endTime, // End time
    mps: 'default', // Meta panel size
    rs: 'max' // Recon size
  };
  const query = serializeQueryParams(queryParams);
  const path = `${window.location.origin}/investigate/events?${query}`;
  windowProxy.openInNewTab(path);
};

export const navigateToInvestigate = (entityType, entityValue, indicatorSchema, eventTime, item, { linkField, field, additionalFilter }, brokerId) => {
  if (processOrLinkFields.includes(linkField)) {
    windowProxy.openInNewTab(item[linkField]);
  } else {
    const extraQueryParam = additionalFilter ? ` && ${additionalFilter} = ${item[field]}` : '';
    const serviceId = brokerId || item[linkField].match(/investigation\/(.*)\/events/i)[1];
    navigateToInvestigateEventsAnalysis(entityType, entityValue, indicatorSchema, eventTime, serviceId, extraQueryParam);
  }
};