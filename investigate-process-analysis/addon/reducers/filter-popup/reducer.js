import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import reduxActions from 'redux-actions';
import Immutable from 'seamless-immutable';

const filterPopupInitialState = Immutable.from({
  activeFilterTab: 'all'
});

const popupFilter = reduxActions.handleActions({

  [ACTION_TYPES.SET_ACTIVE_EVENT_FILTER_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeFilterTab', tabName);
  }
}, filterPopupInitialState);

export default popupFilter;