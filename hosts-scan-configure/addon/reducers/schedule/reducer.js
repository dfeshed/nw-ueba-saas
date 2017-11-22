import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'hosts-scan-configure/actions/types';

const initialState = Immutable.from({
  config: {
    name: 'default'
  },
  fetchScheduleStatus: null
});

const scheduleReducer = handleActions({
  [ACTION_TYPES.FETCH_SCHEDULE_CONFIG]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ fetchScheduleStatus: 'wait' }),
      success: (s) => {
        const { payload: { data } } = action;
        return s.merge({ config: data || { name: 'default' }, fetchScheduleStatus: 'completed' });
      }
    });
  },
  [ACTION_TYPES.UPDATE_CONFIG_PROPERTY]: (state, { payload: config }) => {
    return state.merge({ config }, { deep: true });
  }
}, initialState);

export default scheduleReducer;
