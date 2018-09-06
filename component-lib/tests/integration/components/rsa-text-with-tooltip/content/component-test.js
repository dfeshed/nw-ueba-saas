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

  test('it renders the content-note when showNote is set true', async function(assert) {
    this.set('showNote', true);
    await render(hbs`{{rsa-text-with-tooltip/content showNote=showNote text='test-value'}}`);
    assert.ok(find('.tool-tip-value .tool-tip-note').textContent.indexOf('Note') > 0);
  });

  test('it do not renders the tooltip when showNote is set false', async function(assert) {
    this.set('value', 'test-value');
    this.set('showNote', false);
    await render(hbs`{{rsa-text-with-tooltip/content text=value showNote=showNote}}`);
    assert.equal(findAll('.tool-tip-value').length, 1, 'Expected to show text');
    assert.equal(findAll('.tool-tip-note').length, 0, 'Expected not to show note tooltip');
  });

});
