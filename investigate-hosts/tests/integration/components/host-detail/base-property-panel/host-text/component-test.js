import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../../helpers/engine-resolver';
import { find, findAll, render, triggerEvent } from '@ember/test-helpers';

module('Integration | Component | host-detail/base-property-panel/host-text', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders the host-text', async function(assert) {
    await render(hbs`{{host-detail/base-property-panel/host-text}}`);
    assert.equal(findAll('.host-text').length, 1, 'Expected to render the host text content');
  });

  test('it renders the host-text SIZE content', async function(assert) {
    this.set('format', 'SIZE');
    this.set('value', '1024');
    await render(hbs`{{host-detail/base-property-panel/host-text format=format value=value}}`);
    assert.equal(find('.host-text .units').textContent.trim(), 'KB');
  });

  test('it renders the host-text HEX content', async function(assert) {
    this.set('format', 'HEX');
    this.set('value', '16');
    await render(hbs`{{host-detail/base-property-panel/host-text format=format value=value}}`);
    assert.equal(find('.host-text').textContent.trim(), '0x10');
  });

  test('it renders the host-text SIGNATURE content', async function(assert) {
    this.set('format', 'SIGNATURE');
    this.set('value', null);
    await render(hbs`{{host-detail/base-property-panel/host-text format=format value=value}}`);
    assert.equal(find('.host-text').textContent.trim(), 'unsigned');
  });

  test('it renders the tooltip on mouse enter', async function(assert) {
    assert.expect(3);
    this.set('value', 'test value 123123 123123 123123 123123 123123');
    this.set('tipPosition', 'top');
    await render(hbs`{{host-detail/base-property-panel/host-text format=format value=value tipPosition=tipPosition}}`);
    document.querySelector('.host-text').setAttribute('style', 'width:100px');
    await triggerEvent('.host-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(find('.ember-tether .tool-tip-value').textContent.trim(), 'test value 123123 123123 123123 123123 123123');
    await triggerEvent('.host-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');

  });
});