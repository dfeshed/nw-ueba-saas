import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import reduxActions from 'redux-actions';

const initialState = {
  serviceId: null,
  startTime: null,
  endTime: null,
  metaFilter: []
};

export default reduxActions.handleActions({

  [ACTION_TYPES.SET_QUERY_INPUT]: (state, { payload }) => {
    const { serviceId, startTime, endTime } = payload;
    return state.merge({ serviceId, startTime, endTime });
  }

}, Immutable.from(initialState));
