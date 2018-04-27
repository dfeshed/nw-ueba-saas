import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../../../helpers/engine-resolver';
import { find, findAll, render } from '@ember/test-helpers';

module('Integration | Component | host-detail/base-property-panel/host-text/content', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders the content ', async function(assert) {
    await render(hbs`{{host-detail/base-property-panel/host-text/content}}`);
    assert.equal(findAll('.tool-tip-value').length, 1, 'Expected to render the host text content');
  });

  test('it renders the content value', async function(assert) {
    this.set('value', 'XYZ');
    await render(hbs`{{host-detail/base-property-panel/host-text/content text=value}}`);
    assert.equal(find('.tool-tip-value').textContent.trim(), 'XYZ');
  });


  test('it renders the SIZE content', async function(assert) {
    this.set('format', 'SIZE');
    this.set('value', '1024');
    await render(hbs`{{host-detail/base-property-panel/host-text/content format=format text=value}}`);
    assert.equal(find('.tool-tip-value .units').textContent.trim(), 'KB');
  });

  test('it renders the host-text SIGNATURE content', async function(assert) {
    this.set('format', 'SIGNATURE');
    this.set('value', null);
    await render(hbs`{{host-detail/base-property-panel/host-text/content format=format text=value}}`);
    assert.equal(find('.tool-tip-value').textContent.trim(), 'unsigned');
  });

});