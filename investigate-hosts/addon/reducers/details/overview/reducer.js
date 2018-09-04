import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  hostDetails: null,
  downloadId: null,
  exportJSONStatus: 'completed',
  arrangeSecurityConfigsBy: 'alphabetical',
  policyDetails: null
});

const hostDetails = reduxActions.handleActions({

  [ACTION_TYPES.RESET_INPUT_DATA]: (s) => s.merge(initialState),

  [ACTION_TYPES.FETCH_HOST_DETAILS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.merge({ hostDetails: action.payload.data, loadingStatus: 'completed' })
    });
  },

  [ACTION_TYPES.FETCH_POLICY_DETAILS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.merge({ policyDetails: action.payload.data, loadingStatus: 'completed' })
    });
  },

  [ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('exportJSONStatus', 'streaming'),
      success: (s) => s.merge({ exportJSONStatus: 'completed', downloadId: action.payload.data.id })
    });
  },

  [ACTION_TYPES.ARRANGE_SECURITY_CONFIGURATIONS]: (state, { payload }) => state.set('arrangeSecurityConfigsBy', payload.arrangeBy),

  [ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE]: (state) => state.set('downloadId', null)

}, initialState);

export default hostDetails;

