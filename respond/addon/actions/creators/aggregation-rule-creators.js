import { aggregationRules } from '../api';
import * as ACTION_TYPES from '../types';
import * as errorHandlers from '../util/error-handlers';
import Ember from 'ember';

const defaultCallbacks = {
  onSuccess() {},
  onFailure() {}
};

const { Logger } = Ember;

/**
 * Take the ID of an existing rule and creates a new rule based on that rule's configuration
 * @method cloneRule
 * @param templateRuleId
 * @public
 */
const cloneRule = (templateRuleId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_CLONE_SAGA,
    templateRuleId
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
 * Returns the an individual aggregation rule
 * @method getRule
 * @param id The rule ID to be fetched
 * @param callbacks
 * @param callbacks.onSuccess {function} - The callback to be executed when the operation is successful (e.g., showing a flash notification)
 * @param callbacks.onFailure {function} - The callback to be executed when the operation fails
 * @returns {Promise}
 * @public
 */
const getRule = (id, callbacks = defaultCallbacks) => {
  return {
    type: ACTION_TYPES.FETCH_AGGREGATION_RULE,
    promise: aggregationRules.getAggregationRule(id),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.FETCH_AGGREGATION_RULE, response);
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        errorHandlers.handleContentRetrievalError(response, `aggregation rule ${id}`);
        callbacks.onFailure(response);
      }
    }
  };
};

const getFields = (callbacks = defaultCallbacks) => {
  return {
    type: ACTION_TYPES.FETCH_AGGREGATION_FIELDS,
    promise: aggregationRules.getAggregationFields(),
    meta: {
      onSuccess: (response) => {
        Logger.debug(ACTION_TYPES.FETCH_AGGREGATION_FIELDS, response);
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        errorHandlers.handleContentRetrievalError(response, 'aggregation fields');
        callbacks.onFailure(response);
      }
    }
  };
};

const selectRule = (ruleId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_SELECT_RULE,
    payload: ruleId
  };
};

const addGroup = () => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_ADD_GROUP
  };
};

const removeGroup = (groupId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_REMOVE_GROUP,
    payload: groupId
  };
};

const addCondition = (groupId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_ADD_CONDITION,
    payload: groupId
  };
};

const removeCondition = (conditionId) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_REMOVE_CONDITION,
    payload: conditionId
  };
};

const reorderRules = (ruleIds) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_REORDER_SAGA,
    ruleIds
  };
};

const updateCondition = (conditionId, changes) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_UPDATE_CONDITION,
    payload: { conditionId, changes }
  };
};

const updateGroup = (groupId, changes) => {
  return {
    type: ACTION_TYPES.AGGREGATION_RULES_UPDATE_GROUP,
    payload: { groupId, changes }
  };
};

export {
  addCondition,
  addGroup,
  cloneRule,
  deleteRule,
  getFields,
  getRule,
  getRules,
  removeCondition,
  removeGroup,
  reorderRules,
  selectRule,
  updateCondition,
  updateGroup
};