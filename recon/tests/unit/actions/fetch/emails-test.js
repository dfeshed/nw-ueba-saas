import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

import { fetchEmailData } from 'recon/actions/fetch/emails';

module('Unit | API | emails', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('test fetchEmailData', async function(assert) {
    const done = assert.async();

    // 2 responses expected
    assert.expect(2);

    let responseCount = 0;
    const dispatchData = (response) => {
      assert.ok(response, `response ${++responseCount} received`);
      if (response.meta.complete) {
        done();
      }
    };
    fetchEmailData({ endpointId: 2, eventId: 2, email: true }, dispatchData, () => {}, () => {});
  });
});
