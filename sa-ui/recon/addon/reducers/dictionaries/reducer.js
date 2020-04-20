import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'recon/actions/types';
import { getAliases, getLanguage } from './utils';

const dictionariesInitialState = Immutable.from({
  // NOTE: actual aliases/language data is stored in
  // `.data` inside the following props. This is because
  // when these are provided by investigate the data
  // property isn't populated as the promise (in investigate)
  // hasn't resolved. If `data` is pulled out of the array in
  // INITIALIZE, then it remains empty because the promise
  // that populates `data` resolves later. So in this case
  // we benefit from the data binding.
  //
  // But this will cause problems down the road (when we
  // actually use aliases/language).
  //
  // This is something to address when investigate is reduxed
  // or when we figure out how to lazy push attributes to
  // recon from investigate
  aliases: null,
  language: null,
  languageAndAliasesError: null
});

const dictionariesReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload: { aliases, language } }) => {
    // Initialize can pass aliases and language in
    return dictionariesInitialState.merge({ aliases, language });
  },

  [ACTION_TYPES.CLOSE_RECON]: (state) => {
    // TODO language/alias errors?
    return state.merge(dictionariesInitialState);
  },

  // handles results from fetching `language` and `aliases` for given service
  [ACTION_TYPES.LANGUAGE_AND_ALIASES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({
        aliases: undefined,
        language: undefined,
        languageAndAliasesError: false
      }),
      failure: (s) => s.merge({
        languageAndAliasesError: true
      }),
      success: (s) => {
        const aliases = getAliases(action.payload.data);
        const language = getLanguage(action.payload.data);

        return s.merge({
          aliases,
          language
        });
      }
    });
  }
}, dictionariesInitialState);

export default dictionariesReducer;
