import { module, test } from 'qunit';
import { later } from '@ember/runloop';
import { setupApplicationTest } from 'ember-qunit';
import { visit } from '@ember/test-helpers';

module('Acceptance | Request | disconnect during stomp connection', function(hooks) {
  setupApplicationTest(hooks);

  test('disconnecting all shouldnt throw when (stomp) client is still connecting', async function(assert) {
    assert.expect(1);

    await visit('/');
    const request = this.owner.lookup('service:request');

    const requestConfig = {
      method: 'promise/_6',
      modelName: 'test',
      query: {}
    };

    later(() => {
      request.disconnectAll();
    }, 1);

    try {
      await request.promiseRequest(requestConfig);
      assert.ok(true, 'promise should still resolve');
    } catch (err) {
      assert.ok(false, 'promise should not throw');
    }
  });

});