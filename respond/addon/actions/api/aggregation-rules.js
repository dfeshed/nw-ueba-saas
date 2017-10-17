import { promiseRequest } from 'streaming-data/services/data-access/requests';

function getAggregationRule(ruleId = '123') {
  return promiseRequest({
    method: 'queryRecord',
    modelName: 'aggregation-rules',
    query: {
      id: ruleId
    }
  });
}

function getAggregationFields() {
  return promiseRequest({
    method: 'findAll',
    modelName: 'aggregation-fields',
    query: {}
  });
}

export default {
  getAggregationRule,
  getAggregationFields
};
