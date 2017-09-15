import * as ACTION_TYPES from './types';
import { fetchServices } from './fetch/services';
import { fetchAliases, fetchLanguage } from './fetch/dictionaries';

export const initializeDictionaries = () => {
  return (dispatch, getState) => {
    const { data, dictionaries } = getState();
    if (!dictionaries.language) {
      dispatch({
        type: ACTION_TYPES.LANGUAGE_RETRIEVE,
        promise: fetchLanguage(data.endpointId),
        meta: {
          onFailure(response) {
            window.console.warn('Could not retrieve language', response);
          }
        }
      });
    }

    if (!dictionaries.aliases) {
      dispatch({
        type: ACTION_TYPES.ALIASES_RETRIEVE,
        promise: fetchAliases(data.endpointId),
        meta: {
          onFailure(response) {
            window.console.warn('Could not retrieve aliases', response);
          }
        }
      });
    }
  };
};

export const initializeServices = () => {
  return (dispatch, getState) => {
    const { services } = getState();
    if (!services.services) {
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
