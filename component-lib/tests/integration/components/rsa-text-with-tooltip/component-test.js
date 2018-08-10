import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, triggerEvent } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const FIX_ELEMENT_ID = 'tether_fix_style_element';

function insertTetherFix() {
  const styleElement = document.createElement('style');
  styleElement.id = FIX_ELEMENT_ID;
  styleElement.innerText =
    '#ember-testing-container, #ember-testing-container * {' +
      'position: static !important;' +
    '}';

  document.body.appendChild(styleElement);
}

function removeTetherFix() {
  const styleElement = document.getElementById(FIX_ELEMENT_ID);
  document.body.removeChild(styleElement);
}

module('Integration | Component | rsa-text-with-tooltip', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    insertTetherFix();
    this.owner.inject('component', 'i18n', 'service:i18n');
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    removeTetherFix();
  });

  test('it renders the tooltip-text', async function(assert) {
    await render(hbs`{{rsa-text-with-tooltip}}`);
    assert.equal(findAll('.tooltip-text').length, 1, 'Expected to render the tooltip text content');
  });

  test('it renders the tooltip on mouse enter', async function(assert) {
    assert.expect(3);
    this.set('value', 'test value 123123 123123 123123 123123 123123');
    this.set('tipPosition', 'top');
    await render(hbs`{{rsa-text-with-tooltip value=value tipPosition=tipPosition}}`);
    document.querySelector('.tooltip-text').setAttribute('style', 'width:100px');
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(find('.ember-tether .tool-tip-value').textContent.trim(), 'test value 123123 123123 123123 123123 123123');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });
});
