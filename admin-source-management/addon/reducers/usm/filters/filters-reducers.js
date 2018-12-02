import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
// import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'admin-source-management/actions/types';

export const initialState = {
  selectedFilter: null,
  expressionList: []
};

export default reduxActions.handleActions({

  [ACTION_TYPES.APPLY_FILTER]: (state, { payload }) => state.set('expressionList', payload),

  [ACTION_TYPES.RESET_FILTER]: (state) => state.merge({ 'selectedFilter': { id: 1, criteria: { expressionList: [] } } }) // RESET To All

}, Immutable.from(initialState));
