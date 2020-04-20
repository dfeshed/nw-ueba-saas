import { queryPromiseRequest, serviceIdFilter } from '../util/query-util';

const LANGUAGE_AND_ALIASES_MODEL_NAME = 'core-meta-alias';

/**
 * fetch `language` and `aliases` for a service
 * @return {object} RSVP Promise
 * @param {*} serviceId id of service
 */
const fetchLanguageAndAliases = (serviceId) => {
  const query = {
    filter: [
      serviceIdFilter(serviceId)
    ]
  };
  return queryPromiseRequest(
    LANGUAGE_AND_ALIASES_MODEL_NAME,
    query
  );
};

export {
  fetchLanguageAndAliases
};
