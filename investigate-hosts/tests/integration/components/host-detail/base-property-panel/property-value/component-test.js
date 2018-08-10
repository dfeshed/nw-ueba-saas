import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | host-detail/base-property-panel/property-value', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    initialize(this.owner);
  });

  test('it renders the tooltip-text', async function(assert) {
    await render(hbs`{{host-detail/base-property-panel/property-value}}`);
    assert.equal(findAll('.tooltip-text').length, 1, 'Expected to render the tooltip text content');
  });

  test('it renders the tooltip-text SIZE content', async function(assert) {
    const field = {
      format: 'SIZE',
      value: '1024'
    };
    this.set('field', field);
    await render(hbs`{{host-detail/base-property-panel/property-value property=field}}`);
    assert.equal(find('.tooltip-text .units').textContent.trim(), 'KB');
  });

  test('it renders the tooltip-text HEX content', async function(assert) {
    const field = {
      format: 'HEX',
      value: '16'
    };
    this.set('field', field);
    await render(hbs`{{host-detail/base-property-panel/property-value property=field}}`);
    assert.equal(find('.tooltip-text').textContent.trim(), '0x10');
  });

  test('it renders the tooltip-text SIGNATURE content', async function(assert) {
    const field = {
      format: 'SIGNATURE',
      value: null
    };
    this.set('field', field);
    await render(hbs`{{host-detail/base-property-panel/property-value property=field}}`);
    assert.equal(find('.tooltip-text').textContent.trim(), 'unsigned');
  });

  test('it renders the tooltip on mouse enter', async function(assert) {
    assert.expect(3);
    const field = {
      value: 'test value 123123 123123 123123 123123 123123'
    };
    this.set('field', field);
    await render(hbs`{{host-detail/base-property-panel/property-value property=field}}`);
    document.querySelector('.tooltip-text').setAttribute('style', 'width:100px');
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(find('.ember-tether .tool-tip-value').textContent.trim(), 'test value 123123 123123 123123 123123 123123');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });
});
