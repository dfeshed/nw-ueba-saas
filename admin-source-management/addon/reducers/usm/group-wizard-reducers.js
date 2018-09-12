import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import _ from 'lodash';
import * as ACTION_TYPES from 'admin-source-management/actions/types';


const ATTRBT = ['osType', 'osDescription', 'hostname', 'ipv4', 'ipv6', 'agentMode'];
const OPRTR = ['IN', 'EQUALS', 'CONTAINS', 'STARTS_WITH', 'EMDS_WITH', 'NOT_IN', 'BETWEEN', 'NOT_BETWEEN', 'NOT_EQUALS'];
const INPT = ['textInput', 'osSelector', '2textInputs', 'agentSelector', 'textarea'];
const VLDTR = ['none', '256max', 'validHostname', 'validHostnameList', 'validHostnameChars', 'ipv4', 'ipv4Pair', 'ipv4List', 'ipv6', 'ipv6Pair', 'ipv6List'];
// In the future the _GROUP_ATTRIBUTES_MAP may be initialize with an API call
// _GROUP_ATTRIBUTES_MAP is derived from https://wiki.na.rsa.net/display/RPA/Cycle+9+Attributes
// The _GROUP_ATTRIBUTES_MAP is used to draw dynamically the selectors for attributes and operators and also the associated input/s
// Consider __GROUP_ATTRIBUTES_MAP.map[0] = [osType, [IN, osSelector, none]], here the 'osType' attribute has one operator 'IN", has selector type input 'osSelector' and has no validation
// __GROUP_ATTRIBUTES_MAP.map[1] = [osDescription, [EQUALS, textInput, 256max], ... plus three other operators with associated input/s and validation ]
const _GROUP_ATTRIBUTES_MAP = {
  attribute: ATTRBT,
  map: [
    [ATTRBT[0], [[OPRTR[0], INPT[1], VLDTR[0]]]],
    [ATTRBT[1], [[OPRTR[1], INPT[0], VLDTR[1]], [OPRTR[2], INPT[0], VLDTR[1]], [OPRTR[3], INPT[0], VLDTR[1]], [OPRTR[4], INPT[0], VLDTR[1]]]],
    [ATTRBT[2], [[OPRTR[1], INPT[0], VLDTR[2]], [OPRTR[2], INPT[0], VLDTR[4]], [OPRTR[3], INPT[0], VLDTR[4]], [OPRTR[4], INPT[0], VLDTR[4]]]],
    [ATTRBT[3], [[OPRTR[6], INPT[2], VLDTR[6]], [OPRTR[0], INPT[4], VLDTR[7]], [OPRTR[5], INPT[4], VLDTR[7]], [OPRTR[7], INPT[2], VLDTR[6]]]],
    [ATTRBT[4], [[OPRTR[6], INPT[2], VLDTR[9]], [OPRTR[0], INPT[4], VLDTR[10]], [OPRTR[5], INPT[4], VLDTR[10]], [OPRTR[7], INPT[2], VLDTR[9]]]],
    [ATTRBT[5], [[OPRTR[1], INPT[3], VLDTR[0]]]]
  ]
};

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
    group: null,
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        ['osType', 'IN', []]
      ]
    },
    dirty: true,
    lastPublishedCopy: null,
    lastPublishedOn: 0,
    assignedPolicies: {}
  },
  // the policies objects to fill the group select/dropdown
  policies: [],

  initGroupFetchPoliciesStatus: null, // wait, complete, error
  groupStatus: null, // wait, complete, error

  // TODO if the reducer doesn't need to modify these, and the selectors aren't doing anything special,
  // then we may want to extract these to a steps.js and add them directly to the group-wizard component,
  // but keep in mind that we may want to dynamically add steps...
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
  visited: [],
  groupAttributesMap: _GROUP_ATTRIBUTES_MAP
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
    const newState = state.merge({
      ...initialState,
      groupStatus: 'complete'
    });
    return newState;
  },

  [ACTION_TYPES.GET_GROUP]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          ...initialState,
          groupStatus: 'wait'
        });
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
  ),

  [ACTION_TYPES.EDIT_GROUP]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    // Edit the value in the group, and keep track of the field as having been visited by the user.
    // Visited fields will show error/validation messages
    return state.setIn(fields, value).set('visited', _.uniq([...state.visited, field]));
  },

  [ACTION_TYPES.UPDATE_GROUP_CRITERIA]: (state, action) => {
    const { criteriaPath, value, fieldIndex } = action.payload;
    const criteriaPathTrimer = Number(criteriaPath.substring(1));
    let editedCriteria = [];
    if (fieldIndex > 2) {
      const fieldIndexTemp = 2; // concat to field value array if more then one value
      editedCriteria = state.group.groupCriteria.criteria[criteriaPathTrimer].map((field, index) => index === fieldIndexTemp ? field.concat(value) : field);
    } else {
      editedCriteria = state.group.groupCriteria.criteria[criteriaPathTrimer].map((field, index) => index === fieldIndex ? value : field);
    }
    let newEditedCriteria = [];
    if (fieldIndex === 0) {
      // attribute change, set operator to first valid operator and empty input fields
      const attrList = _GROUP_ATTRIBUTES_MAP.map.filter((list) => list[0] === value);
      newEditedCriteria = [value, attrList[0][1][0][0], []];
    } else if (fieldIndex === 1) {
      // operator change, empty input fields
      newEditedCriteria = [editedCriteria[0], value, []];
    } else { // field input value change
      newEditedCriteria = editedCriteria.slice();
    }
    // TODO support for nessted groupCriteria
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: [newEditedCriteria]
      }
    };
    return state.set('group', editedGroup);
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
