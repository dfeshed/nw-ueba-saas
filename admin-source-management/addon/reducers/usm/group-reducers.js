import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

const initialState = {
  group: {
    id: null,
    name: null,
    description: null,
    createdBy: null,
    createdOn: null,
    lastModifiedBy: null,
    lastModifiedOn: null
  },

  groupSaveStatus: null // wait, complete, error
};

export default reduxActions.handleActions({

  [ACTION_TYPES.NEW_GROUP]: (state /* , action */) => {
    return state.merge({
      group: { ...initialState.group },
      groupSaveStatus: null
    });
  },

  [ACTION_TYPES.EDIT_GROUP]: (state, action) => {
    const { field, value } = action.payload; // const { payload: { field, value } } = action;
    const fields = field.split('.');
    // Edit the value in the group, and keep track of the field as having been visited by the user.
    // Visited fields will show error/validation messages
    return state.setIn(fields, value); // .set('visited', [...state.visited, field]);
  },

  [ACTION_TYPES.SAVE_GROUP]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('groupSaveStatus', 'wait');
      },
      failure: (state) => {
        return state.set('groupSaveStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          group: action.payload.data,
          groupSaveStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));
