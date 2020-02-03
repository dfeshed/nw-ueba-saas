import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import sort from 'fast-sort';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import { getAliases, getLanguage } from './utils';

const _initialState = Immutable.from({
  aliases: undefined,
  aliasesCache: {},
  language: undefined,
  languageCache: {},
  metaKeyCache: undefined,
  languageAndAliasesError: false
});

const ENDPOINTID = 'endpointId';

export default handleActions({

  // handles results from a Promise call that
  // fetches `language` and `aliases` for a given service
  [ACTION_TYPES.LANGUAGE_AND_ALIASES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({
        language: undefined,
        languageAndAliasesError: false,
        aliases: undefined
      }),
      failure: (s) => s.merge({
        languageAndAliasesError: true
      }),
      success: (s) => {
        const { filter: filters } = action.payload.request;
        const filter = filters.find((e) => e.field === ENDPOINTID);
        const { value: serviceId } = filter;
        const aliases = getAliases(action.payload.data);
        const aliasesCache = s.aliasesCache.setIn([serviceId], aliases);
        const language = getLanguage(action.payload.data);
        const languageCache = s.languageCache.setIn([serviceId], language);

        return s.merge({
          aliases,
          aliasesCache,
          language,
          languageCache
        });
      }
    });
  },

  // gets `language` and `aliases` from cache for a given service
  [ACTION_TYPES.LANGUAGE_AND_ALIASES_GET_FROM_CACHE]: (state, action) => {
    const { payload: serviceId } = action;
    const language = state.languageCache[serviceId];
    const aliases = state.aliasesCache[serviceId];
    return state.merge({ aliases, language });
  },

  [ACTION_TYPES.META_KEY_CACHE_RETRIEVE]: (state, action) => {
    const metaKeys = action.payload ?
      sort(action.payload.data).by([{ asc: (meta) => meta.metaName.toUpperCase() }]) : [];

    return handle(state, action, {
      // TODO failure
      failure: (s) => s.set('metaKeyCache', []),
      success: (s) => {
        if (metaKeys) {
          // metaKeys retrieved
          return s.set('metaKeyCache', metaKeys);
        } else {
          // TODO if no metaKeys returned
          return s.set('metaKeyCache', []);
        }
      }
    });
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
