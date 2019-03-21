import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import _ from 'lodash';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

// In the future the _GROUP_ATTRIBUTES_MAP may be initialize with an API call
// _GROUP_ATTRIBUTES_MAP is derived from https://wiki.na.rsa.net/display/RPA/Cycle+9+Attributes
// The _GROUP_ATTRIBUTES_MAP is used to draw dynamically the selectors for attributes and operators and also the associated input/s
// Consider __GROUP_ATTRIBUTES_MAP.map[0] = [osType, [IN, os-selector-input, notEmpty]], here the 'osType' attribute has one operator 'IN", has selector type input 'os-selector' and has no validation
// __GROUP_ATTRIBUTES_MAP.map[1] = [osDescription, [EQUALS, text-input, maxLength255], ... plus three other operators with associated input/s and validation ]

/* eslint-disable no-multi-spaces*/
const _GROUP_ATTRIBUTES_MAP = {
  attribute: ['osType', 'osDescription', 'hostname', 'ipv4', 'ipv6'],
  map: [
    ['osType', [
      ['IN',            'os-selector',          'notEmpty']
    ]],
    ['osDescription', [
      ['EQUAL',         'text-input',           'maxLength255'],
      ['CONTAINS',      'text-input',           'maxLength255'],
      ['STARTS_WITH',   'text-input',           'maxLength255'],
      ['ENDS_WITH',     'text-input',           'maxLength255']
    ]],
    ['hostname', [
      ['EQUAL',         'text-input',           'validHostname'],
      ['CONTAINS',      'text-input',           'validHostnameContains'],
      ['STARTS_WITH',   'text-input',           'validHostnameStartsWith'],
      ['ENDS_WITH',     'text-input',           'validHostnameEndsWith'],
      ['IN',            'textarea-input',       'validHostnameList']
    ]],
    ['ipv4', [
      ['BETWEEN',       'between-text-input',   'validIPv4'],
      ['IN',            'textarea-input',       'validIPv4List'],
      ['NOT_IN',        'textarea-input',       'validIPv4List'],
      ['NOT_BETWEEN',   'between-text-input',   'validIPv4']
    ]],
    ['ipv6', [
      ['BETWEEN',       'between-text-input',   'validIPv6'],
      ['IN',            'textarea-input',       'validIPv6List'],
      ['NOT_IN',        'textarea-input',       'validIPv6List'],
      ['NOT_BETWEEN',   'between-text-input',   'validIPv6']
    ]]
  ]
};
/* eslint-enable no-multi-spaces */

export const getValidatorForExpression = (expression) => {
  let validator = null;
  if (expression) {
    const [attribute, operator] = expression;
    const attrIndex = _GROUP_ATTRIBUTES_MAP.attribute.indexOf(attribute);
    for (let index = 0; index < _GROUP_ATTRIBUTES_MAP.map[attrIndex][1].length; index++) {
      const element = _GROUP_ATTRIBUTES_MAP.map[attrIndex][1][index];
      if (element[0] === operator) {
        validator = element[2];
        break;
      }
    }
  }
  return validator;
};

