import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  // RIAC is disabled by default until we get the actual backend value
  isRiacEnabled: undefined,
  adminRoles: ['Administrators', 'Respond_Administrator', 'SOC_Managers']
};

const riacReducers = reduxActions.handleActions({
  [ACTION_TYPES.GET_RIAC_SETTINGS]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s }),
      success: (s) => ({
        ...s,
        isRiacEnabled: action.payload.data.enabled,
        adminRoles: action.payload.data.adminRoles
      })
    })
  )

}, initialState);

export default riacReducers;
