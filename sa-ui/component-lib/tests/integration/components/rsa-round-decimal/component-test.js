import { find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-round-decimal', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('should display 199 as 199.00', async function(assert) {
    this.set('value', 199);
    await render(hbs`{{rsa-round-decimal value=value}}`);
    assert.equal(find('*').textContent.trim(), '199.00');
  });

  test('should display 100.53345 as 100.533', async function(assert) {
    this.set('value', 100.533456);
    this.set('digits', 3);
    await render(hbs`{{rsa-round-decimal value=value digits=digits}}`);
    assert.equal(find('*').textContent.trim(), '100.533');
  });

  test('should display 50.2356 as 50.236', async function(assert) {
    this.set('value', 50.2356);
    this.set('digits', 3);
    await render(hbs`{{rsa-round-decimal value=value digits=digits}}`);
    assert.equal(find('*').textContent.trim(), '50.236');
  });
});

