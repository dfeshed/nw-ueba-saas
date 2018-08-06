import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

const initialState = {
  items: [],
  itemsStatus: null // wait, complete, error
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_POLICY_LIST]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          items: [],
          itemsStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('itemsStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          items: action.payload.data,
          itemsStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));

