import { lookup } from 'ember-dependency-lookup';

import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';

const MODEL_NAME = 'query-hashes';

// Takes array of hashes and executes request
// to retrieve the params for each of those hashes
const getParamsForHashes = (hashes) => {
  const request = lookup('service:request');

  return request.promiseRequest({
    modelName: MODEL_NAME,
    method: 'find',
    query: {
      predicateIds: hashes
    }
  });
};

// Takes query inputs and sends them off
// to get a hash
const getHashForParams = (pillData, language) => {
  const request = lookup('service:request');
  const pillDataAsString = encodeMetaFilterConditions(pillData, language);
  return request.promiseRequest({
    modelName: MODEL_NAME,
    method: 'persist',
    query: {
      predicateRequests: [{
        query: pillDataAsString
      }]
    }
  });
};

export {
  getParamsForHashes,
  getHashForParams
};