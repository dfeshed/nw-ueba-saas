import * as ACTION_TYPES from '../types';
import {
  getAllCategories,
  getAllEnabledUsers
} from 'respond/actions/creators/dictionary-creators';
import {
  getFields as getAggregationFields,
  getRuleConditions,
  getRuleConditionGroups,
  getRuleInfo,
  getAssigneeOptions } from 'respond/selectors/aggregation-rule';
import {
  getGroupedCategories
} from 'respond/selectors/dictionaries';
import ruleNormalizer from 'respond/reducers/respond/util/aggregation-rule-normalizer';
import _ from 'lodash';

// Properties removed from the rule information when an update request payload is constructed
const EXCLUDED_RULE_PROPERTIES_ON_UPDATE = [
  'matchConditions',
  'lastMatched',
  'alertsMatchedCount',
  'incidentsCreatedCount',
  'deleted',
  'notificationOptions'
];

/**
 * Constructs the update/create request payload. The meat of this is to create stringified JSON of the rule builder
 * conditions and condition groups, reconstructed in a nested data structure from the flattened structure used in state.
 * Additionally some fields are removed from the request either because they are superfluous or because they should not
 * be sent.
 * @param ruleInfo
 * @param conditionGroups
 * @param conditions
 * @returns {{uiFilterConditions: string}}
 * @private
 */
function prepareRequest(ruleInfo, conditionGroups, conditions) {
  const info = _.omit(ruleInfo, EXCLUDED_RULE_PROPERTIES_ON_UPDATE);
  const uiFilterConditions = ruleInfo.advancedUiFilterConditions ? // if this is an advanced query
    ruleInfo.uiFilterConditions : // use the uiFilterConditions string as is
    ruleNormalizer.toJSON(conditionGroups, conditions); // otherwise serialize the conditionGroups/conditions to a json string

  return {
    ...info,
    uiFilterConditions
  };
}

/**
 * Take the ID of an existing rule and creates a new rule based on that rule's configuration
 * @method cloneRule
 * @param templateRuleId
 * @public
 */
const cloneRule = (templateRuleId, onSuccess) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_CLONE_SAGA,
    templateRuleId,
    onSuccess
  };
};

/**
 * Deletes a given rule by ID
 * @method deleteRule
 * @param ruleId
 * @public
 */
const deleteRule = (ruleId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_DELETE_SAGA,
    ruleId
  };
};

/**
* Returns all known aggregation rules
* @method getRules
* @public
*/
const getRules = () => {
  return {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULES_SAGA
  };
};

/**
 * Either dispatches an update/save saga action for updating an existing rule, or it delegates to the
 * createRule() creator when the record is new
 * @param onSuccess callback that should be executed if the save/create operation is successful
 * @public
 */
const saveRule = (onSuccess) => {
  return (dispatch, getState) => {
    const state = getState();
    const ruleInfo = getRuleInfo(state);
    const conditionGroups = getRuleConditionGroups(state);
    const conditions = getRuleConditions(state);

    const update = prepareRequest(ruleInfo, conditionGroups, conditions);
    // If this is already an existing record
    if (update.id) {
      dispatch({
        type: ACTION_TYPES.SAVE_AGGREGATION_RULE_SAGA,
        ruleInfo: update,
        onSuccess
      });
    } else { // otherwise create a new aggregation rule record
      dispatch(createRule(update, onSuccess));
    }
  };
};

/**
 * Creates a new rule
 * @param ruleInfo The rule information used in creating the new record
 * @param onSuccess Callback to be invoked if the creation operation is successful
 * @public
 */
const createRule = (ruleInfo, onSuccess) => {
  return {
    type: ACTION_TYPES.CREATE_AGGREGATION_RULE_SAGA,
    ruleInfo,
    onSuccess
  };
};

/**
 * Returns the an individual aggregation rule
 * @method getRule
 * @param ruleId The rule ID to be fetched
 * @public
 */
