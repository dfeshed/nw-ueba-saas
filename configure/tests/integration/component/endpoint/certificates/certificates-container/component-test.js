import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | certificates-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('configure')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('container for certificates render', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-container').length, 1, 'certificates container has rendered.');
  });

  test('action bar is rendered', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-action-bar').length, 1, 'certificates action bar has rendered.');
  });

  test('certificates body is rendered', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-body').length, 1, 'certificates body has rendered.');
  });

  test('certificates footer is rendered', async function(assert) {
    await render(hbs`{{endpoint/certificates-container}}`);
    assert.equal(findAll('.certificates-footer').length, 1, 'certificates footer has rendered.');
  });
});
