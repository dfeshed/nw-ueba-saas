import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  detailsTabSelected: 'Properties' // Possible values Properties or Events at this point.
});

const processVisualsReducer = handleActions({

  [ACTION_TYPES.SET_DETAILS_TAB]: (state, action) => state.merge({ detailsTabSelected: action.payload })

}, dataInitialState);

export default processVisualsReducer;