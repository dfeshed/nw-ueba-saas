import {
  queryPromiseRequest,
  serviceIdFilter,
  findAllPromiseRequest
} from 'investigate-shared/actions/api/events/utils';

const LANGUAGE_AND_ALIASES_MODEL_NAME = 'core-meta-alias';
const META_KEY_CACHE_MODEL_NAME = 'meta-key-cache';

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

/**
 * Fetch all the meta keys.
 * @return {object} RSVP Promise
 * @public
 */
const fetchMetaKeyCache = () => findAllPromiseRequest(META_KEY_CACHE_MODEL_NAME);

export {
  fetchMetaKeyCache,
  fetchLanguageAndAliases
};
