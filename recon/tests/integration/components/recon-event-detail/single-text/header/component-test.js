import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | recon-meta-content-item', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('endpoint header should be displayed', async function(assert) {
    this.set('isEndpoint', true);
    await render(hbs `{{recon-event-detail/single-text/header isEndpoint=isEndpoint}}`);
    assert.equal(find('.recon-request-response-header .text').textContent.trim(), 'Raw Endpoint');
  });

  test('log header should be displayed', async function(assert) {
    this.set('isLog', true);
    await render(hbs `{{recon-event-detail/single-text/header isLog=isLog}}`);
    assert.equal(find('.recon-request-response-header .text').textContent.trim(), 'Raw Log');
  });

});
