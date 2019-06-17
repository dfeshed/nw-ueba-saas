import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import reduxActions from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

const hostContextInitialState = Immutable.from({
  hostList: [],
  loading: false
});

const HostContext = reduxActions.handleActions({

  [ACTION_TYPES.SET_HOST_CONTEXT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ hostList: [], loading: true }),
      success: (s) => s.merge({ hostList: action.payload.data, loading: false })
    });
  }
}, hostContextInitialState);

export default HostContext;
