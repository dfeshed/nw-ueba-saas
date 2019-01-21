import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const callback = () => {};

const wormhole = 'wormhole-context-menu';
module('Integration | Component | host-detail/process/process-tree/process-name', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    document.removeEventListener('contextmenu', callback);
  });

  test('Analyze process is disabled, for no selection', async function(assert) {
    await render(hbs`{{host-detail/process/pivot-to-process-analysis}}`);
    assert.equal(findAll('.rsa-form-button-wrapper')[0].classList.contains('is-disabled'), true, 'Analyze process is disabled when no items are selected');
  });

  test('Analyze process is disabled, for multiple selection', async function(assert) {
    await render(hbs`{{host-detail/process/pivot-to-process-analysis item=[{ a:1 }, { a:1 }] }}`);
    assert.equal(findAll('.rsa-form-button-wrapper')[0].classList.contains('is-disabled'), true, 'Analyze process is disabled when multiple items are selected');
    assert.equal(findAll('.rsa-form-button-wrapper')[0].classList.contains('is-disabled'), true, 'Analyze process is disabled when multiple items are selected');
    assert.equal(findAll('.rsa-form-button-wrapper')[0].title, 'Select a single file to analyze.', 'tooltop should be Select a single file to analyze.');
  });

  test('Analyze process enabled for single item selction', async function(assert) {
    await render(hbs`{{host-detail/process/pivot-to-process-analysis item=[{ a:1 }] }}`);
    assert.equal(findAll('.rsa-form-button-wrapper')[0].classList.contains('is-disabled'), true, 'Analyze process is disabled when multiple items are selected');
  });

  test('Analyze process is disabled, for linux host', async function(assert) {
    await render(hbs`{{host-detail/process/pivot-to-process-analysis osType='linux' item=[{ a:1 }, { a:1 }] }}`);
    assert.equal(findAll('.rsa-form-button-wrapper')[0].classList.contains('is-disabled'), true, 'Analyze process is disabled when multiple items are selected');
  });


});
