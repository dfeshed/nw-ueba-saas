import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { waitUntil, render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Service from '@ember/service';
import rsvp from 'rsvp';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const timeout = 15000;
const contextStub = Service.extend({
  metas: () => {
    return new rsvp.Promise((resolve) => resolve({}));
  }
});

module('Integration | Component | context-panel', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:context', contextStub);
  });

  test('Test header for context panel should display when loading and afterword', async function(assert) {
    this.set('entityId', '1.1.1.1');
    this.set('entityType', 'IP');
    await render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

    assert.equal(findAll('[test-id=contextPanelHeader]').length, 1);

    await waitUntil(() => {
      const tabGroup = findAll('.rsa-nav-tab-group');
      return tabGroup && tabGroup.length === 1;
    }, { timeout });

    assert.equal(findAll('.rsa-nav-tab').length, 6, 'We should get all 5 data sources for meta ip');
    assert.equal(findAll('[test-id=contextPanelHeader]').length, 1);
  });

  test('Test context panel should display', async function(assert) {
    this.set('entityId', '1.1.1.1');
    this.set('entityType', 'IP');
    await render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

    await waitUntil(() => {
      const tabGroup = findAll('.rsa-nav-tab-group');
      return tabGroup && tabGroup.length === 1;
    }, { timeout });

    assert.equal(findAll('.rsa-nav-tab').length, 6, 'We should get all 5 data sources for meta ip');
  });

  test('Test context panel should not close on clicking other tab', async function(assert) {
    this.set('entityId', '1.1.1.1');
    this.set('entityType', 'IP');
    await render(hbs`{{context-panel entityId=entityId entityType=entityType }}`);

    await waitUntil(() => {
      const tabGroup = findAll('.rsa-nav-tab-group');
      return tabGroup && tabGroup.length === 1;
    }, { timeout });

    assert.equal(findAll('.rsa-nav-tab').length, 6, 'Should render all 6 data sources for meta ip');
    await click('.rsa-icon-flag-square-2-filled');
    assert.equal(findAll('.rsa-nav-tab').length, 6, 'Should not close panel onclicking another data source');
    await click('.rsa-icon-alarm-sound-filled');
    assert.equal(findAll('.rsa-nav-tab').length, 6, 'Should not close panel onclicking another data source');
  });

  test('Test context panel should display for File hash', async function(assert) {
    this.set('entityId', '1.1.1.1.');
    this.set('entityType', 'FILE_HASH');
    await render(hbs `{{context-panel entityId=entityId entityType=entityType}}`);

    await waitUntil(() => {
      const tabGroup = findAll('.rsa-nav-tab-group');
      return tabGroup && tabGroup.length === 1;
    }, { timeout });

    assert.equal(findAll('.rsa-nav-tab').length, 5, 'We should get 5 data sources for File Hash');
    assert.equal(findAll('.tabsFileReputationServer').length, 1, 'File Reputation datasource tab should exists.');
  });
});
