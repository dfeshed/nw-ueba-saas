import { lookup } from 'ember-dependency-lookup';


const getInvestigateServiceId = () => {
  const request = lookup('service:request');
  return request.promiseRequest({
    method: 'serviceId',
    modelName: 'investigate-service',
    query: {}
  });
};

export {
  getInvestigateServiceId
};
