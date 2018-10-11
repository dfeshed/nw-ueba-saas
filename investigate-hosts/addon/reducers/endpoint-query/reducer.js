import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';

const initialState = {
  serverId: null
};

export default reduxActions.handleActions({

  [ACTION_TYPES.ENDPOINT_SERVER_SELECTED]: (state, { payload }) => {
    return state.set('serverId', payload);
  },

  [ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE]: (state) => state.set('serverId', null)

}, Immutable.from(initialState));