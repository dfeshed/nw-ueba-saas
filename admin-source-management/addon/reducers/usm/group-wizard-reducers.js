import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

export const initialState = {
  // the group object to be created/updated/saved
  group: {
    id: null,
    name: '',
    description: '',
    createdBy: null,
    createdOn: null,
    lastModifiedBy: null,
    lastModifiedOn: null,
    group: null // map of { 'type': 'groupID' }  ( ex. { 'edrGroup': 'id_abc123' } )
  },

  // the policies objects to fill the group select/dropdown
  policies: [],

  initGroupFetchPoliciesStatus: null, // wait, complete, error
  groupStatus: null, // wait, complete, error

// TODO if the reducer doesn't need to modify these, and the selectors aren't doing anything special,
  //   then we may want to extract these to a steps.js and add them directly to the group-wizard component,
  //   but keep in mind that we may want to dynamically add steps...
  steps: [
    {
      id: 'identifyGroupStep',
      nextStepId: 'defineGroupStep',
      prevStepId: '',
      title: 'adminUsm.groupWizard.identifyGroup',
      stepComponent: 'usm-groups/group-wizard/identify-group-step',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    },
    {
      id: 'defineGroupStep',
      prevStepId: 'identifyGroupStep',
      nextStepId: 'applyPolicyStep',
      title: 'adminUsm.groupWizard.defineGroup',
      stepComponent: 'usm-groups/group-wizard/define-group-step',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    },
    {
      id: 'applyPolicyStep',
      prevStepId: 'defineGroupStep',
      nextStepId: 'reviewGroupStep',
      title: 'adminUsm.groupWizard.applyPolicy',
      stepComponent: 'usm-groups/group-wizard/apply-policy-step',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    },
    {
      id: 'reviewGroupStep',
      prevStepId: 'applyPolicyStep',
      nextStepId: '',
      title: 'adminUsm.groupWizard.reviewGroup',
      stepComponent: 'usm-groups/group-wizard/review-group-step',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    }
  ],
  // keeps track of the form fields visited by the user
  visited: []
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
    return state.setIn(fields, value).set('visited', [...state.visited, field]);
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
