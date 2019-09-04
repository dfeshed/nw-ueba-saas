import { assert } from '@ember/debug';
import { getProperties } from '@ember/object';
import { isBlank } from '@ember/utils';
import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import { encodeMetaFilterConditions, addSessionIdFilter } from 'investigate-shared/actions/api/events/utils';
import { getTimeRangeIdFromRange } from 'investigate-shared/utils/time-range-utils';
import { OPEN_PAREN, CLOSE_PAREN } from 'investigate-events/constants/pill';

/**
 * Creates (but does not start) a stream to fetch a given number of events.
 * To start the stream, the caller should call `stream.start()`.
 * @param {object} query Represents the Core query inputs
 * @param {object[]} language Array of meta key definitions from Core SDK `language` call.
 * @param {number} limit The maximum number of records to stream to the client.
 * @param {number} batch The maximum number of records to include in a single socket response message.
 * @param {string} [startSessionId] Optional lower bound (exclusive) for session IDs.
 * @returns {object} Newly created stream instance.
 * @public
 */
function _buildEventStreamInputs(query, language, limit, batch = 1, startSessionId = null) {
  const inputs = _makeServerInputsForQuery(query, language);
  inputs.stream = { limit, batch };
  const metaFilterInput = inputs.filter.findBy('field', 'query');
  metaFilterInput.value = addSessionIdFilter(metaFilterInput.value, startSessionId);
  return inputs;
}

function buildMetaValueStreamInputs(metaName, query, language, queryOptions, limit, batch) {
  const inputs = _buildEventStreamInputs(query, language, limit, batch);
  inputs.filter.pushObject({ field: 'metaName', value: metaName });
  if (queryOptions) {
    const { size, metric, sortField, sortOrder } = getProperties(queryOptions, 'size', 'metric', 'sortField', 'sortOrder');
    inputs.filter.pushObjects([
      { field: 'valuesCount', value: size },
      { field: 'flags', value: `${metric},sort-${sortField},order-${sortOrder}` }
    ]);
  }
  return inputs;
}

/**
 * Given an object representing a query, computes the input parameters required to submit that
 * query to the server.
 * @param {object} query The query object.
 * @param {object[]} language Array of meta key definitions. @see investigate-events/state/query
 * @public
 */
function _makeServerInputsForQuery(query, language) {
  const {
    serviceId, startTime, endTime, metaFilter
  } = getProperties(
    query || {}, 'serviceId', 'startTime', 'endTime', 'metaFilter'
  );

  assert(
    'Cannot make a core query without a service id, start time & end time.',
    !isBlank(serviceId) && !isNaN(startTime) && !isNaN(endTime)
  );

  return {
    filter: [
      { field: 'endpointId', value: serviceId },
      { field: 'timeRange', range: { from: startTime, to: endTime } },
      { field: 'query', value: encodeMetaFilterConditions(metaFilter || [], language) }
    ]
  };
}

function executeMetaValuesRequest(request, inputs, values) {
  return new RSVP.Promise((resolve, reject) => {
    values.setProperties({
      data: [],
      status: 'streaming',
      reason: undefined
    });

    // let rendering for request about to go out happen
    // before request goes out
    run.next(() => {
      request.streamRequest({
        method: 'stream',
        modelName: 'core-meta-value',
        query: inputs,
        onInit(stopStream) {
          values.set('stopStreaming', stopStream);
        },
        onResponse(response) {
          if (response) {
            run.next(function() {
              if (response.data && response.data.length) {
                // Meta Values call *sometimes* returns "partial" results while still computing results.
                // So when we get values back, replace whatever the previous set of values were; don't append to them.
                values.set('data', response.data);
              }
              values.set('description', response.meta && response.meta.description);
              const percent = response.meta && response.meta.percent;
              if (percent !== undefined) {
                values.set('percent', percent);
              }
            });
          }
        },
        onError(response) {
          values.setProperties({
            status: 'error',
            reason: response && response.code
          });
          reject();
        },
        onCompleted() {
          values.set('status', 'complete');
          resolve();
        },
        onStopped() {
          values.set('status', 'idle');
          resolve();
        }
      });
    });
  });
}

