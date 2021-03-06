import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/endpoint';
import reduxActions from 'redux-actions';

const initialState = {
  serverId: null
};

export default reduxActions.handleActions({

  [ACTION_TYPES.ENDPOINT_SERVER_SELECTED]: (state, { payload }) => {
    return state.set('serverId', payload);
  }

}, Immutable.from(initialState));