import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import _ from 'lodash';
import * as ACTION_TYPES from 'admin-source-management/actions/types';


const ATTRBT = ['osType', 'osDescription', 'hostname', 'ipv4', 'ipv6', 'agentMode'];
const OPRTR = ['IN', 'EQUAL', 'CONTAINS', 'STARTS_WITH', 'ENDS_WITH', 'NOT_IN', 'BETWEEN', 'NOT_BETWEEN'];
const INPT = ['textInput', 'osSelector', '2textInputs', 'agentSelector', 'textarea'];
const VLDTR = ['none', '256max', 'validHostname', 'validHostnameList', 'validHostnameChars', 'ipv4', 'ipv4Pair', 'ipv4List', 'ipv6', 'ipv6Pair', 'ipv6List'];
// In the future the _GROUP_ATTRIBUTES_MAP may be initialize with an API call
// _GROUP_ATTRIBUTES_MAP is derived from https://wiki.na.rsa.net/display/RPA/Cycle+9+Attributes
// The _GROUP_ATTRIBUTES_MAP is used to draw dynamically the selectors for attributes and operators and also the associated input/s
// Consider __GROUP_ATTRIBUTES_MAP.map[0] = [osType, [IN, osSelector, none]], here the 'osType' attribute has one operator 'IN", has selector type input 'osSelector' and has no validation
// __GROUP_ATTRIBUTES_MAP.map[1] = [osDescription, [EQUAL, textInput, 256max], ... plus three other operators with associated input/s and validation ]
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
  groupStatus: null, // wait, complete, error

  // the summary list of policies objects to fill the group select/dropdown
  groupList: [],
  groupListStatus: null, // wait, complete, error

  // the summary list of policies objects to fill the group select/dropdown
  policyList: [],
  policyListStatus: null, // wait, complete, error

  // TODO if the reducer doesn't need to modify these, and the selectors aren't doing anything special,
  // then we may want to extract these to a steps.js and add them directly to the group-wizard component,
  // but keep in mind that we may want to dynamically add steps...
  steps: [
    {
      id: 'identifyGroupStep',
      prevStepId: '',
      nextStepId: 'defineGroupStep',
      title: 'adminUsm.groupWizard.identifyGroup',
      stepComponent: 'usm-groups/group-wizard/identify-group-step',
      titlebarComponent: 'usm-groups/group-wizard/group-titlebar',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    },
    {
      id: 'defineGroupStep',
      prevStepId: 'identifyGroupStep',
      nextStepId: 'applyPolicyStep',
      title: 'adminUsm.groupWizard.defineGroup',
      stepComponent: 'usm-groups/group-wizard/define-group-step',
      titlebarComponent: 'usm-groups/group-wizard/group-titlebar',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    },
    {
      id: 'applyPolicyStep',
      prevStepId: 'defineGroupStep',
      nextStepId: 'reviewGroupStep',
      title: 'adminUsm.groupWizard.applyPolicy',
      stepComponent: 'usm-groups/group-wizard/apply-policy-step',
      titlebarComponent: 'usm-groups/group-wizard/group-titlebar',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    },
    {
      id: 'reviewGroupStep',
      prevStepId: 'applyPolicyStep',
      nextStepId: '',
      title: 'adminUsm.groupWizard.reviewGroup',
      stepComponent: 'usm-groups/group-wizard/review-group-step',
      titlebarComponent: 'usm-groups/group-wizard/group-titlebar',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar'
    }
  ],
  // keeps track of the form fields visited by the user
  visited: [],
  groupAttributesMap: _GROUP_ATTRIBUTES_MAP
};

export default reduxActions.handleActions({

  [ACTION_TYPES.NEW_GROUP]: (state /* , action */) => {
    const newState = state.merge({
      ...initialState,
      groupStatus: 'complete'
    });
    return newState;
  },

  [ACTION_TYPES.FETCH_GROUP]: (state, action) => (
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

  [ACTION_TYPES.FETCH_GROUP_LIST]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          groupList: [],
          groupListStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('groupListStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          groupList: action.payload.data,
          groupListStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.FETCH_POLICY_LIST]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          policyList: [],
          policyListStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('policyListStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          policyList: action.payload.data,
          policyListStatus: 'complete'
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
    const criteriaPathTrimed = Number(criteriaPath.substring(1));
    let editedCriteria = [];
    if (fieldIndex < 3) { // attribute, operator or single input change
      editedCriteria = state.group.groupCriteria.criteria[criteriaPathTrimed].map((field, index) => index === fieldIndex ? value : field);
    } else { // two input change for between operator
      const criteriaToEdit = state.group.groupCriteria.criteria[criteriaPathTrimed].slice();
      let editedInput = [];
      if (fieldIndex === 10) {
        // first input change for between operator
        editedInput = Object.assign([], criteriaToEdit[2], { 0: value });
      } else if (fieldIndex === 11) {
        // second input change for between operator
        editedInput = Object.assign([], criteriaToEdit[2], { 1: value });
      }
      editedCriteria = Object.assign([], criteriaToEdit, { 2: editedInput });
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
    const oldCriterias = state.group.groupCriteria.criteria.slice();
    const newCriterias = oldCriterias.map((criteria, index) => index === criteriaPathTrimed ? newEditedCriteria : criteria);

    // TODO support for nested group of criterias
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: newCriterias
      }
    };
    return state.set('group', editedGroup);
  },

  [ACTION_TYPES.ADD_CRITERIA]: (state) => {
    const oldCriterias = state.group.groupCriteria.criteria.slice();
    const newCriterias = oldCriterias.concat([['osType', 'IN', []]]);
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: newCriterias
      }
    };
    return state.set('group', editedGroup);
  },

  [ACTION_TYPES.REMOVE_CRITERIA]: (state, action) => {
    const { criteriaPath } = action.payload;
    const criteriaPathTrimed = Number(criteriaPath.substring(1));
    const oldCriterias = state.group.groupCriteria.criteria.slice();
    const newCriterias = oldCriterias.filter((criteria, index) => index !== criteriaPathTrimed ? criteria : '');
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: newCriterias
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
  ),

  [ACTION_TYPES.SAVE_PUBLISH_GROUP]: (state, action) => (
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