const getRule = (ruleId) => {
  return (dispatch) => {
    if (ruleId) {
      dispatch({
        type: ACTION_TYPES.FETCH_AGGREGATION_RULE_SAGA,
        ruleId
      });
    } else {
      dispatch(newRule());
    }
  };
};

/**
 * Fetches the list of fields used in rule-builder and group-by configuration
 * @public
 */
const getFields = () => {
  return {
    type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS_SAGA
  };
};

/**
 * Fetches a rule and all related / required data for the rule details configuration
 * @param ruleId
 * @public
 */
const initializeRule = (ruleId) => {
  return (dispatch, getState) => {
    const state = getState();
    if (ruleId) {
      dispatch(getRule(ruleId));
    } else {
      dispatch(newRule());
    }

    if (!getAssigneeOptions(state).length) {
      dispatch(getAllEnabledUsers());
    }
    if (!getGroupedCategories(state).length) {
      dispatch(getAllCategories());
    }
    if (!getAggregationFields(state).length) {
      dispatch(getFields());
    }
  };
};

/**
 * Replaces any previous rule state with the template for a brand new rule
 * @public
 */
const newRule = () => ({ type: ACTION_TYPES.NEW_AGGREGATION_RULE });

/**
 * Sets the specified rule (by ID) as the currently selected rule. The selected rule can be deleted or cloned.
 * @param ruleId
 * @public
 */
const selectRule = (ruleId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_SELECT_RULE,
    payload: ruleId
  };
};

/**
 * Adds a new rule builder grouping, which can contain conditions. The following example illustrates this
 *
 * The expression (A = B AND B = C), contains one group (parentheses), and two conditions (A = B, B = C).
 *
 * Adding a new group with one condition, D = E, might result in the expression
 *
 * (A = B AND B = C AND (D = E))
 *
 * @public
 */
const addGroup = () => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_ADD_GROUP
  };
};

/**
 * Removes a group of conditions from the rule builder
 * @param groupId
 * @public
 */
const removeGroup = (groupId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_REMOVE_GROUP,
    payload: groupId
  };
};

/**
 * Adds a condition to a specified group (by group id)
 * @param groupId
 * @public
 */
const addCondition = (groupId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_ADD_CONDITION,
    payload: groupId
  };
};

/**
 * Removes a condition (by condition id)
 * @param conditionId
 * @public
 */
const removeCondition = (conditionId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_REMOVE_CONDITION,
    payload: conditionId
  };
};

/**
 * Reorders the rules by specifying an array of rule IDs, where they order of the IDs in the array specifies the new
 * order of the rules
 * @param ruleIds
 * @public
 */
const reorderRules = (ruleIds) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_REORDER_SAGA,
    ruleIds
  };
};

/**
 * Updates an existing condition by specifying the condition ID and the set of changes to make
 * @param conditionId
 * @param changes
 * @public
 */
const updateCondition = (conditionId, changes) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_UPDATE_CONDITION,
    payload: { conditionId, changes }
  };
};

/**
 * Updates an existing group by specifying the group ID and the set of changes to make
 * @param groupId
 * @param changes
 * @public
 */
const updateGroup = (groupId, changes) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_UPDATE_GROUP,
    payload: { groupId, changes }
  };
};

/**
 * Updates an aggregation rule by specifying the field name (fully qualified, e.g., 'ruleInfo.incidentScoringOptions.type')
 * and the new value that should be set
 * @param field
 * @param value
 * @public
 */
const updateRule = (field, value) => {
  const payload = {
    field,
    value
  };
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_UPDATE_INFO,
    payload
  };
};

/**
 * Removes / resets the match conditions in the rule-builder
 * @public
 */
const clearMatchConditions = () => ({
  type: ACTION_TYPES.AGGREGATION_RULES_CLEAR_MATCH_CONDITIONS
});

export {
  addCondition,
  addGroup,
  clearMatchConditions,
  cloneRule,
  deleteRule,
  getFields,
  getRule,
  getRules,
  initializeRule,
  removeCondition,
  removeGroup,
  reorderRules,
  saveRule,
  selectRule,
  updateCondition,
  updateGroup,
  updateRule
};