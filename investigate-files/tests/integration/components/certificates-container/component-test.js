import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | certificates-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('container for certificates render', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-container').length, 1, 'certificates container has rendered.');
  });

  test('action bar is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-action-bar').length, 1, 'certificates action bar has rendered.');
  });

  test('certificates body is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-body').length, 1, 'certificates body has rendered.');
  });

  test('certificates footer is rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.certificates-footer').length, 1, 'certificates footer has rendered.');
  });
  test('certificates filter panel rendered', async function(assert) {
    await render(hbs`{{certificates-container}}`);
    assert.equal(findAll('.filter-wrapper').length, 1, 'certificates filter panel has rendered.');
  });
});
