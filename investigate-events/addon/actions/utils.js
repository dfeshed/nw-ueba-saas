import { assert } from '@ember/debug';
import { getProperties } from '@ember/object';
import { isBlank } from '@ember/utils';
import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import { lookup } from 'ember-dependency-lookup';

import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import { getTimeRangeIdFromRange } from 'investigate-shared/utils/time-range-utils';
import { relevantOperators } from 'investigate-events/util/possible-operators';


const operators = ['!exists', 'exists', 'contains', 'begins', 'ends', '<=', '>=', '!=', '='];
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

const complexOperators = ['||', '&&', '(', ')', 'length', 'regex'];
/**
 * Adds a session id filter to a given Core query filter. Appends a condition
 * for session id, but only if a session id is given.
 * @param {string} filter - A Core filter condition (typically for meta keys
 * other than 'sessionid')
 * @param {string} [startSessionId] - Lower bound (exclusive) for session IDs
 * @return {string} A sessionId filter
 * @private
 */
function _addSessionIdFilter(filter, startSessionId) {
  const out = [];
  if (startSessionId) {
    out.push(`(sessionid > ${startSessionId})`);
  }
  if (filter) {
    out.push(filter);
  }
  return out.join(' && ');
}

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
  metaFilterInput.value = _addSessionIdFilter(metaFilterInput.value, startSessionId);
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
  return {
    endTime: params.et,
    sessionId: params.eid,
    metaPanelSize: params.mps,
    reconSize: params.rs,
    serviceId: params.sid,
    startTime: params.st,
    selectedTimeRangeId: getTimeRangeIdFromRange(params.st, params.et)
  };
}

/**
 * Parses a given URI string component that represents 0, 1 or more metaFilters
 * for a Core query. Assumes the URI is of the following syntax:
 * `key1 operator1 value1/key2 operator1 value2/../keyN operatorN valueN`,
 * where each `key#` string is a meta key identifier (e.g., `ip.src`, not a
 * display name), each operator is a logical operator (e.g. =, !=, ends), and
 * each `value#` string is a meta value (raw, not alias). Assumes `key#` strings
 * do not need URI decoding (they're just alphanumerics, plus dots maybe), but
 * `value#` strings and operators will need URI decoding.
 *
 * @param {string} uri
 * @returns {object[]} Array of condition objects. Each array item is an object
 * with properties `key` & `value`, where:
 * (i) `key` is a meta key identifier (e.g., "ip.src", not a display name); and
 * (ii) value` is a meta key value (raw, not alias).
 * @private
 */
function parsePillDataFromUri(uri, availableMeta) {
  if (isBlank(uri)) {
    // When uri is empty, return empty array. Alas, ''.split() returns a non-empty array; it's a 1-item array with
    // an empty string in it, which is not what we want.  So we check for '' and return [] explicitly here.
    return [];
  }
  return uri.split('/')
    .filter((segment) => !!segment)
    .map((queryString) => {
      const decodedQuery = decodeURIComponent(queryString);
      return transformTextToPillData(decodedQuery, availableMeta);
    });
}

const _createComplexFilterText = (complexFilterText) => ({
  meta: undefined,
  operator: undefined,
  value: undefined,
  complexFilterText
});

function transformTextToPillData(queryText, availableMeta) {

  // Nuke any surrounding white space
  queryText = queryText.trim();

  // 1. Check if the text contains characters
  // that immediately make the query complex
  const hasComplexItem = complexOperators.some((operator) => queryText.includes(operator));
  if (hasComplexItem) {
    if (!(queryText.startsWith('(') && queryText.endsWith(')'))) {
      queryText = `(${queryText})`;
    }

    return _createComplexFilterText(queryText);
  }

  // 2. Then check to see if there IS an operator,
  // no operator = complex
  const operator = operators.find((option) => {
    return queryText.includes(option);
  });

  if (!operator) {
    return _createComplexFilterText(queryText);
  }

  // eliminate empty chunks
  const chunks = queryText.split(operator).filter((s) => s !== '');

  let [ meta ] = chunks;
  meta = meta.trim();

  if (availableMeta && availableMeta.length > 0) {
    // 3. Check that the meta is a real meta,
    // if we do not recognize the meta, complex
    const metaConfig = availableMeta.find((m) => m.metaName === meta);
    if (!metaConfig) {
      return _createComplexFilterText(queryText);
    }

    // 4. Check that the operator applies to the meta,
    // if the operator isn't valid for the meta, complex
    const possibleOperators = relevantOperators(metaConfig);
    const operatorConfig = possibleOperators.find((o) => o.displayName === operator);
    if (!operatorConfig) {
      return _createComplexFilterText(queryText);
    }

    // 5. If the operator requires value and doesn't have one,
    // then complex
    // chunks are split by operator, so "medium = 1" would be
    // two chunks
    if (chunks.length < 2 && operatorConfig.hasValue) {
      return _createComplexFilterText(queryText);
    }

    // 6. if the operator does not have a value but a value is
    // include, then complex
    if (chunks.length >= 2 && !operatorConfig.hasValue) {
      return _createComplexFilterText(queryText);
    }
  }

  // NOT COMPLEX!

  let value;
  if (chunks.length > 2) {
    [ , ...value ] = chunks;
    value = value.join(operator).trim();
  } else {
    [ , value ] = chunks;
    // empty means it isn't there
    value = (!value || value.trim() === '') ? undefined : value.trim();
  }

  return {
    meta,
    operator: operator.trim(),
    value,
    complexFilterText: undefined
  };
}

function filterIsPresent(filters, freeFormText) {
  const currentFilters = encodeMetaFilterConditions(filters).replace(/(&&\s*)*$/g, '').trim();
  return currentFilters === freeFormText.trim();
}

/**
 * Encodes a given list of metaFilters into a URI string component
 * that can be used for routing.
 * The reverse of `parseMetaFilterUri()`.
 * @param {object[]} filters The array of meta filters.
 *   For structure, @see return value of parseMetaFilterUri.
 * @returns {string}
 * @private
 */
function uriEncodeMetaFilters(filters = []) {
  const encodedFilters = filters
    .map((d) => {
      let ret;

      if (d.complexFilterText) {
        ret = d.complexFilterText;
      } else {
        ret = `${(d.meta) ? d.meta.trim() : ''} ${(d.operator) ? d.operator.trim() : ''} ${(d.value) ? d.value.trim() : ''}`;
      }
      return isBlank(ret) ? undefined : encodeURIComponent(ret);
    })
    .filter((d) => !!d)
    .join('/');

  return encodedFilters || undefined;
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
    newPills = pills.filter((pill) => pills.indexOf(pill) > position);
  } else if (direction === 'left') {
    newPills = pills.filter((pill) => pills.indexOf(pill) < position);
  }
  return newPills;
};

export {
  buildMetaValueStreamInputs,
  executeMetaValuesRequest,
  parseBasicQueryParams,
  parsePillDataFromUri,
  uriEncodeMetaFilters,
  transformTextToPillData,
  filterIsPresent,
  clientSideParseAndValidate,
  getMetaFormat,
  selectPillsFromPosition
};
