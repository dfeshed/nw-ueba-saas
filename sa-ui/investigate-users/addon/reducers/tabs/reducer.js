import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  activeTabName: 'overview'
});

const tabs = handleActions({
  [ACTION_TYPES.RESTORE_DEFAULT]: () => (Immutable.from(initialState)),
  [ACTION_TYPES.UPDATE_ACTIVE_TAB]: (state, { payload }) => state.set('activeTabName', payload)
}, initialState);

export default tabs;
