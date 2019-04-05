import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';
import { coreServiceNotUpdated } from 'component-lib/utils/core-services';

const coreServicesKey = 'investigate.services.coreServiceNotUpdated';

export const inMixedMode = (services, events, minVersion) => {
  const sourceIds = extractSource(events);
  const servicesInMixedMode = _.filter(services, ({ version, host, port }) => {
    if (coreServiceNotUpdated(version, minVersion) && host && port) {
      return sourceIds.includes(`${host}:${port}`);
    }
  });

  const version = extractVersion(servicesInMixedMode);
  return version ? localizeMessage(coreServicesKey, version, minVersion) : false;
};

export const extractSource = (events) => {
  // eslint-disable-next-line
  return events.map(({ event_source }) => event_source).filter((sources) => sources !== undefined && sources !== null);
};

export const extractVersion = (services) => {
  const versions = services.map(({ version }) => version).filter((versions) => versions !== undefined && versions !== null);
  const [ first ] = versions;
  return first;
};

const localizeMessage = (key, version, minVersion) => {
  const i18n = lookup('service:i18n');
  return i18n.t(key, { version, minVersion }).toString();
};
