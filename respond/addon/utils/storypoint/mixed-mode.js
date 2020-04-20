import _ from 'lodash';
import { lookup } from 'ember-dependency-lookup';
import { coreServiceNotUpdated } from 'component-lib/utils/core-services';

const coreServicesKey = 'respond.mixedModeMessage';

export const inMixedMode = (services, events, minVersion) => {
  const sourceIds = extractSource(events);
  const servicesInMixedMode = _.filter(services, ({ version, host, port }) => {
    if (coreServiceNotUpdated(version, minVersion) && host && port) {
      return sourceIds.includes(`${host}:${port}`);
    }
  });

  return !_.isEmpty(servicesInMixedMode) ? localizeMessage(coreServicesKey, minVersion) : false;
};

export const extractSource = (events) => {
  // eslint-disable-next-line
  return events.map(({ event_source }) => event_source).filter((sources) => sources !== undefined && sources !== null);
};

const localizeMessage = (key, minVersion) => {
  const i18n = lookup('service:i18n');
  return i18n.t(key, { minVersion }).toString();
};