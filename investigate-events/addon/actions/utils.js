import { assert } from '@ember/debug';
import { getProperties } from '@ember/object';
import { isBlank } from '@ember/utils';
import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';

import { encodeMetaFilterConditions, addSessionIdFilter } from 'investigate-shared/actions/api/events/utils';
import { getTimeRangeIdFromRange } from 'investigate-shared/utils/time-range-utils';

const _isFloat = (value) => {
  return value.includes('.') && (value - value === 0);
};

const _isIPv4 = (value) => {
  return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(value);
};

const _isIPv6 = (value) => {
  return /^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/.test(value);
};

const _isInt = (value) => {
  return /^[0-9]\d*$/.test(value);
};

const _isMac = (value) => {
  return /^(?:[0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$/g.test(value);
};

const VALIDATORS = {
  TimeT: {
    isValid(value) {
      return new Date(value) != 'Invalid Date';
    },
    i18nString: 'queryBuilder.validationMessages.time'
  },
  Text: {
    isValid(value) {
      return value.slice(0) != '\'' || value.slice(-1) != '\'';
    },
    i18nString: 'queryBuilder.validationMessages.text'
  },
  IPv4: {
    isValid(value) {
      return _isIPv4(value);
    },
    i18nString: 'queryBuilder.validationMessages.ipv4'
  },
  IPv6: {
    isValid(value) {
      return _isIPv6(value);
    },
    i18nString: 'queryBuilder.validationMessages.ipv6'
  },
  UInt8: {
    isValid(value) {
      return _isInt(value);
    },
    i18nString: 'queryBuilder.validationMessages.uint8'
  },
  UInt16: {
    isValid(value) {
      return _isInt(value);
    },
    i18nString: 'queryBuilder.validationMessages.uint16'
  },
  UInt32: {
    isValid(value) {
      return _isInt(value);
    },
    i18nString: 'queryBuilder.validationMessages.uint32'
  },
  UInt64: {
    isValid(value) {
      return _isInt(value);
    },
    i18nString: 'queryBuilder.validationMessages.uint64'
  },
  Float32: {
    isValid(value) {
      return _isFloat(value);
    },
    i18nString: 'queryBuilder.validationMessages.float32'
  },
  MAC: {
    isValid(value) {
      return _isMac(value);
    },
    i18nString: 'queryBuilder.validationMessages.mac'
  }
};

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

const clientSideParseAndValidate = (format, value) => {
  return new RSVP.Promise((resolve, reject) => {
    let validationError;
    const validator = VALIDATORS[format];
    // if validator object is not found, move on
    if (validator && !validator.isValid(value)) {
      const i18n = lookup('service:i18n');
      validationError = i18n.t(validator.i18nString);
      reject({ meta: validationError });
    } else {
      resolve();
    }
  });
};

const getMetaFormat = (meta, languages) => {
  const metaObject = languages.findBy('metaName', meta);
  return metaObject.format;
};

const selectPillsFromPosition = (pills, position, direction) => {
  let newPills = [];
  if (direction === 'right') {
    newPills = pills.filter((pill) => pills.indexOf(pill) >= position);
  } else if (direction === 'left') {
    newPills = pills.filter((pill) => pills.indexOf(pill) <= position);
  }
  return newPills;
};

export {
  buildMetaValueStreamInputs,
  clientSideParseAndValidate,
  executeMetaValuesRequest,
  filterIsPresent,
  getMetaFormat,
  parseBasicQueryParams,
  selectPillsFromPosition
};
