import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Service | standard-errors', function(hooks) {
  setupTest(hooks);

  test('display returns the static localized message sent to flash', function(assert) {
    const service = this.owner.lookup('service:standard-errors');

    assert.expect(2);

    service.set('flashMessages.error', (passedToError) => {
      assert.equal(passedToError.string, 'Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions. code: 110 - ACCESS_DENIED');
    });

    const displayedMessage = service.display({
      type: 'ACCESS_DENIED',
      messageLocaleKey: 'errorDictionaryMessages.investigateEvents.ACCESS_DENIED',
      errorCode: 110,
      logInConsole: false,
      sendServerMessage: false,
      serverMessage: undefined
    });

    assert.equal(displayedMessage.string, 'Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions. code: 110 - ACCESS_DENIED');
  });

  test('display returns the static localized message sent to flash', function(assert) {
    const service = this.owner.lookup('service:standard-errors');

    assert.expect(2);

    service.set('flashMessages.error', (passedToError) => {
      assert.equal(passedToError, 'Server message.');
    });

    const displayedMessage = service.display({
      type: 'INVALID_SYNTAX',
      messageLocaleKey: null,
      errorCode: 11,
      logInConsole: false,
      sendServerMessage: true,
      serverMessage: 'Server message.'
    });

    assert.equal(displayedMessage, 'Server message.');
  });
});
