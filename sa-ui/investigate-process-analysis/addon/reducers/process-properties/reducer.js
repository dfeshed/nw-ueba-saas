import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

const dataInitialState = Immutable.from({
  propertiesData: null
});

const processDetailsReducer = handleActions({

  [ACTION_TYPES.FETCH_PROCESS_PROPERTIES]: (state, action) => {
    return handle(state, action, {
      success: (state) => {
        const data = action.payload ? action.payload.data : [];
        return state.merge({ hostDetails: data });
      }
    });
  }

}, dataInitialState);

export default processDetailsReducer;