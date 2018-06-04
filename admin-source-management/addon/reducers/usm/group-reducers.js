import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

export const initialState = {
  // the group object to be created/updated/saved
  group: {
    id: null,
    name: null,
    description: null,
    createdBy: null,
    createdOn: null,
    lastModifiedBy: null,
    lastModifiedOn: null,
    osTypes: [], // ID's only
    osDescriptions: [], // ID's only
    ipRangeStart: null,
    ipRangeEnd: null,
    policy: null // map of { 'type': 'policyID' }  ( ex. { 'edrPolicy': 'id_abc123' } )
  },

  // the osType objects & osDescriptions objects to fill the selects/dropdowns
  osTypes: [
    {
      id: 'lynn_001',
      name: 'Lynn Ucks',
      osDescriptions: [
        { id: 'ucks_001', name: 'Ucks Desktop' },
        { id: 'ucks_002', name: 'Ucks Mobile' },
        { id: 'ucks_003', name: 'Ucks Server' }
      ]
    },
    {
      id: 'apple_001',
      name: 'Apple Tosh',
      osDescriptions: [
        { id: 'tosh_001', name: 'Tosh Desktop' },
        { id: 'tosh_002', name: 'Tosh Mobile' },
        { id: 'tosh_003', name: 'Tosh Server' }
      ]
    },
    {
      id: 'win_001',
      name: 'Win D0ze',
      osDescriptions: [
        { id: 'doze_001', name: 'Doze Desktop' },
        { id: 'doze_002', name: 'Doze Mobile' },
        { id: 'doze_003', name: 'Doze Server' }
      ]
    }
  ],

  // the policies objects to fill the policy select/dropdown
  policies: [],

  initGroupFetchPoliciesStatus: null, // wait, complete, error
  groupSaveStatus: null // wait, complete, error
};

export default reduxActions.handleActions({

  [ACTION_TYPES.INIT_GROUP_FETCH_POLICIES]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('initGroupFetchPoliciesStatus', 'wait');
      },
      failure: (state) => {
        return state.set('initGroupFetchPoliciesStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          policies: action.payload.data,
          initGroupFetchPoliciesStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.NEW_GROUP]: (state /* , action */) => {
    return state.merge({
      group: { ...initialState.group },
      groupSaveStatus: null,
      initGroupFetchPoliciesStatus: null
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
