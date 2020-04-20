import { lookup } from 'ember-dependency-lookup';

function fetchRiskScoringSettings() {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'findAll',
    modelName: 'risk-scoring-settings',
    query: {}
  });
}

function updateRiskScoringSettings(settings) {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'updateRecord',
    modelName: 'risk-scoring-settings',
    query: {
      data: settings
    }
  });
}

export default {
  fetchRiskScoringSettings,
  updateRiskScoringSettings
};
