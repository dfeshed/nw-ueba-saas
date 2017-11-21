import { promiseRequest } from 'streaming-data/services/data-access/requests';

/**
 * Creates an incident rule using a pre-existing rule as a template
 * @method cloneIncidentRule
 * @param id
 * @returns {*}
 * @public
 */
function cloneIncidentRule(id) {
  return promiseRequest({
    method: 'createRecord',
    modelName: 'incident-rule-clone',
    query: {
      data: { id }
    }
  });
}

/**
 * Creates an incident rule from the rule information provided
 * @param rule
 * @returns {*}
 * @public
 */
function createIncidentRule(rule) {
  return promiseRequest({
    method: 'createRecord',
    modelName: 'incident-rules',
    query: {
      data: rule
    }
  });
}

/**
 * Deletes an incident rule
 * @method deleteIncidentRule
 * @param id
 * @returns {*}
 * @public
 */
function deleteIncidentRule(id) {
  return promiseRequest({
    method: 'deleteRecord',
    modelName: 'incident-rules',
    query: {
      data: { id }
    }
  });
}

/**
 * Returns a list of all of the known incident rules
 * @method getIncidentRules
 * @returns {*}
 * @public
 */
function getIncidentRules() {
  return promiseRequest({
    method: 'findAll',
    modelName: 'incident-rules',
    query: {}
  });
}

/**
 * Returns a single incident rule by way of rule ID
 * @method getIncidentRule
 * @param ruleId
 * @returns {*}
 * @public
 */
function getIncidentRule(ruleId) {
  return promiseRequest({
    method: 'queryRecord',
    modelName: 'incident-rules',
    query: {
      data: {
        id: ruleId
      }
    }
  });
}

/**
 * Returns the list of fields available for use in incident rule matching conditions
 * @method getAggregationFields
 * @returns {*}
 * @public
 */
function getIncidentFields() {
  return promiseRequest({
    method: 'findAll',
    modelName: 'incident-fields',
    query: {}
  });
}

/**
 * Reorders the list of incident rules
 * @method reorderIncidentRules
 * @param ruleIds An array of rule IDs in the new order
 * @returns {*}
 * @public
 */
function reorderIncidentRules(ruleIds) {
  return promiseRequest({
    method: 'updateRecord',
    modelName: 'incident-rules-reorder',
    query: {
      data: ruleIds
    }
  });
}

/**
 * Makes an update call to the service to persist the provided incident rule
 * @param rule {Object} The rule information to persist
 * @returns {*}
 * @public
 */
function saveIncidentRule(rule) {
  return promiseRequest({
    method: 'updateRecord',
    modelName: 'incident-rules',
    query: {
      data: rule
    }
  });
}

export default {
  cloneIncidentRule,
  createIncidentRule,
  deleteIncidentRule,
  getIncidentRule,
  getIncidentRules,
  getIncidentFields,
  reorderIncidentRules,
  saveIncidentRule
};
