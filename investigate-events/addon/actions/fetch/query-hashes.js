import { lookup } from 'ember-dependency-lookup';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import {
  COMPLEX_FILTER,
  QUERY_FILTER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

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
    // filter out non-filter items
    const predicateRequests = pillData.reduce((acc, cur) => {
      if (cur.type === COMPLEX_FILTER || cur.type === QUERY_FILTER || cur.type === TEXT_FILTER) {
        acc.push(_generatePredicateRequest([cur]));
      }
      return acc;
    }, []);
    request.promiseRequest({
      modelName: MODEL_NAME,
      method: 'persist',
      query: { predicateRequests }
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