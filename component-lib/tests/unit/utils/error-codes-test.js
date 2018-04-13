import { module, test } from 'qunit';

import { investigateEventsErrorCodeDictionary, handleInvestigateErrorCode } from 'component-lib/utils/error-codes';

module('Unit | Helper | Error Codes');

test('investigateEventsErrorCodeDictionary has correct entries', function(assert) {
  const codes = investigateEventsErrorCodeDictionary;

  assert.equal(Object.keys(codes).length, 35);

  assert.equal(codes[11].type, 'INVALID_SYNTAX');
  assert.equal(codes[11].messageLocaleKey, null);
  assert.equal(codes[11].logInConsole, false);
  assert.equal(codes[11].sendServerMessage, true);

  assert.equal(codes[13].type, 'MISCONFIGURED_SERVICE_CERTIFICATE');
  assert.equal(codes[13].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.MISCONFIGURED_SERVICE_CERTIFICATE');
  assert.equal(codes[13].logInConsole, false);
  assert.equal(codes[13].sendServerMessage, false);

  assert.equal(codes[110].type, 'ACCESS_DENIED');
  assert.equal(codes[110].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.ACCESS_DENIED');
  assert.equal(codes[110].logInConsole, false);
  assert.equal(codes[110].sendServerMessage, false);

  assert.equal(codes[119].type, 'LENGTH_EXCEEDED');
  assert.equal(codes[119].messageLocaleKey, null);
  assert.equal(codes[119].logInConsole, false);
  assert.equal(codes[119].sendServerMessage, true);

  assert.equal(codes[129].type, 'SESSION_REMOVED');
  assert.equal(codes[129].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.SESSION_REMOVED');
  assert.equal(codes[129].logInConsole, false);
  assert.equal(codes[129].sendServerMessage, false);

  assert.equal(codes[130].type, 'PACKETS_NOT_FOUND');
  assert.equal(codes[130].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.PACKETS_NOT_FOUND');
  assert.equal(codes[130].logInConsole, false);
  assert.equal(codes[130].sendServerMessage, false);

  assert.equal(codes[12].type, 'MISSING_SECURITY_TOKEN');
  assert.equal(codes[12].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[12].logInConsole, true);
  assert.equal(codes[12].sendServerMessage, false);

  assert.equal(codes[109].type, 'TIMEOUT');
  assert.equal(codes[109].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[109].logInConsole, true);
  assert.equal(codes[109].sendServerMessage, false);

  assert.equal(codes[111].type, 'INVALID_PARAMETER');
  assert.equal(codes[111].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[111].logInConsole, true);
  assert.equal(codes[111].sendServerMessage, false);

  assert.equal(codes[112].type, 'MISSING_PARAMETER');
  assert.equal(codes[112].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[112].logInConsole, true);
  assert.equal(codes[112].sendServerMessage, false);

  assert.equal(codes[113].type, 'UNKNOWN');
  assert.equal(codes[113].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[113].logInConsole, true);
  assert.equal(codes[113].sendServerMessage, false);

  assert.equal(codes[114].type, 'EXPECTED_VALUE');
  assert.equal(codes[114].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[114].logInConsole, true);
  assert.equal(codes[114].sendServerMessage, false);

  assert.equal(codes[115].type, 'ILLEGAL_OPERATION');
  assert.equal(codes[115].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[115].logInConsole, true);
  assert.equal(codes[115].sendServerMessage, false);

  assert.equal(codes[116].type, 'CONNECTION_FORCIBLY_CLOSED');
  assert.equal(codes[116].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[116].logInConsole, true);
  assert.equal(codes[116].sendServerMessage, false);

  assert.equal(codes[117].type, 'CONNECTION_CLOSED');
  assert.equal(codes[117].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[117].logInConsole, true);
  assert.equal(codes[117].sendServerMessage, false);

  assert.equal(codes[118].type, 'CANCELED');
  assert.equal(codes[118].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[118].logInConsole, true);
  assert.equal(codes[118].sendServerMessage, false);

  assert.equal(codes[122].type, 'ILLEGAL_VALUE');
  assert.equal(codes[122].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[122].logInConsole, true);
  assert.equal(codes[122].sendServerMessage, false);

  assert.equal(codes[123].type, 'INVALID_CREDENTIALS');
  assert.equal(codes[123].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[123].logInConsole, true);
  assert.equal(codes[123].sendServerMessage, false);

  assert.equal(codes[124].type, 'SESSION_DOES_NOT_EXIST');
  assert.equal(codes[124].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[124].logInConsole, true);
  assert.equal(codes[124].sendServerMessage, false);

  assert.equal(codes[125].type, 'SESSION_INCOMPLETE');
  assert.equal(codes[125].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[125].logInConsole, true);
  assert.equal(codes[125].sendServerMessage, false);

  assert.equal(codes[126].type, 'INVALID_FORMAT');
  assert.equal(codes[126].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[126].logInConsole, true);
  assert.equal(codes[126].sendServerMessage, false);

  assert.equal(codes[127].type, 'CACHE_MISS');
  assert.equal(codes[127].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[127].logInConsole, true);
  assert.equal(codes[127].sendServerMessage, false);

  assert.equal(codes[128].type, 'LIMIT_EXCEEDED');
  assert.equal(codes[128].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[128].logInConsole, true);
  assert.equal(codes[128].sendServerMessage, false);

  assert.equal(codes[131].type, 'LICENSE_LIMIT_EXCEEDED');
  assert.equal(codes[131].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[131].logInConsole, true);
  assert.equal(codes[131].sendServerMessage, false);

  assert.equal(codes[132].type, 'UNLICENSED');
  assert.equal(codes[132].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[132].logInConsole, true);
  assert.equal(codes[132].sendServerMessage, false);

  assert.equal(codes[133].type, 'REQUEST_QUEUE_FULL');
  assert.equal(codes[133].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[133].logInConsole, true);
  assert.equal(codes[133].sendServerMessage, false);

  assert.equal(codes[135].type, 'NOT_SUPPORTED');
  assert.equal(codes[135].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[135].logInConsole, true);
  assert.equal(codes[135].sendServerMessage, false);

  assert.equal(codes[136].type, 'OUT_OF_SPACE');
  assert.equal(codes[136].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[136].logInConsole, true);
  assert.equal(codes[136].sendServerMessage, false);

  assert.equal(codes[137].type, 'TRANSMIT_QUEUE_FULL');
  assert.equal(codes[137].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[137].logInConsole, true);
  assert.equal(codes[137].sendServerMessage, false);

  assert.equal(codes[138].type, 'DECRYPTION_FAILED');
  assert.equal(codes[138].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[138].logInConsole, true);
  assert.equal(codes[138].sendServerMessage, false);

  assert.equal(codes[139].type, 'DATA_CORRUPTED');
  assert.equal(codes[139].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[139].logInConsole, true);
  assert.equal(codes[139].sendServerMessage, false);

  assert.equal(codes[200].type, 'METHOD_NOT_ALLOWED');
  assert.equal(codes[200].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[200].logInConsole, true);
  assert.equal(codes[200].sendServerMessage, false);

  assert.equal(codes[201].type, 'NOT_ACCEPTABLE');
  assert.equal(codes[201].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[201].logInConsole, true);
  assert.equal(codes[201].sendServerMessage, false);

  assert.equal(codes[202].type, 'UNAUTHORIZED');
  assert.equal(codes[202].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[202].logInConsole, true);
  assert.equal(codes[202].sendServerMessage, false);

  assert.equal(codes[203].type, 'INTERNAL_SERVER_ERROR');
  assert.equal(codes[203].messageLocaleKey, 'errorDictionaryMessages.investigateEvents.GENERIC');
  assert.equal(codes[203].logInConsole, true);
  assert.equal(codes[203].sendServerMessage, false);
});

test('handleInvestigateErrorCode returns the correct error object', function(assert) {
  [
    11, 119, 13, 110, 129, 130, 12, 109, 111, 112, 113, 114, 115,
    116, 117, 118, 122, 123, 124, 125, 126, 127, 128, 131, 132,
    133, 135, 136, 137, 138, 139, 200, 201, 202, 203
  ].forEach((errorCode) => {
    const message = 'Server message.';
    const errorObject = handleInvestigateErrorCode({ errorCode, message });

    const keys = Object.keys(errorObject);

    assert.equal(keys.length, 6);
    assert.equal(errorObject.serverMessage, message);
    assert.equal(errorObject.errorCode, errorCode);
    assert.ok(keys.includes('errorCode'));
    assert.ok(keys.includes('type'));
    assert.ok(keys.includes('messageLocaleKey'));
    assert.ok(keys.includes('logInConsole'));
    assert.ok(keys.includes('sendServerMessage'));
    assert.ok(keys.includes('serverMessage'));
  });
});
