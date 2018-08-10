import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';

module('Integration | Component | rsa-text-with-tooltip/content', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders the content ', async function(assert) {
    await render(hbs`{{rsa-text-with-tooltip/content}}`);
    assert.equal(findAll('.tool-tip-value').length, 1, 'Expected to render the tooltip value content');
  });

  test('it renders the content value', async function(assert) {
    this.set('value', 'XYZ');
    await render(hbs`{{rsa-text-with-tooltip/content text=value}}`);
    assert.equal(find('.tool-tip-value').textContent.trim(), 'XYZ');
  });


  test('it renders the SIZE content', async function(assert) {
    this.set('format', 'SIZE');
    this.set('value', '1024');
    await render(hbs`{{rsa-text-with-tooltip/content format=format text=value}}`);
    assert.equal(find('.tool-tip-value .units').textContent.trim(), 'KB');
  });

});
