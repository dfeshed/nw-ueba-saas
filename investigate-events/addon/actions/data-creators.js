import * as ACTION_TYPES from './types';
import { fetchServices } from './fetch/services';
import { fetchAliases, fetchLanguage } from './fetch/dictionaries';

/**
 * Initializes the dictionaries (language and aliases). If we've already
 * retrieved the dictionaries for a specific service, we reuse that data.
 * @public
 */
export const initializeDictionaries = () => {
  return (dispatch, getState) => {
    const { serviceId } = getState().investigate.queryNode;
    const { aliasesCache, languageCache } = getState().investigate.dictionaries;

    if (!languageCache[serviceId]) {
      dispatch({
        type: ACTION_TYPES.LANGUAGE_RETRIEVE,
        promise: fetchLanguage(serviceId),
        meta: {
          onFailure(response) {
            window.console.warn('Could not retrieve language', response);
          }
        }
      });
    } else {
      dispatch({ type: ACTION_TYPES.LANGUAGE_GET_FROM_CACHE, payload: serviceId });
    }

    if (!aliasesCache[serviceId]) {
      dispatch({
        type: ACTION_TYPES.ALIASES_RETRIEVE,
        promise: fetchAliases(serviceId),
        meta: {
          onFailure(response) {
            window.console.warn('Could not retrieve aliases', response);
          }
        }
      });
    } else {
      dispatch({ type: ACTION_TYPES.ALIASES_GET_FROM_CACHE, payload: serviceId });
    }
  };
};

/**
 * Initializes the list of services (aka endpoints). This list shouldn't really
 * change much, so we only retrieve it once.
 * @public
 */
export const initializeServices = () => {
  return (dispatch, getState) => {
    const { services } = getState().investigate;
    if (!services.data) {
      dispatch({
        type: ACTION_TYPES.SERVICES_RETRIEVE,
        promise: fetchServices(),
        meta: {
          onFailure(response) {
            window.console.error('Failed to retrieve Services.', response);
          }
        }
      });
    }
  };
};
