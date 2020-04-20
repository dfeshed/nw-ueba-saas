import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-shared/actions/types';

const investigateState = Immutable.from({
  serviceId: '-1',
  timeRange: {
    unit: 'days',
    value: 7
  }
});

const investigateReducer = handleActions({

  [ACTION_TYPES.SET_INVESTIGATE_PREFERENCE]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        return s.set('serviceId', '-1');
      },
      success: (s) => {
        const { payload: { data } } = action;
        return s.set('serviceId', data);
      }
    });
  }
}, investigateState);

export default investigateReducer;
