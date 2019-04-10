import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  selectedIndicatorId: null,
  events: [],
  historicalData: null,
  totalEvents: null,
  eventFilter: {
    page: 1,
    size: 100,
    sort_direction: 'DESC'
  }
});

const indicators = handleActions({
  [ACTION_TYPES.RESET_INDICATOR]: () => Immutable.from(initialState),
  [ACTION_TYPES.INITIATE_ALERT]: () => Immutable.from(initialState),
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
  [ACTION_TYPES.GET_INDICATOR_HISTORICAL_DATA]: (state, { payload }) => state.set('historicalData', payload)
}, initialState);

export default indicators;