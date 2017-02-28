import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  // id of the incident that owns `info` & `storyline`
  id: null,

  // incident details
  info: null,

  // either 'wait', 'error' or 'completed'
  infoStatus: null,

  // incident storyline information
  storyline: null,

  // either 'wait', 'error' or 'completed'
  storylineStatus: null
};

const incident = reduxActions.handleActions({

  [ACTION_TYPES.INITIALIZE_INCIDENT]: (state, { payload }) => {
    // payload is the new incident id
    if (payload === state.id) {

      // incident id is unchanged, so no need to change state
      return state;
    } else {

      // reset state for a new incident id
      return {
        ...initialState,
        id: payload
      };
    }
  },

  [ACTION_TYPES.FETCH_INCIDENT_DETAILS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, info: null, infoStatus: 'wait' }),
      failure: (s) => ({ ...s, infoStatus: 'error' }),
      success: (s) => ({ ...s, info: action.payload.data, infoStatus: 'completed' })
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, storyline: null, storylineStatus: 'wait' }),
      failure: (s) => ({ ...s, storylineStatus: 'error' }),
      success: (s) => ({ ...s, storyline: action.payload.data, storylineStatus: 'completed' })
    });
  }

}, initialState);

export default incident;