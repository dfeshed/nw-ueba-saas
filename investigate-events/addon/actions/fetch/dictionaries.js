import {
  queryPromiseRequest,
  serviceIdFilter,
  findAllPromiseRequest
} from 'investigate-shared/actions/api/events/utils';

const LANGUAGE_MODEL_NAME = 'core-meta-key';
const ALIAS_MODEL_NAME = 'core-meta-alias';
const META_KEY_CACHE_MODEL_NAME = 'meta-key-cache';

/**
 * Fetch the language for a given service.
 * @param {string|number} serviceId Id of the service
 * @return {object} RSVP Promise
 * @public
 */
const fetchLanguage = (serviceId) => {
  const query = {
    filter: [
      serviceIdFilter(serviceId)
    ]
  };
  return queryPromiseRequest(
    LANGUAGE_MODEL_NAME,
    query
  );
};

/**
 * Fetch the aliases for a given service.
 * @param {string|number} serviceId Id of the service
 * @return {object} RSVP Promise
 * @public
 */
const fetchAliases = (serviceId) => {
  const query = {
    filter: [
      serviceIdFilter(serviceId)
    ]
  };
  return queryPromiseRequest(
    ALIAS_MODEL_NAME,
    query
  );
};

/**
 * Fetch all the meta keys.
 * @return {object} RSVP Promise
 * @public
 */
const fetchMetaKeyCache = () => findAllPromiseRequest(META_KEY_CACHE_MODEL_NAME);

export {
  fetchLanguage,
  fetchAliases,
  fetchMetaKeyCache
};
