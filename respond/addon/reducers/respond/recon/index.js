import _ from 'lodash';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'respond/actions/types';
import { getAliases, getLanguage } from './utils';

const initialState = Immutable.from({
  serviceData: undefined,
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined,
  aliases: {},
  language: {},
  aliasesCache: {},
  languageCache: {},
  loadingRecon: false
});

export default handleActions({
  [ACTION_TYPES.SERVICES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('isServicesLoading', true),
      failure: (s) => s.merge({ isServicesRetrieveError: true, isServicesLoading: false }),
      success: (s) => {
        const { data } = action.payload;
        const validServices = data && data.filter((service) => service !== null);
        const serviceData = _.keyBy(validServices, (service) => service.id);
        return s.merge({
          serviceData,
          isServicesLoading: false,
          isServicesRetrieveError: false
        });
      }
    });
  },

  [ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE]: (state, action) => {
    return state.set('loadingRecon', action.payload.loading);
  },

  [ACTION_TYPES.GET_FROM_LANGUAGE_AND_ALIASES_CACHE]: (state, action) => {
    const { payload: { endpointId } } = action;
    const aliases = state.aliasesCache[endpointId];
    const language = state.languageCache[endpointId];
    return state.merge({
      language,
      aliases
    });
  },

  [ACTION_TYPES.ALIASES_AND_LANGUAGE_COMPLETE]: (state, action) => {
    const { response: { data }, endpointId } = action.payload;
    const aliases = getAliases(data);
    const language = getLanguage(data);
    const aliasesCache = state.aliasesCache.setIn([endpointId], getAliases(data));
    const languageCache = state.languageCache.setIn([endpointId], language);
    return state.merge({
      language,
      aliases,
      aliasesCache,
      languageCache
    });
  }
}, initialState);
