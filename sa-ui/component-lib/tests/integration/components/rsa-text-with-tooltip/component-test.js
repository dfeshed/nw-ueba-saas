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

  test('it renders the tooltip on displayOnTab', async function(assert) {
    assert.expect(2);
    this.set('value', 'test value 123123 123123 123123 123123 123123');
    this.set('tipPosition', 'top');
    await render(hbs`{{rsa-text-with-tooltip value=value tipPosition=tipPosition displayOnTab=true}}`);
    document.querySelector('.tooltip-text').setAttribute('style', 'width:100px');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered on displayOnTab');
    assert.equal(find('.ember-tether .tool-tip-value').textContent.trim(), 'test value 123123 123123 123123 123123 123123');
  });

  test('it renders the tooltip when alwaysShow is true', async function(assert) {
    assert.expect(3);
    this.set('value', 'test value');
    this.set('alwaysShow', 'true');
    this.set('tipPosition', 'top');
    await render(hbs`{{rsa-text-with-tooltip alwaysShow=alwaysShow value=value tipPosition=tipPosition}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(find('.ember-tether .tool-tip-value').textContent.trim(), 'test value');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });

  test('it renders the highlighted style as default', async function(assert) {
    assert.expect(3);
    this.set('value', 'test value 123123 123123 123123 123123 123123');
    this.set('tipPosition', 'top');
    await render(hbs`{{rsa-text-with-tooltip value=value tipPosition=tipPosition}}`);
    document.querySelector('.tooltip-text').setAttribute('style', 'width:100px');
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(findAll('.ember-tether .highlighted .tool-tip-value').length, 1, 'highlighted style is rendered');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });

  test('it renders the standard style when passed', async function(assert) {
    assert.expect(3);
    this.set('value', 'test value');
    this.set('alwaysShow', 'true');
    this.set('style', 'standard');
    this.set('tipPosition', 'top');
    await render(hbs`{{rsa-text-with-tooltip style=style
      alwaysShow=alwaysShow value=value tipPosition=tipPosition}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal(findAll('.ember-tether .standard .tool-tip-value').length, 1, 'standard style is rendered');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });

  test('it adds any passed tetherClass to the nested tethered-panel container', async function(assert) {
    assert.expect(3);
    this.set('value', 'test value');
    this.set('alwaysShow', 'true');
    this.set('style', 'standard');
    this.set('tipPosition', 'top');
    this.set('tetherClass', 'someTetherClass');
    await render(hbs`{{rsa-text-with-tooltip style=style
      alwaysShow=alwaysShow value=value tipPosition=tipPosition tetherClass=tetherClass}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.someTetherClass.ember-tether').length, 1, 'tethered-panel container rendered with passed tetherClass');
    assert.equal(find('.someTetherClass.ember-tether .tool-tip-value').textContent.trim(), 'test value');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.someTetherClass.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });

  test('it renders copy text icon by default', async function(assert) {
    assert.expect(1);
    this.set('value', 'Test text');
    await render(hbs`{{rsa-text-with-tooltip alwaysShow=true style='standard' value=value}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('i.rsa-icon-copy-1').length, 1, 'Copy text icon rendered by default');
  });

  test('it does not render copy text icon when copyText is false', async function(assert) {
    assert.expect(1);
    this.set('value', 'Test text');
    await render(hbs`{{rsa-text-with-tooltip alwaysShow=true style='standard' value=value copyText=false}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('i.rsa-icon-copy-1').length, 0, 'Copy text icon not rendered when copyText is set to false');
  });
});