export const initialState = {
  // the group object to be created/updated/saved
  criteriaCache: [
    ['osType', 'IN', []]
  ],
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
  groupOrig: {},
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
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar',
      prevButtonDisabled: true,
      nextButtonDisabled: false,
      saveButtonDisabled: true,
      publishButtonDisabled: true,
      showErrors: false
    },
    {
      id: 'defineGroupStep',
      prevStepId: 'identifyGroupStep',
      nextStepId: 'applyPolicyStep',
      title: 'adminUsm.groupWizard.defineGroup',
      stepComponent: 'usm-groups/group-wizard/define-group-step',
      titlebarComponent: 'usm-groups/group-wizard/group-titlebar',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar',
      prevButtonDisabled: false,
      nextButtonDisabled: false,
      saveButtonDisabled: false,
      publishButtonDisabled: false,
      showErrors: false
    },
    {
      id: 'applyPolicyStep',
      prevStepId: 'defineGroupStep',
      nextStepId: '',
      title: 'adminUsm.groupWizard.applyPolicy.stepTitle',
      stepComponent: 'usm-groups/group-wizard/apply-policy-step',
      titlebarComponent: 'usm-groups/group-wizard/group-titlebar',
      toolbarComponent: 'usm-groups/group-wizard/group-toolbar',
      prevButtonDisabled: false,
      nextButtonDisabled: true,
      saveButtonDisabled: false,
      publishButtonDisabled: false,
      showErrors: false
    }
  ],
  rankingSteps: [
    {
      id: 'chooseSourceStep',
      prevStepId: '',
      nextStepId: 'editRankingStep',
      title: 'adminUsm.groupRankingWizard.chooseSource',
      stepComponent: 'usm-groups/group-ranking/choose-source-step',
      titlebarComponent: 'usm-groups/group-ranking/group-titlebar',
      toolbarComponent: 'usm-groups/group-ranking/group-toolbar'
    },
    {
      id: 'editRankingStep',
      prevStepId: 'chooseSourceStep',
      nextStepId: '',
      title: 'adminUsm.groupRankingWizard.editRanking',
      stepComponent: 'usm-groups/group-ranking/edit-ranking-step',
      titlebarComponent: 'usm-groups/group-ranking/group-titlebar',
      toolbarComponent: 'usm-groups/group-ranking/group-toolbar'
    }
  ],
  // keeps track of the form fields visited by the user
  visited: [],
  groupAttributesMap: _GROUP_ATTRIBUTES_MAP,
  groupRanking: [],
  groupRankingOrig: [],
  selectedSourceType: null,
  groupRankingStatus: null,
  selectedGroupRanking: null,
  groupRankingPrevListStatus: null
};

