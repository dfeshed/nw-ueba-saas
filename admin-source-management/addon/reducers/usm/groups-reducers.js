import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'admin-source-management/actions/types/groups-types';

const initialState = {
  groups: [],
  groupsStatus: null // wait, complete, error
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_GROUPS]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          groups: [],
          groupsStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('groupsStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          groups: action.payload.data,
          groupsStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));
