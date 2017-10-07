import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  aliases: undefined,
  aliasesCache: {},
  language: undefined,
  languageCache: {},
  languageError: false,
  aliasesError: false
});

export default handleActions({
  // Handles the results from a Promise call to fetch `language` for a given
  // service.
  [ACTION_TYPES.LANGUAGE_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ language: undefined, languageError: false }),
      failure: (s) => s.set('languageError', true),
      success: (s) => {
        const { filter: filters } = action.payload.request;
        const filter = filters.find((e) => e.field === 'endpointId');
        const { value: serviceId } = filter;
        const languageCache = s.languageCache.setIn([serviceId], action.payload.data);
        return s.merge({
          language: action.payload.data,
          languageCache
        });
      }
    });
  },

  // Gets a language object from cache for a given service
  [ACTION_TYPES.LANGUAGE_GET_FROM_CACHE]: (state, action) => {
    const { payload: serviceId } = action;
    const language = state.languageCache[serviceId];
    return state.merge({ language });
  },

  // Handles the results from a Promise call to fetch `aliases` for a given
  // service.
  [ACTION_TYPES.ALIASES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ aliases: undefined, aliasesError: false }),
      failure: (s) => s.set('aliasesError', true),
      success: (s) => {
        const { filter: filters } = action.payload.request;
        const filter = filters.find((e) => e.field === 'endpointId');
        const { value: serviceId } = filter;
        const aliasesCache = s.aliasesCache.setIn([serviceId], action.payload.data);
        return s.merge({
          aliases: action.payload.data,
          aliasesCache
        });
      }
    });
  },

  // Gets an aliases object from cache for a given service
  [ACTION_TYPES.ALIASES_GET_FROM_CACHE]: (state, action) => {
    const { payload: serviceId } = action;
    const aliases = state.aliasesCache[serviceId];
    return state.merge({ aliases });
  },

  // Helper function for testing
  [ACTION_TYPES.SET_ALIASES]: (state, { payload }) => {
    return state.merge(payload);
  },

  // Helper function for testing
  [ACTION_TYPES.SET_LANGUAGE]: (state, { payload }) => {
    return state.merge(payload);
  }
}, _initialState);
