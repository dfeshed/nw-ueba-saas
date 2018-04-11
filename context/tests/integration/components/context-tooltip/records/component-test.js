import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import { next, later } from '@ember/runloop';
import { waitForSockets } from '../../../../helpers/wait-for-sockets';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const model = { type: 'IP', id: '10.20.30.40' };

module('Integration | Component | context tooltip records', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'context', 'service:context');
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    assert.expect(4);
    const done = assert.async();
    const revert = waitForSockets();
    this.set('model', model);
    await render(hbs`{{context-tooltip/records model=model}}`);
    assert.equal(this.$('.rsa-context-tooltip-records').length, 1);
    // Using `next()` gives the component time to kick off its data fetch.
    next(() => {
      // Wait long enough for some data to start streaming in before checking for data in the DOM.
      later(() => {
        assert.ok(this.$('.rsa-context-tooltip-records__record').length, 'Expected to find one or more records in the DOM');
        assert.ok(this.$('.rsa-context-tooltip-records__record .value').text().trim(), 'Expected to find record value');
        assert.ok(this.$('.rsa-context-tooltip-records__record .text').text().trim(), 'Expected to find record name');
        return wait().then(() => {
          revert();
          done();
        });
      }, 1000);
    });
  });
});
