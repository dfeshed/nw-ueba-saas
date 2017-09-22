import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _dataInitialState = Immutable.from({
  serviceId: null
});

const data = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
    const { data: { serviceId } } = payload;
    return _dataInitialState.set('serviceId', serviceId);
  },

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    return state.set('serviceId', payload);
  }
}, _dataInitialState);

export default data;
