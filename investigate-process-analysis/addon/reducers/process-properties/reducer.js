import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  propertiesData: null
});

const processDetailsReducer = handleActions({

  [ACTION_TYPES.FETCH_PROCESS_PROPERTIES]: (state, action) => {
    const data = action.payload ? action.payload.data : [];
    return state.merge({ hostDetails: data });
  }

}, dataInitialState);

export default processDetailsReducer;