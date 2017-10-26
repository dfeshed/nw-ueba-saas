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

export default {
  cloneAggregationRule,
  deleteAggregationRule,
  getAggregationRule,
  getAggregationRules,
  getAggregationFields,
  reorderAggregationRules
};
