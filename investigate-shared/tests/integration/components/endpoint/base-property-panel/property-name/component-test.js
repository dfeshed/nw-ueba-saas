import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';

module('Integration | Component | endpoint/base-property-panel/property-name', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders the property name', async function(assert) {
    const field = {
      displayName: 'test'
    };
    this.set('field', field);
    await render(hbs`{{endpoint/base-property-panel/property-name property=field}}`);
    assert.equal(findAll('.property-name').length, 1, 'Expected to render the property name');
    assert.equal(findAll('.property-name')[0].innerText.trim(), 'test', 'display name is test');
  });
});