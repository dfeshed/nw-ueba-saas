import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/respond';
import ruleNormalizer from 'configure/reducers/respond/incident-rules/incident-rule-normalizer';
import reduxActions from 'redux-actions';

const initialState = {
  // rule information
  ruleInfo: null,

  // fetch status of the rule information: 'wait' 'complete', or 'error'
  ruleStatus: null,

  // a map of the rule builder groups (organized by id)
  conditionGroups: null,

  // a map of the rule builder conditions (organized by key id)
  conditions: null,

  // fields used in rules and group-by configuration
  fields: [],

  // fetch status of the fields used in rules and group-by configuration: 'wait', 'complete', or 'error'
  fieldsStatus: null,

  // whether a save (or equivalent) operation is occurring
  isTransactionUnderway: false,

  // keeps track of the form controls visited by the user
  visited: []
};

const reducer = reduxActions.handleActions({
  [ACTION_TYPES.NEW_INCIDENT_RULE]: (state) => {
    const normalizedConditions = ruleNormalizer.emptyConditions();
    return state.merge({
      ruleInfo: {
        action: 'GROUP_INTO_INCIDENT',
        incidentCreationOptions: {
          ruleTitle: '${ruleName} for ${groupByValue1}',
          categories: []
        },
        priorityScale: {
          CRITICAL: 90,
          HIGH: 50,
          MEDIUM: 20,
          LOW: 1
        },
        incidentScoringOptions: {
          type: 'average'
        },
        notificationOptions: {}
      },
      conditionGroups: normalizedConditions && normalizedConditions.groups,
      conditions: normalizedConditions && normalizedConditions.conditions
    });
  },
  [ACTION_TYPES.FETCH_INCIDENT_FIELDS_STARTED]: (state) => state.set('fieldsStatus', 'wait'),
  [ACTION_TYPES.FETCH_INCIDENT_FIELDS_FAILED]: (state) => {
    return state.merge({
      fields: [],
      fieldsStatus: 'error'
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_FIELDS]: (state, { payload }) => {
    return state.merge({
      fields: payload.data,
      fieldsStatus: 'complete'
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_RULE_STARTED]: (state) => state.merge({ ruleStatus: 'wait', visited: [] }),
  [ACTION_TYPES.FETCH_INCIDENT_RULE_FAILED]: (state) => state.set('ruleStatus', 'error'),
  [ACTION_TYPES.FETCH_INCIDENT_RULE]: (state, action) => {
    const { payload: { data } } = action;
    const normalizedConditions = !data.advancedUiFilterConditions ? ruleNormalizer.processRuleConfiguration(data.uiFilterConditions) : {};
    return state.merge({
      ruleStatus: 'complete',
      ruleInfo: data,
      conditionGroups: normalizedConditions && normalizedConditions.groups,
      conditions: normalizedConditions && normalizedConditions.conditions
    });
  },

  [ACTION_TYPES.INCIDENT_RULES_UPDATE_INFO]: (state, { payload: { field, value } }) => {
    const fields = field.split('.');
    // Update the value in the ruleInfo, and keep track of the field as having been visited by the user.
    // Visited fields will show error / validation messages
    return state.setIn(fields, value).set('visited', [...state.visited, field]);
  },

  [ACTION_TYPES.INCIDENT_RULES_ADD_CONDITION]: (state, { payload }) => {
    const id = ruleNormalizer.getNewConditionId();
    const condition = {
      [id]: { id, groupId: payload, filterType: 'FILTER' }
    };
    return state.set('conditions', Immutable.merge(state.conditions, condition));
  },
  [ACTION_TYPES.INCIDENT_RULES_REMOVE_CONDITION]: (state, { payload }) => {
    return state.set('conditions', Immutable.without(state.conditions, payload));
  },
  [ACTION_TYPES.INCIDENT_RULES_ADD_GROUP]: (state) => {
    const id = ruleNormalizer.getNewGroupId();
    const group = {
      [id]: { id, filterType: 'FILTER_GROUP', groupId: 0, logicalOperator: 'and' } // all additional groups are added under the root level group
    };
    return state.set('conditionGroups', Immutable.merge(state.conditionGroups, group));
  },
  [ACTION_TYPES.INCIDENT_RULES_REMOVE_GROUP]: (state, { payload }) => {
    return state.set('conditionGroups', Immutable.without(state.conditionGroups, payload));
  },
  [ACTION_TYPES.INCIDENT_RULES_UPDATE_CONDITION]: (state, { payload: { conditionId, changes } }) => {
    const condition = state.conditions[conditionId];
    const updatedCondition = {
      [conditionId]: condition.merge(changes)
    };
    return state.set('conditions', Immutable.merge(state.conditions, updatedCondition));
  },
  [ACTION_TYPES.INCIDENT_RULES_UPDATE_GROUP]: (state, { payload: { groupId, changes } }) => {
    const group = state.conditionGroups[groupId];
    const updatedGroup = {
      [groupId]: group.merge(changes)
    };
    return state.set('conditionGroups', Immutable.merge(state.conditionGroups, updatedGroup));
  },
  // Clear/remove all of the rule query conditions (uiFilterConditions, matchConditions) and all normalized conditionGroups and conditions
  [ACTION_TYPES.INCIDENT_RULES_CLEAR_MATCH_CONDITIONS]: (state) => {
    const normalizedConditions = ruleNormalizer.emptyConditions();
    return state.merge({
      ruleInfo: state.ruleInfo.merge({
        uiFilterConditions: '',
        matchConditions: ''
      }),
      conditionGroups: normalizedConditions && normalizedConditions.groups,
      conditions: normalizedConditions && normalizedConditions.conditions
    });
  },
  [ACTION_TYPES.INCIDENT_RULES_SAVE_STARTED]: (state) => state.set('isTransactionUnderway', true),
  [ACTION_TYPES.INCIDENT_RULES_SAVE_FAILED]: (state) => state.set('isTransactionUnderway', false),
  [ACTION_TYPES.INCIDENT_RULES_SAVE]: (state, { payload: { data } }) => {
    const normalizedConditions = !data.advancedUiFilterConditions ? ruleNormalizer.processRuleConfiguration(data.uiFilterConditions) : {};
    return state.merge({
      ruleInfo: data,
      conditionGroups: normalizedConditions.groups,
      conditions: normalizedConditions.conditions,
      isTransactionUnderway: false
    });
  }
}, Immutable.from(initialState));

export default reducer;