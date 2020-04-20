import { find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa content hex', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('should display blank', async function(assert) {
    this.set('value', '');
    await render(hbs`{{rsa-content-hex value=value}}`);
    assert.equal(find('*').textContent.trim(), '');
  });

  test('should display 0', async function(assert) {
    this.set('value', 0);
    await render(hbs`{{rsa-content-hex value=value}}`);
    assert.equal(find('*').textContent.trim(), 0);
  });

  test('should convert into hex for positive decimal', async function(assert) {
    this.set('value', 35176284735872);
    await render(hbs`{{rsa-content-hex value=value}}`);
    assert.equal(find('*').textContent.trim(), '0x1FFE1DF4C980');
  });

  test('should convert into hex for negative decimal', async function(assert) {
    this.set('value', -35176284735872);
    await render(hbs`{{rsa-content-hex value=value}}`);
    assert.equal(find('*').textContent.trim(), '-0x1FFE1DF4C980');
  });
});
