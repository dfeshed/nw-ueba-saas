import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import { contextDataParser } from 'context/helpers/context-data-parser';
import Immutable from 'seamless-immutable';

const resetState = Immutable.from({
  meta: null,
  lookupKey: null,
  errorMessage: null,
  lookupData: [{}],
  entitiesMetas: null,
  isClicked: false
});

const initialState = Immutable.from(resetState);

const context = handleActions({
  [ACTION_TYPES.RESTORE_DEFAULT]: () => (Immutable.from(resetState)),
  [ACTION_TYPES.INITIALIZE_CONTEXT_PANEL]: (state, { payload }) => {
    return state.merge({ ...resetState, lookupKey: payload.lookupKey, meta: payload.meta });
  },
  [ACTION_TYPES.GET_CONTEXT_ENTITIES_METAS]: (state, { payload }) => state.set('entitiesMetas', payload),
  [ACTION_TYPES.CONTEXT_ERROR]: (state, { payload }) => state.set('errorMessage', payload),
  [ACTION_TYPES.UPDATE_PANEL_CLICKED]: (state, { payload }) => state.set('isClicked', payload),
  [ACTION_TYPES.GET_LOOKUP_DATA]: (state, { payload }) => {
    const lookupData = [].concat(contextDataParser([payload, state.lookupData]));
    return state.merge({ lookupData });
  }
}, initialState);

export default context;
