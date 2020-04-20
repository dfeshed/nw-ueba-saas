import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { blur, find, findAll, focus, render, triggerKeyEvent, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | rsa-form-input-oneway', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway}}`);
    assert.equal(findAll('input').length, 1);
  });

  test('it is a label', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway}}`);
    const labelCount = findAll('label').length === 1;
    assert.equal(labelCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('rsa-form-input'));
  });

  test('sets the value', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway value='foo'}}`);
    const [input] = findAll('input');
    assert.equal(input.value, 'foo');
  });

  test('sets the label', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway label='foo'}}`);
    const label = find('.rsa-form-label').textContent.trim();
    assert.equal(label, 'foo');
  });

  test('sets the errorMessage', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway label='foo' isError=true errorMessage='Bar'}}`);
    const error = find('.input-error').textContent.trim();
    assert.equal(error, 'Bar');
  });

  test('it includes the proper classes when isInline is true', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isInline=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-inline'));
  });

  test('it includes the proper classes when isDisabled is true', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isDisabled=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-disabled'));
  });

  test('it includes the proper classes when isReadOnly is true', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isReadOnly=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-read-only'));
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isError=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-error'));
  });

  test('it includes the proper classes when isSuccess is true', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isSuccess=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-success'));
  });

  test('it renders a placeholder', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway placeholder='foo'}}`);
    const [input] = findAll('input');
    assert.equal(input.getAttribute('placeholder'), 'foo');
  });

  test('Pop tooltip', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway tooltip='foo'}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(), 'foo', 'Tooltip form-input pops with correct text');
  });

  test('it can be disabled', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isDisabled=true}}`);
    const disabledCount = findAll('input[disabled]').length === 1;
    assert.equal(disabledCount, 1);
  });

  test('it is disabled when isReadOnly', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isReadOnly=true}}`);
    const disabledCount = findAll('input[disabled]').length === 1;
    assert.equal(disabledCount, 1);
  });

  test('it does not show the error message if there is no error', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isError=false errorMessage='There was an error'}}`);
    assert.equal(findAll('.input-error').length, 0, 'The error message is not present');
  });

  test('it does show the error message if isError is true', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway isError=true errorMessage='There was an error'}}`);
    assert.equal(findAll('.input-error').length, 1, 'The error message is present');
  });

  test('it takes focus when autofocus is true', async function(assert) {
    await render(hbs `{{rsa-form-input-oneway value='foo' autofocus=true}}`);
    assert.equal(findAll('input[autofocus]').length, 1, 'expect component to be focused');
  });

  test('it calls closure action onEnter', async function(assert) {
    assert.expect(1);
    this.set('onEnter', () => {
      assert.ok(true, 'onEnter() was properly triggered');
    });
    await render(hbs `{{rsa-form-input-oneway onEnter=(action onEnter)}}`);
    const input = find('input');
    await focus(input);
    await triggerKeyEvent(input, 'keyup', 13);
  });

  // not part of component yet...
  // test('it calls closure action onFocusIn', async function(assert) {
  // });

  test('it calls closure action onFocusOut', async function(assert) {
    assert.expect(1);
    this.set('onFocusOut', () => {
      assert.ok(true, 'onFocusOut() was properly triggered');
    });
    await render(hbs `{{rsa-form-input-oneway onFocusOut=(action onFocusOut)}}`);
    const input = find('input');
    await focus(input);
    await blur(input);
  });

  test('it calls closure action onKeyDown', async function(assert) {
    assert.expect(1);
    this.set('onKeyDown', () => {
      assert.ok(true, 'onKeyDown() was properly triggered');
    });
    await render(hbs `{{rsa-form-input-oneway onKeyDown=(action onKeyDown)}}`);
    const input = find('input');
    await focus(input);
    await triggerKeyEvent(input, 'keydown', 65);
  });

  test('it calls closure action onKeyUp', async function(assert) {
    assert.expect(1);
    this.set('onKeyUp', () => {
      assert.ok(true, 'onKeyUp() was properly triggered');
    });
    await render(hbs `{{rsa-form-input-oneway onKeyUp=(action onKeyUp)}}`);
    const input = find('input');
    await focus(input);
    await triggerKeyEvent(input, 'keyup', 65);
  });

});
