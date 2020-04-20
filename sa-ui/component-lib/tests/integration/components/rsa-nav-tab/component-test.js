import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-nav-tab', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-nav-tab}}`);
    const tabCount = findAll('.rsa-nav-tab').length;
    assert.equal(tabCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-nav-tab isActive=true}}`);
    const tabCount = findAll('.rsa-nav-tab.is-active').length;
    assert.equal(tabCount, 1);
  });

  test('it includes the proper classes when isCenterAlignedPrimary', async function(assert) {
    await render(hbs `{{rsa-nav-tab align='center'}}`);
    const tabCount = findAll('.rsa-nav-tab.is-center-aligned-primary').length;
    assert.equal(tabCount, 1);
  });

  test('it includes the proper classes when isLeftAlignedPrimary', async function(assert) {
    await render(hbs `{{rsa-nav-tab align='left'}}`);
    const tabCount = findAll('.rsa-nav-tab.is-left-aligned-primary').length;
    assert.equal(tabCount, 1);
  });

  test('it includes the proper classes when isCenterAlignedSecondary', async function(assert) {
    await render(hbs `{{rsa-nav-tab align='center' compact=true}}`);
    const tabCount = findAll('.rsa-nav-tab.is-center-aligned-secondary').length;
    assert.equal(tabCount, 1);
  });

  test('it includes the proper classes when isLeftAlignedSecondary', async function(assert) {
    await render(hbs `{{rsa-nav-tab align='left' compact=true}}`);
    const tabCount = findAll('.rsa-nav-tab.is-left-aligned-secondary').length;
    assert.equal(tabCount, 1);
  });
});