export default reduxActions.handleActions({

  [ACTION_TYPES.NEW_GROUP]: (state /* , action */) => {
    const newState = state.merge({
      ...initialState,
      groupOrig: initialState.group,
      groupStatus: 'complete'
    });
    return newState;
  },

  [ACTION_TYPES.FETCH_GROUP]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        // reset everything on load start (same as NEW_GROUP) so things are in sync in case of a load error
        return state.merge({
          ...initialState,
          groupOrig: initialState.group,
          groupStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('groupStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          group: action.payload.data,
          groupOrig: action.payload.data,
          groupStatus: 'complete',
          criteriaCache: action.payload.data.groupCriteria.criteria.slice()
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

  [ACTION_TYPES.UPDATE_GROUP_STEP]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    return state.setIn(fields, value);
  },

  [ACTION_TYPES.DISCARD_GROUP_CHANGES]: (state) => {
    return state.set('group', state.groupOrig);
  },

  [ACTION_TYPES.REMOVE_PLACEHOLDER_POLICY_ASSIGNMENTS]: (state) => {
    const assignments = state.group.assignedPolicies;
    const updatedAssignments = {};
    for (const key in assignments) {
      if (assignments.hasOwnProperty(key)) {
        if (assignments[key].referenceId !== 'placeholder') {
          // delete assignments.key;
          updatedAssignments[key] = assignments[key];
        }
      }
    }
    const editedGroup = {
      ...state.group,
      assignedPolicies: updatedAssignments
    };
    return state.set('group', editedGroup);
  },

  [ACTION_TYPES.UPDATE_CRITERIA_FROM_CACHE]: (state) => {
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: state.criteriaCache
      }
    };
    return state.set('group', editedGroup);
  },

  [ACTION_TYPES.UPDATE_GROUP_CRITERIA]: (state, action) => {
    const { criteriaPath, value, fieldIndex } = action.payload;
    const criteriaPathTrimed = Number(criteriaPath.substring(1));
    let editedCriteria = [];
    if (fieldIndex < 3 || fieldIndex === 9) { // attribute, operator, single input change or OS multiple selector
      const editFieldIndex = fieldIndex === 9 ? 2 : fieldIndex;
      editedCriteria = state.criteriaCache[criteriaPathTrimed].map((field, index) => index === editFieldIndex ? value : field);
    } else { // two input change for between operator
      const criteriaToEdit = state.criteriaCache[criteriaPathTrimed].slice();
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
    let onlyCriteriaCache = false;
    if (fieldIndex === 0) {
      // attribute change, set operator to first valid operator and empty input fields
      const attrList = _GROUP_ATTRIBUTES_MAP.map.filter((list) => list[0] === value);
      newEditedCriteria = [value, attrList[0][1][0][0], []];
    } else if (fieldIndex === 1) {
      // operator change, empty input fields
      newEditedCriteria = [editedCriteria[0], value, []];
    } else { // field input value change
      newEditedCriteria = editedCriteria.slice();
      if (fieldIndex !== 9) {
        // Text field input value change
        onlyCriteriaCache = true;
      }
    }
    const oldCriterias = state.criteriaCache.slice();
    const newCriterias = oldCriterias.map((criteria, index) => index === criteriaPathTrimed ? newEditedCriteria : criteria);

    // TODO support for nested group of criterias
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: newCriterias
      }
    };
    /* Don't opdate DOM for text input fields focus out actions
    Update onlyCriteriaCache to avoid focus out actions side effects and improve performance */
    if (onlyCriteriaCache) {
      return state.set('criteriaCache', newCriterias);
    } else {
      return state.merge({
        group: editedGroup,
        criteriaCache: newCriterias
      });
    }
  },

  [ACTION_TYPES.ADD_CRITERIA]: (state) => {
    const oldCriterias = state.criteriaCache.slice();
    const newCriterias = oldCriterias.concat([['osType', 'IN', []]]);
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: newCriterias
      }
    };
    return state.merge({
      group: editedGroup,
      criteriaCache: newCriterias
    });
  },

  [ACTION_TYPES.REMOVE_CRITERIA]: (state, action) => {
    const { criteriaPath } = action.payload;
    const criteriaPathTrimed = Number(criteriaPath.substring(1));
    const oldCriterias = state.criteriaCache.slice();
    const newCriterias = oldCriterias.filter((criteria, index) => index !== criteriaPathTrimed ? criteria : '');
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        criteria: newCriterias
      }
    };
    return state.merge({
      group: editedGroup,
      criteriaCache: newCriterias
    });
  },

  [ACTION_TYPES.ADD_OR_OPERATOR]: (state, action) => {
    const { andOr } = action.payload;
    const editedGroup = {
      ...state.group,
      groupCriteria: {
        ...state.group.groupCriteria,
        conjunction: andOr
      }
    };
    return state.set('group', editedGroup);
  },

  [ACTION_TYPES.FETCH_GROUP_RANKING]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          groupRanking: [],
          groupRankingOrig: [],
          groupRankingStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('groupRankingStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          groupRanking: action.payload.data,
          groupRankingOrig: action.payload.data,
          groupRankingStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.REORDER_GROUP_RANKING]: (state, action) => {
    const { groupRanking } = action.payload;
    return state.set('groupRanking', groupRanking);
  },

  [ACTION_TYPES.SELECT_GROUP_RANKING]: (state, action) => {
    const { groupRankingName } = action.payload;
    return state.set('selectedGroupRanking', groupRankingName);
  },

  [ACTION_TYPES.SET_TOP_RANKING]: (state) => {
    const selectedGroup = state.groupRanking.filter((group) => state.selectedGroupRanking === group.name);
    return state.merge({
      groupRanking: selectedGroup.concat(state.groupRanking.filter((group) => state.selectedGroupRanking !== group.name)),
      selectedGroupRanking: null
    });
  },

  [ACTION_TYPES.RESET_GROUP_RANKING]: (state) => {
    return state.merge({
      groupRanking: state.groupRankingOrig.slice(),
      selectedGroupRanking: null
    });
  },

  [ACTION_TYPES.SAVE_GROUP_RANKING]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('groupRankingStatus', 'wait');
      },
      failure: (state) => {
        return state.set('groupRankingStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          groupRanking: action.payload.data,
          groupRankingOrig: action.payload.data,
          groupRankingStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.SOURCE_TYPE]: (state, action) => {
    const { sourceType } = action.payload;
    return state.set('selectedSourceType', sourceType);
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
          groupOrig: action.payload.data,
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
          groupOrig: action.payload.data,
          groupStatus: 'complete'
        });
      }
    })
  )
}, Immutable.from(initialState));
