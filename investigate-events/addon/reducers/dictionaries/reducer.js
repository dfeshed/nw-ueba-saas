import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
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
  languageError: null,
  aliasesError: null
});

const dictionariesReducer = handleActions({
  [ACTION_TYPES.LANGUAGE_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ language: null, languageError: null }),
      failure: (s) => s.set('languageError', true),
      success: (s) => s.set('language', { data: action.payload.data })
    });
  },

  [ACTION_TYPES.ALIASES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ aliases: null, aliasesError: null }),
      failure: (s) => s.set('aliasesError', true),
      success: (s) => s.set('aliases', { data: action.payload.data })
    });
  }
}, _initialState);

export default dictionariesReducer;
