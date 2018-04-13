const localeRoot = 'errorDictionaryMessages.investigateEvents';
const genericMsgKey = `${localeRoot}.GENERIC`;

const investigateEventsErrorCodeDictionary = {
  // common codes
  11: { type: 'INVALID_SYNTAX', messageLocaleKey: null, logInConsole: false, sendServerMessage: true },
  13: { type: 'MISCONFIGURED_SERVICE_CERTIFICATE', messageLocaleKey: `${localeRoot}.MISCONFIGURED_SERVICE_CERTIFICATE`, logInConsole: false, sendServerMessage: false },
  110: { type: 'ACCESS_DENIED', messageLocaleKey: `${localeRoot}.ACCESS_DENIED`, logInConsole: false, sendServerMessage: false },
  119: { type: 'LENGTH_EXCEEDED', messageLocaleKey: null, logInConsole: false, sendServerMessage: true },
  129: { type: 'SESSION_REMOVED', messageLocaleKey: `${localeRoot}.SESSION_REMOVED`, logInConsole: false, sendServerMessage: false },
  130: { type: 'PACKETS_NOT_FOUND', messageLocaleKey: `${localeRoot}.PACKETS_NOT_FOUND`, logInConsole: false, sendServerMessage: false },
  // uncommon codes
  12: { type: 'MISSING_SECURITY_TOKEN', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  109: { type: 'TIMEOUT', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  111: { type: 'INVALID_PARAMETER', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  112: { type: 'MISSING_PARAMETER', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  113: { type: 'UNKNOWN', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  114: { type: 'EXPECTED_VALUE', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  115: { type: 'ILLEGAL_OPERATION', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  116: { type: 'CONNECTION_FORCIBLY_CLOSED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  117: { type: 'CONNECTION_CLOSED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  118: { type: 'CANCELED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  122: { type: 'ILLEGAL_VALUE', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  123: { type: 'INVALID_CREDENTIALS', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  124: { type: 'SESSION_DOES_NOT_EXIST', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  125: { type: 'SESSION_INCOMPLETE', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  126: { type: 'INVALID_FORMAT', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  127: { type: 'CACHE_MISS', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  128: { type: 'LIMIT_EXCEEDED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  131: { type: 'LICENSE_LIMIT_EXCEEDED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  132: { type: 'UNLICENSED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  133: { type: 'REQUEST_QUEUE_FULL', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  135: { type: 'NOT_SUPPORTED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  136: { type: 'OUT_OF_SPACE', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  137: { type: 'TRANSMIT_QUEUE_FULL', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  138: { type: 'DECRYPTION_FAILED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  139: { type: 'DATA_CORRUPTED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  200: { type: 'METHOD_NOT_ALLOWED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  201: { type: 'NOT_ACCEPTABLE', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  202: { type: 'UNAUTHORIZED', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false },
  203: { type: 'INTERNAL_SERVER_ERROR', messageLocaleKey: genericMsgKey, logInConsole: true, sendServerMessage: false }
};

function _parseAndLogErrorCode(response, errorCodeDictionary) {
  const errorObject = errorCodeDictionary[response.errorCode];
  const message = response['unhandled-error'] || response.message || '';

  if (errorObject.logInConsole) {
    console.warn('----- ERROR RECEIVED -----'); // eslint-disable-line no-console
    console.warn('ERROR TYPE: ', errorObject.type); // eslint-disable-line no-console
    console.warn('ERROR CODE: ', response.errorCode); // eslint-disable-line no-console

    // response['unhandled-error'] and response.message are both possible on the response obj
    if (message != undefined && message != null && message != '') {
      console.warn('ERROR MESSAGE: ', message); // eslint-disable-line no-console
    }

    console.warn('--------------------------'); // eslint-disable-line no-console
  }

  return { ...errorObject, errorCode: response.errorCode, serverMessage: message };
}

function handleInvestigateErrorCode(response) {
  return _parseAndLogErrorCode(response, investigateEventsErrorCodeDictionary);
}

export {
  investigateEventsErrorCodeDictionary,
  handleInvestigateErrorCode
};
