import { promiseRequest } from 'streaming-data/services/data-access/requests';

/**
 * Creates an aggregation rule using a pre-existing rule as a template
 * @method cloneAggregationRule
 * @param id
 * @returns {*}
 * @public
 */
function cloneAggregationRule(id) {
  return promiseRequest({
    method: 'createRecord',
    modelName: 'aggregation-rule-clone',
    query: {
      data: { id }
    }
  });
}

/**
 * Creates an aggregation rule from the rule information provided
 * @param rule
 * @returns {*}
 * @public
 */
function createAggregationRule(rule) {
  return promiseRequest({
    method: 'createRecord',
    modelName: 'aggregation-rules',
    query: {
      data: rule
    }
  });
}

/**
 * Deletes an aggregation rule
 * @method deleteAggregationRule
 * @param id
 * @returns {*}
 * @public
 */
function deleteAggregationRule(id) {
  return promiseRequest({
    method: 'deleteRecord',
    modelName: 'aggregation-rules',
    query: {
      data: { id }
    }
  });
}

/**
 * Returns a list of all of the known aggregation rules
 * @method getAggregationRules
 * @returns {*}
 * @public
 */
function getAggregationRules() {
  return promiseRequest({
    method: 'findAll',
    modelName: 'aggregation-rules',
    query: {}
  });
}

/**
 * Returns a single aggregation rule by way of rule ID
 * @method getAggregationRule
 * @param ruleId
 * @returns {*}
 * @public
 */
function getAggregationRule(ruleId) {
  return promiseRequest({
    method: 'queryRecord',
    modelName: 'aggregation-rules',
    query: {
      data: {
        id: ruleId
      }
    }
  });
}

/**
 * Returns the list of fields available for use in aggregation rule matching conditions
 * @method getAggregationFields
 * @returns {*}
 * @public
 */
function getAggregationFields() {
  return promiseRequest({
    method: 'findAll',
    modelName: 'aggregation-fields',
    query: {}
  });
}

/**
 * Reorders the list of aggregation rules
 * @method reorderAggregationRules
 * @param ruleIds An array of rule IDs in the new order
 * @returns {*}
 * @public
 */
function reorderAggregationRules(ruleIds) {
  return promiseRequest({
    method: 'updateRecord',
    modelName: 'aggregation-rules-reorder',
    query: {
      data: ruleIds
    }
  });
}

/**
 * Makes an update call to the service to persist the provided aggregation rule
 * @param rule {Object} The rule information to persist
 * @returns {*}
 * @public
 */
function saveAggregationRule(rule) {
  return promiseRequest({
    method: 'updateRecord',
    modelName: 'aggregation-rules',
    query: {
      data: rule
    }
  });
}

export default {
  cloneAggregationRule,
  createAggregationRule,
  deleteAggregationRule,
  getAggregationRule,
  getAggregationRules,
  getAggregationFields,
  reorderAggregationRules,
  saveAggregationRule
};
