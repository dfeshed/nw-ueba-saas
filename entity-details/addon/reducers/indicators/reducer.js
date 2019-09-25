import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  selectedIndicatorId: null,
  events: [],
  historicalData: null,
  globalBaselineData: null,
  totalEvents: null,
  indicatorGraphError: false,
  indicatorEventError: false,
  eventFilter: {
    page: 1,
    size: 100,
    sort_direction: 'DESC'
  }
});

const indicators = handleActions({
  [ACTION_TYPES.RESET_INDICATOR]: () => Immutable.from(initialState),
  [ACTION_TYPES.SELECT_ALERT]: () => Immutable.from(initialState),
  [ACTION_TYPES.INDICATOR_EVENTS_ERROR]: (state) => state.set('indicatorEventError', true),
  [ACTION_TYPES.INDICATOR_GRAPH_ERROR]: (state) => state.set('indicatorGraphError', true),
  [ACTION_TYPES.INITIATE_INDICATOR]: (state, { payload }) => state.set('selectedIndicatorId', payload),
  [ACTION_TYPES.GET_INDICATOR_EVENTS]: (state, { payload: { data, total } }) => {
    // Concat events list data to current events list.
    let newState = state.set('events', state.getIn(['events']).concat(data));
    // Total events count for display and stop scrolling event.
    newState = newState.set('totalEvents', total);
    // Increment current page by one to fetch next page on scroll again.
    newState = newState.setIn(['eventFilter', 'page'], newState.getIn(['eventFilter', 'page']) + 1);
    return newState;
  },
  [ACTION_TYPES.GET_INDICATOR_HISTORICAL_DATA]: (state, { payload: { data, globalData } }) => {
    let newState = state.set('historicalData', data);
    newState = newState.set('globalBaselineData', globalData);
    return newState;
  }
}, initialState);

export default indicators;