// Documentation for this util can be found at https://libhq-ro.rsa.lab.emc.com/SA/SAStyle/production/#/demos/app/standard-errors

import { isEmpty } from '@ember/utils';

const localeRoot = 'errorDictionaryMessages.investigateEvents';
const genericMsgKey = `${localeRoot}.GENERIC`;

const investigateEventsErrorCodeDictionary = {
  // common codes
  11: { type: 'INVALID_SYNTAX', messageLocaleKey: null, sendServerMessage: true },
  13: { type: 'MISCONFIGURED_SERVICE_CERTIFICATE', messageLocaleKey: `${localeRoot}.MISCONFIGURED_SERVICE_CERTIFICATE`, sendServerMessage: false },
  110: { type: 'ACCESS_DENIED', messageLocaleKey: `${localeRoot}.ACCESS_DENIED`, sendServerMessage: false },
  119: { type: 'LENGTH_EXCEEDED', messageLocaleKey: null, sendServerMessage: true },
  129: { type: 'SESSION_REMOVED', messageLocaleKey: `${localeRoot}.SESSION_REMOVED`, sendServerMessage: false },
  130: { type: 'PACKETS_NOT_FOUND', messageLocaleKey: `${localeRoot}.PACKETS_NOT_FOUND`, sendServerMessage: false },
  // uncommon codes
  12: { type: 'MISSING_SECURITY_TOKEN', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  109: { type: 'TIMEOUT', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  111: { type: 'INVALID_PARAMETER', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  112: { type: 'MISSING_PARAMETER', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  113: { type: 'UNKNOWN', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  114: { type: 'EXPECTED_VALUE', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  115: { type: 'ILLEGAL_OPERATION', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  116: { type: 'CONNECTION_FORCIBLY_CLOSED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  117: { type: 'CONNECTION_CLOSED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  118: { type: 'CANCELED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  122: { type: 'ILLEGAL_VALUE', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  123: { type: 'INVALID_CREDENTIALS', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  124: { type: 'SESSION_DOES_NOT_EXIST', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  125: { type: 'SESSION_INCOMPLETE', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  126: { type: 'INVALID_FORMAT', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  127: { type: 'CACHE_MISS', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  128: { type: 'LIMIT_EXCEEDED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  131: { type: 'LICENSE_LIMIT_EXCEEDED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  132: { type: 'UNLICENSED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  133: { type: 'REQUEST_QUEUE_FULL', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  135: { type: 'NOT_SUPPORTED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  136: { type: 'OUT_OF_SPACE', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  137: { type: 'TRANSMIT_QUEUE_FULL', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  138: { type: 'DECRYPTION_FAILED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  139: { type: 'DATA_CORRUPTED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  200: { type: 'METHOD_NOT_ALLOWED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  201: { type: 'NOT_ACCEPTABLE', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  202: { type: 'UNAUTHORIZED', messageLocaleKey: genericMsgKey, sendServerMessage: false },
  203: { type: 'INTERNAL_SERVER_ERROR', messageLocaleKey: genericMsgKey, sendServerMessage: false }
};

function _parseAndLogErrorCode(response, errorCodeDictionary, requestName) {
  const errorCode = response.code || response.errorCode || response['error-code'];
  const message = response.meta ? response.meta.message : response['unhandled-error'] || '';
  let errorObject = errorCodeDictionary[errorCode];

  if (isEmpty(errorObject)) {
    errorObject = { type: 'UNHANDLED_ERROR', messageLocaleKey: genericMsgKey, sendServerMessage: false };
  }

  /* eslint-disable no-console */
  console.warn('----- ERROR RECEIVED -----');

  if (requestName) {
    console.warn('REQUEST: ', requestName);
  }

  console.warn('ERROR TYPE: ', errorObject.type);
  console.warn('ERROR CODE: ', errorCode);

  // response['unhandled-error'] and response.message are both possible on the response obj
  if (!isEmpty(message)) {
    console.warn('ERROR MESSAGE: ', message);
  }

  console.warn('--------------------------');
  /* eslint-enable no-console */

  return { ...errorObject, errorCode, serverMessage: message };
}

function handleInvestigateErrorCode(response, requestName) {
  return _parseAndLogErrorCode(response, investigateEventsErrorCodeDictionary, requestName);
}

export {
  investigateEventsErrorCodeDictionary,
  handleInvestigateErrorCode
};
