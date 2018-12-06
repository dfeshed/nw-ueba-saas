import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  hostDetails: null,
  downloadId: null,
  exportJSONStatus: 'completed',
  arrangeSecurityConfigsBy: 'alphabetical',
  policyDetails: null,
  activeAlertTab: 'critical'
});

/**
 * Updating the agent status.
 * @param state
 * @param payload
 * @private
 */
const _updateAgentStatus = (state, { payload }) => {
  const { hostDetails } = state;
  if (hostDetails) {
    const agentStatus = payload ? payload.data.find((d) => d.agentId === hostDetails.id) : hostDetails.agentStatus;
    return state.merge({
      hostDetails: {
        ...hostDetails,
        agentStatus
      }
    });
  }
  return state;
};

const hostDetails = reduxActions.handleActions({

  [ACTION_TYPES.RESET_INPUT_DATA]: (s) => s.merge(initialState),

  [ACTION_TYPES.FETCH_HOST_DETAILS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('hostDetails', action.payload.data)
    });
  },

  [ACTION_TYPES.FETCH_POLICY_DETAILS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('policyDetails', action.payload.data)
    });
  },

  [ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('exportJSONStatus', 'streaming'),
      success: (s) => s.merge({ exportJSONStatus: 'completed', downloadId: action.payload.data.id })
    });
  },

  [ACTION_TYPES.ARRANGE_SECURITY_CONFIGURATIONS]: (state, { payload }) => state.set('arrangeSecurityConfigsBy', payload.arrangeBy),

  [ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE]: (state) => state.set('downloadId', null),

  [ACTION_TYPES.CHANGE_ALERT_TAB]: (state, { payload: { tabName } }) => state.set('activeAlertTab', tabName),

  [ACTION_TYPES.FETCH_AGENT_STATUS]: (state, action) => _updateAgentStatus(state, action)

}, initialState);

export default hostDetails;

