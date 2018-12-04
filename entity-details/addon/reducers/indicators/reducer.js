import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  indicatorId: null,
  events: null,
  historicalData: null,
  eventFilter: {
    page: 1,
    size: 100,
    sort_direction: 'DESC'
  }
});

const indicators = handleActions({
  [ACTION_TYPES.RESET_INDICATOR]: () => Immutable.from(initialState),
  [ACTION_TYPES.INITIATE_INDICATOR]: (state, { payload: { indicatorId } }) => state.merge({ indicatorId }),
  [ACTION_TYPES.GET_INDICATOR_EVENTS]: (state, { payload }) => state.merge({ events: payload }),
  [ACTION_TYPES.GET_INDICATOR_HISTORICAL_DATA]: (state, { payload }) => state.merge({ historicalData: payload })
}, initialState);

export default indicators;