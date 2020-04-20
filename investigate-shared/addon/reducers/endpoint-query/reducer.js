import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-shared/actions/types';
import reduxActions from 'redux-actions';

const initialState = {
  serverId: null,
  selectedMachineServerId: null // server Id we get from machine/detail response
};

export default reduxActions.handleActions({

  [ACTION_TYPES.ENDPOINT_SERVER_SELECTED]: (state, { payload }) => {
    return state.set('serverId', payload);
  },

  [ACTION_TYPES.SET_SELECTED_MACHINE_SERVER_ID]: (state, { payload }) => state.set('selectedMachineServerId', payload)


}, Immutable.from(initialState));