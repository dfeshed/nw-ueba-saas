import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  modelSummary: null
});

const hover = handleActions({
  [ACTION_TYPES.GET_SUMMARY_DATA]: (state, { payload }) => state.set('modelSummary', payload)
}, initialState);

export default hover;
