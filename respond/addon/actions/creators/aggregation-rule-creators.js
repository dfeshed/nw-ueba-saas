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
  getFields,
  getRule,
  removeCondition,
  removeGroup,
  updateCondition,
  updateGroup
};