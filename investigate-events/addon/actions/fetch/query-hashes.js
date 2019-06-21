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

const _generatePredicateRequest = (pillDataArray) => ({
  query: encodeMetaFilterConditions(pillDataArray)
});

// Takes query inputs and sends them off to get a hash.
const getHashForParams = (pillData) => {
  const request = lookup('service:request');
  // Save off each individual pill if there's more than one. We do not need to
  // wait for the return as we don't care about these individual pills.
  if (pillData.length > 1) {
    // const predicateRequests = pillData.map((pD) => _generatePredicateRequest(pD));
    request.promiseRequest({
      modelName: MODEL_NAME,
      method: 'persist',
      query: {
        predicateRequests: pillData.map((pD) => _generatePredicateRequest([pD]))
      }
    });
  }
  // This is what we care about. This is all the pills stringified, and saved as
  // one hash. We wait for this because this is the hash we put in the URL.
  // const pillDataAsString = encodeMetaFilterConditions(pillData);
  return request.promiseRequest({
    modelName: MODEL_NAME,
    method: 'persist',
    query: {
      predicateRequests: [_generatePredicateRequest(pillData)]
    }
  });
};

export {
  getParamsForHashes,
  getHashForParams
};