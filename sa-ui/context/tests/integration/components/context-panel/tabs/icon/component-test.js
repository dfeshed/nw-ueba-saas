import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | context panel/tabs/icon', function(hooks) {
  setupRenderingTest(hooks);

  test('derivedClassName enabled & not disabled if loadingIcon + toolTipText are both undefined', async function(assert) {
    this.set('tab', {});
    await render(hbs`{{context-panel/tabs/icon tab=tab}}`);

    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 1, 'icon did not have enabled className');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 0, 'icon had disabled className when it should not have');
  });

  test('derivedClassName disabled & not enabled if loadingIcon false + toolTipText truthy', async function(assert) {
    this.set('tab', {
      toolTipText: 'x',
      loadingIcon: false
    });
    await render(hbs`{{context-panel/tabs/icon tab=tab}}`);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 1, 'icon did not have disabled className');
  });

  test('derivedClassName disabled & not enabled if loadingIcon null + toolTipText truthy', async function(assert) {
    this.set('tab', {
      toolTipText: 'x',
      loadingIcon: null
    });
    await render(hbs`{{context-panel/tabs/icon tab=tab}}`);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 1, 'icon did not have disabled className');
  });

  test('derivedClassName disabled & not enabled if loadingIcon undefined + toolTipText truthy', async function(assert) {
    this.set('tab', {
      toolTipText: 'x'
    });
    await render(hbs`{{context-panel/tabs/icon tab=tab}}`);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 1, 'icon did not have disabled className');
  });

  test('derivedClassName not disabled & not enabled if loadingIcon true + toolTipText truthy', async function(assert) {
    this.set('tab', {
      toolTipText: 'x',
      loadingIcon: true
    });
    await render(hbs`{{context-panel/tabs/icon tab=tab}}`);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 0, 'icon had disabled className when it should not have');
  });

  test('derivedClassName not disabled & not enabled if loadingIcon true + toolTipText undefined', async function(assert) {
    this.set('tab', {
      loadingIcon: true
    });
    this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 0, 'icon had disabled className when it should not have');
  });

  test('derivedClassName recomputed when loadingIcon & toolTipText change', async function(assert) {
    this.set('tab', {
    });
    await render(hbs`{{context-panel/tabs/icon tab=tab}}`);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 1, 'icon did not have enabled className');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 0, 'icon had disabled className when it should not have');

    this.set('tab.toolTipText', 'x');
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 1, 'icon did not have disabled className');

    this.set('tab.loadingIcon', true);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 0, 'icon had disabled className when it should not have');

    this.set('tab.loadingIcon', false);
    assert.equal(findAll('div .rsa-context-panel__tabs__enabled').length, 0, 'icon had enabled className when it should not have');
    assert.equal(findAll('div .rsa-context-panel__tabs__disabled').length, 1, 'icon did not have disabled className');
  });
});
