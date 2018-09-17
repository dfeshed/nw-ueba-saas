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
// to get a hash.

const getHashForParams = (pillData, language) => {
  const request = lookup('service:request');

  // fire and forget, we want to persist the ENTIRE
  // query as one hash, but do not need to know the
  // outcome. But we only need to send this if
  // there is more than one pill, otherwise the
  // request below does the work of sending the
  // entire query
  if (pillData.length > 1) {
    const pillDataAsString = encodeMetaFilterConditions(pillData, language);
    request.promiseRequest({
      modelName: MODEL_NAME,
      method: 'persist',
      query: {
        predicateRequests: [{
          query: pillDataAsString
        }]
      }
    });
  }

  const predicateRequests = pillData.map((pD) => {
    const pillDataString = encodeMetaFilterConditions([pD], language);
    return {
      query: pillDataString
    };
  });

  return request.promiseRequest({
    modelName: MODEL_NAME,
    method: 'persist',
    query: {
      predicateRequests
    }
  });
};

export {
  getParamsForHashes,
  getHashForParams
};