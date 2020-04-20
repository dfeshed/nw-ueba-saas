import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from '../../actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { lookup } from 'ember-dependency-lookup';
import { success, failure } from 'respond-shared/utils/flash-messages';

const initialState = {
  priorityTypes: [],
  enabledUsers: [],
  categoryTags: [],
  // either 'wait', 'error' or 'completed'
  enabledUsersStatus: null
};
/**
 * The user object returned from the service has a property named 'disabled', which can cause issues with UI
 * components (e.g., ember power select), which interprets that property in unanticipated ways. The ember power select
 * component, for example, disables the user entry in the dropdown list because of the presence of this property, even
 * when that behavior is not desired.
 *
 * This function remaps the 'disabled' property onto a new property 'isInactive', and deletes the original property to
 * avoid this name collision.
 * @private
 * @param users
 */
const remapDisabledProperty = (users) => {
  return users.map((user) => {
    const isDisabled = !!user.disabled;
    const newUser = { ...user, isInactive: isDisabled };
    delete newUser.disabled;
    return newUser;
  });
};

export default reduxActions.handleActions({

  [ACTION_TYPES.FETCH_CATEGORY_TAGS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('categoryTags', []),
      failure: (s) => s.set('categoryTags', []),
      success: (s) => s.set('categoryTags', action.payload.data)
    });
  },


  [ACTION_TYPES.FETCH_PRIORITY_TYPES]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('priorityTypes', []),
      failure: (s) => s.set('priorityTypes', []),
      success: (s) => s.set('priorityTypes', action.payload.data)
    });
  },

  [ACTION_TYPES.FETCH_ALL_ENABLED_USERS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ enabledUsers: [], enabledUsersStatus: 'wait' }),
      failure: (s) => s.set('enabledUsersStatus', 'error'),
      success: (s) => s.merge({ enabledUsers: remapDisabledProperty(action.payload.data), enabledUsersStatus: 'completed' })
    });
  },

  [ACTION_TYPES.CREATE_INCIDENT_FROM_EVENTS]: (state, action) => {
    return handle(state, action, {
      failure: (s) => {
        failure('respond.incidents.actions.actionMessages.incidentCreationFromEventsFailed');
        return s;
      },
      success: (s) => {
        lookup('service:eventBus').trigger('rsa-application-modal-close-create-incident');
        success('respond.incidents.actions.actionMessages.incidentCreatedFromEvents', { incidentId: action.payload.data.id });
        return s;
      }
    });
  }

}, Immutable.from(initialState));