/**
 * Parses a URL query param object.
 *
 * @param {object} params Query param object.
 * @return {object}
 * @public
 */
function parseBasicQueryParams(params) {

  // hashes are comma-separated if there are many
  let pillDataHashes = params.pdhash;
  if (params.pdhash) {
    // pdhash can either be string or array of strings
    // if array of strings, leave it alone, otherwise
    // convert to array
    if (typeof params.pdhash === 'string') {
      pillDataHashes = params.pdhash.split(',');
    }
  }

  return {
    pillData: params.mf,
    pillDataHashes,
    endTime: params.et,
    sessionId: params.eid,
    metaPanelSize: params.mps,
    reconSize: params.rs,
    serviceId: params.sid,
    startTime: params.st,
    sortField: params.sortField,
    sortDir: params.sortDir,
    selectedTimeRangeId: getTimeRangeIdFromRange(params.st, params.et)
  };
}

function filterIsPresent(filters, freeFormText) {
  const currentFilters = encodeMetaFilterConditions(filters).replace(/(&&\s*)*$/g, '').trim();
  return currentFilters === freeFormText.trim();
}

function selectPillsFromPosition(pills, position, direction) {
  let newPills = [];
  if (direction === 'right') {
    newPills = pills.filter((pill) => pills.indexOf(pill) >= position);
  } else if (direction === 'left') {
    newPills = pills.filter((pill) => pills.indexOf(pill) <= position);
  }
  return newPills;
}

/**
 * Looks for an open paren immediately preceding a closed paren, based off of
 * the close paren's position.
 * @param {Object[]} arr Array of filters
 * @param {number} closeParenIndex Index of closed paren
 */
function hasEmptyParensAt(arr, closeParenIndex) {
  const op = arr[closeParenIndex - 1];
  const cp = arr[closeParenIndex];
  return op && op.type === OPEN_PAREN && cp && cp.type === CLOSE_PAREN;
}

/**
 * Find empty paren sets within the array of filters. This is a recursive
 * function, so if you have nested parens like ( ( ( ) ) ), it will find all
 * paren sets if you start from the inner empty paren set.
 * @param {Object[]} pillsData Array of filters
 * @param {number} position Index within `pillsData` to look for empty parens
 */
function findEmptyParensAtPosition(pillsData, position) {
  const pillsDataCopy = [...pillsData];
  let emptyParenSets = [];
  let currentPosition = position;
  while (currentPosition >= 0 && hasEmptyParensAt(pillsDataCopy, currentPosition)) {
    emptyParenSets = emptyParenSets.concat(pillsDataCopy.splice(currentPosition - 1, 2));
    currentPosition--;
  }
  return emptyParenSets;
}

function findAllEmptyParens(pillsData) {
  const emptyParenSets = [];
  let i = pillsData.length - 1;
  for (i; i >= 0; i--) {
    if (isEmptyParenSetAt(pillsData, i)) {
      if (pillsData.length === 2) {
        return pillsData;
      } else {
        // remove empty paren set by mutating the pillsData array
        emptyParenSets.push(...pillsData.splice(i, 2));
        return emptyParenSets.concat(findAllEmptyParens(pillsData));
      }
    }
  }
  return emptyParenSets;
}

export const isEmptyParenSetAt = (arr, i) => {
  const op = arr[i];
  const cp = arr[i + 1];
  return op && op.type === OPEN_PAREN && cp && cp.type === CLOSE_PAREN;
};

// export const removeEmptyParens = (pillsData) => {
//   for (let i = 0; i < pillsData.length; i++) {
//     if (isEmptyParenSetAt(pillsData, i)) {
//       if (pillsData.length === 2) {
//         return [];
//       } else {
//         const newPillsData = [
//           ...pillsData.slice(0, i),
//           ...pillsData.slice(i + 2)
//         ];
//         return removeEmptyParens(newPillsData);
//       }
//     }
//   }
//   return pillsData;
// };

export {
  buildMetaValueStreamInputs,
  executeMetaValuesRequest,
  filterIsPresent,
  findAllEmptyParens,
  findEmptyParensAtPosition,
  hasEmptyParensAt,
  parseBasicQueryParams,
  selectPillsFromPosition
};
