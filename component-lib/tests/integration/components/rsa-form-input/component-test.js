import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, triggerKeyEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-form-input', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.actions = {};
    this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-form-input}}`);
    assert.equal(findAll('input').length, 1);
  });

  test('it is a label', async function(assert) {
    await render(hbs `{{rsa-form-input}}`);
    const labelCount = findAll('label').length === 1;
    assert.equal(labelCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-form-input}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('rsa-form-input'));
  });

  test('sets the value', async function(assert) {
    await render(hbs `{{rsa-form-input value='foo'}}`);
    const [input] = findAll('input');
    assert.equal(input.value, 'foo');
  });

  test('sets the label', async function(assert) {
    await render(hbs `{{rsa-form-input label='foo'}}`);
    const label = find('.rsa-form-label').textContent.trim();
    assert.equal(label, 'foo');
  });

  test('sets the errorMessage', async function(assert) {
    await render(hbs `{{rsa-form-input label='foo' isError=true errorMessage='Bar'}}`);
    const error = find('.input-error').textContent.trim();
    assert.equal(error, 'Bar');
  });

  test('it includes the proper classes when isInline is true', async function(assert) {
    await render(hbs `{{rsa-form-input isInline=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-inline'));
  });

  test('it includes the proper classes when isDisabled is true', async function(assert) {
    await render(hbs `{{rsa-form-input isDisabled=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-disabled'));
  });

  test('it includes the proper classes when isReadOnly is true', async function(assert) {
    await render(hbs `{{rsa-form-input isReadOnly=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-read-only'));
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    await render(hbs `{{rsa-form-input isError=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-error'));
  });

  test('it includes the proper classes when isSuccess is true', async function(assert) {
    await render(hbs `{{rsa-form-input isSuccess=true}}`);
    const [label] = findAll('label');
    assert.ok(label.classList.contains('is-success'));
  });

  test('it renders a placeholder', async function(assert) {
    await render(hbs `{{rsa-form-input placeholder='foo'}}`);
    const [input] = findAll('input');
    assert.equal(input.getAttribute('placeholder'), 'foo');
  });

  test('it can be disabled', async function(assert) {
    await render(hbs `{{rsa-form-input isDisabled=true}}`);
    const disabledCount = findAll('input[disabled]').length === 1;
    assert.equal(disabledCount, 1);
  });

  test('it is disabled when isReadOnly', async function(assert) {
    await render(hbs `{{rsa-form-input isReadOnly=true}}`);
    const disabledCount = findAll('input[disabled]').length === 1;
    assert.equal(disabledCount, 1);
  });

  test('it calls closure action focusIn', async function(assert) {
    assert.expect(1);
    const done = assert.async();
    this.actions.focus = () => {
      assert.ok(true);
      done();
    };
    await render(hbs `{{rsa-form-input focusIn=(action 'focus')}}`);
    this.$('input').focus();
  });

  test('it calls closure action focusOut', async function(assert) {
    assert.expect(1);
    const done = assert.async();
    this.actions.blur = () => {
      assert.ok(true);
      done();
    };
    await render(hbs `{{rsa-form-input focusOut=(action 'blur')}}`);
    this.$('input').blur();
  });

  test('it does not show the error message if there is no error', async function(assert) {
    await render(hbs `{{rsa-form-input isError=false errorMessage='There was an error'}}`);
    assert.equal(findAll('.input-error').length, 0, 'The error message is not present');
  });

  test('it does show the error message if isError is true', async function(assert) {
    await render(hbs `{{rsa-form-input isError=true errorMessage='There was an error'}}`);
    assert.equal(findAll('.input-error').length, 1, 'The error message is present');
  });

  test('it takes focus when autofocus is true', async function(assert) {
    await render(hbs `{{rsa-form-input value='foo' autofocus=true}}`);
    assert.equal(findAll('input[autofocus]').length, 1, 'expect component to be focused');
  });

  test('it calls closure action focusOut', async function(assert) {
    assert.expect(1);
    const done = assert.async();
    this.actions.onKeyUp = () => {
      assert.ok(true);
      done();
    };
    await render(hbs `{{rsa-form-input onKeyUp=(action 'onKeyUp')}}`);
    await triggerKeyEvent('input', 'keyup', 65);
  });
});

