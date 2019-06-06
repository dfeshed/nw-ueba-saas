import { lookup } from 'ember-dependency-lookup';

const getLocalRiskScore = (host, files) => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'localRiskScore',
    modelName: 'risk-score',
    query: {
      data: {
        host,
        files,
        useCache: false
      }
    }
  });
};

export {
  getLocalRiskScore
};