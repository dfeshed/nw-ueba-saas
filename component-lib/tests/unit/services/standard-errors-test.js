import { module, test } from 'ember-qunit';

module('service:standard-errors', 'Unit | Service | standard-errors', {
  needs: ['service:i18n']
}, function() {
  test('display returns the static localized message sent to flash', function(assert) {
    const service = this.subject();

    assert.expect(2);

    service.set('flashMessages.error', (passedToError) => {
      assert.equal(passedToError, 'Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions. (Error Code 110)');
    });

    const displayedMessage = service.display({
      type: 'ACCESS_DENIED',
      messageLocaleKey: 'errorDictionaryMessages.investigateEvents.ACCESS_DENIED',
      errorCode: 110,
      logInConsole: false,
      sendServerMessage: false,
      serverMessage: undefined
    });

    assert.equal(displayedMessage, 'Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions. (Error Code 110)');
  });

  test('display returns the static localized message sent to flash', function(assert) {
    const service = this.subject();

    assert.expect(2);

    service.set('flashMessages.error', (passedToError) => {
      assert.equal(passedToError, 'Server message. (Error Code 11)');
    });

    const displayedMessage = service.display({
      type: 'INVALID_SYNTAX',
      messageLocaleKey: null,
      errorCode: 11,
      logInConsole: false,
      sendServerMessage: true,
      serverMessage: 'Server message.'
    });

    assert.equal(displayedMessage, 'Server message. (Error Code 11)');
  });
});
