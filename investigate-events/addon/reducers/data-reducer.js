import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _dataInitialState = Immutable.from({
  endpointId: null
});

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
    const { data: { endpointId } } = payload;
    return _dataInitialState.set('endpointId', endpointId);
  },

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    return state.set('endpointId', payload);
  }
}, _dataInitialState);

export default data;
