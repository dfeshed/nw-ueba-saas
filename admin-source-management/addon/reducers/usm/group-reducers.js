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
    dirty: false,
    lastModifiedBy: null,
    lastModifiedOn: null,
    assignedPolicies: {},
    osTypes: [], // ID's only
    osDescriptions: [], // ID's only
    ipRangeStart: null,
    ipRangeEnd: null
  },

  // the osType objects & osDescriptions objects to fill the selects/dropdowns
  osTypes: [
    {
      id: 'Windows',
      name: 'Windows',
      osDescriptions: [
        { id: 'Windows Vista', name: 'Windows Vista' },
        { id: 'Windows 7', name: 'Windows 7' },
        { id: 'Windows 8', name: 'Windows 8' },
        { id: 'Windows 10', name: 'Windows 10' },
        { id: 'Windows server 2008', name: 'Windows server 2008' },
        { id: 'Windows server 2008 R2', name: 'Windows server 2008 R2' },
        { id: 'Windows server 2012', name: 'Windows server 2012' }
      ]
    },
    {
      id: 'Mac',
      name: 'Mac',
      osDescriptions: [
        { id: 'Mac OS X 10.9', name: 'Mac OS X 10.9' },
        { id: 'Mac OS X 10.10', name: 'Mac OS X 10.10' },
        { id: 'Mac OS X 10.11', name: 'Mac OS X 10.11' },
        { id: 'macOS 10.12', name: 'macOS 10.12' }
      ]
    },
    {
      id: 'Linux',
      name: 'Linux',
      osDescriptions: [
        { id: 'CentOS 6.x', name: 'CentOS 6.x' },
        { id: 'CentOS 7.x', name: 'CentOS 7.x' },
        { id: 'Red Hat Enterprise 6.x', name: 'Red Hat Enterprise 6.x' },
        { id: 'Red Hat Enterprise 7.x', name: 'Red Hat Enterprise 7.x' }
      ]
    }
  ],

  // the policies objects to fill the policy select/dropdown
  policies: [],

  initGroupFetchPoliciesStatus: null, // wait, complete, error
  groupStatus: null // wait, complete, error
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
      groupStatus: null,
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
        return state.set('groupStatus', 'wait');
      },
      failure: (state) => {
        return state.set('groupStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          group: action.payload.data,
          groupStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));
