import { focus, render, findAll, find, triggerKeyEvent, triggerEvent } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-form-textarea-oneway', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.actions = {};
    this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway}}`);
    assert.equal(findAll('textarea').length, 1);
  });

  test('it is a label', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway}}`);
    const labelCount = findAll('label').length;
    assert.equal(labelCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway}}`);
    const label = find('label');
    assert.ok(label.classList.contains('rsa-form-textarea'));
  });

  test('sets the value', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway value='foo'}}`);
    const textarea = find('textarea');
    assert.ok(textarea.value = 'foo');
  });

  test('sets the label', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway label='foo'}}`);
    const label = find('.rsa-form-label');
    assert.equal(label.textContent.trim(), 'foo');
  });

  test('sets the errorMessage', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway label='foo' isError=true errorMessage='Bar'}}`);
    const error = find('.input-error').textContent.trim();
    assert.equal(error, 'Bar');
  });

  test('it includes the proper classes when isDisabled is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isDisabled=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-disabled'));
  });

  test('it includes the proper classes when isReadOnly is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isReadOnly=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-read-only'));
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isError=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-error'));
  });

  test('it includes the proper classes when isSuccess is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isSuccess=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-success'));
  });

  test('it renders a placeholder', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway placeholder='foo'}}`);
    const textarea = find('textarea');
    assert.equal(textarea.getAttribute('placeholder'), 'foo');
  });

  test('it renders the tooltip', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway tooltip='foo'}}`);
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(document.querySelectorAll('.tool-tip-value')[0].innerText.trim(), 'foo', 'Tooltip form-textarea pops with correct text');
  });

  test('it can be disabled', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isDisabled=true}}`);
    const disabledCount = findAll('textarea[disabled]').length;
    assert.equal(disabledCount, 1);
  });

  test('it is disabled when isReadOnly', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isReadOnly=true}}`);
    const disabledCount = findAll('textarea[disabled]').length;
    assert.equal(disabledCount, 1);
  });

  test('it does not show the error message if there is no error', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isError=false errorMessage='There was an error'}}`);
    assert.equal(findAll('.input-error').length, 0, 'The error message is not present');
  });

  test('it does show the error message if isError is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway isError=true errorMessage='There was an error'}}`);
    assert.equal(findAll('.input-error').length, 1, 'The error message is present');
  });

  test('it takes focus when autofocus is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea-oneway value='foo' autofocus=true}}`);
    assert.equal(findAll('textarea[autofocus]').length, 1, 'expect component to be focused');
  });

  test('it calls closure action onEnter', async function(assert) {
    assert.expect(1);
    this.set('onEnter', () => {
      assert.ok(true, 'onEnter() was properly triggered');
    });
    await render(hbs `{{rsa-form-textarea-oneway onEnter=(action onEnter)}}`);
    const textarea = find('textarea');
    await focus(textarea);
    await triggerKeyEvent(textarea, 'keyup', 13);
  });

  // not part of component yet...
  // test('it calls closure action onFocusIn', async function(assert) {
  // });

  test('it calls closure action onFocusOut', async function(assert) {
    assert.expect(1);
    this.set('onFocusOut', () => {
      assert.ok(true, 'onFocusOut() was properly triggered');
    });
    await render(hbs `{{rsa-form-textarea-oneway onFocusOut=(action onFocusOut)}}`);
    const textarea = find('textarea');
    await focus(textarea);
    await blur(textarea);
  });

  test('it calls closure action onKeyDown', async function(assert) {
    assert.expect(1);
    this.set('onKeyDown', () => {
      assert.ok(true, 'onKeyDown() was properly triggered');
    });
    await render(hbs `{{rsa-form-textarea-oneway onKeyDown=(action onKeyDown)}}`);
    const textarea = find('textarea');
    await focus(textarea);
    await triggerKeyEvent(textarea, 'keydown', 65);
  });

  test('it calls closure action onKeyUp', async function(assert) {
    assert.expect(1);
    this.set('onKeyUp', () => {
      assert.ok(true, 'onKeyUp() was properly triggered');
    });
    await render(hbs `{{rsa-form-textarea-oneway onKeyUp=(action onKeyUp)}}`);
    const textarea = find('textarea');
    await focus(textarea);
    await triggerKeyEvent(textarea, 'keyup', 65);
  });

});
