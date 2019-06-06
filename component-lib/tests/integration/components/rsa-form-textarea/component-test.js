import { focus, render, findAll, find } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-form-textarea', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.actions = {};
    this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-form-textarea}}`);
    assert.equal(findAll('textarea').length, 1);
  });

  test('it is a label', async function(assert) {
    await render(hbs `{{rsa-form-textarea}}`);
    const labelCount = findAll('label').length;
    assert.equal(labelCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-form-textarea}}`);
    const label = find('label');
    assert.ok(label.classList.contains('rsa-form-textarea'));
  });

  test('sets the value', async function(assert) {
    await render(hbs `{{rsa-form-textarea value='foo'}}`);
    const input = find('textarea');
    assert.ok(input.value = 'foo');
  });

  test('sets the label', async function(assert) {
    await render(hbs `{{rsa-form-textarea label='foo'}}`);
    const label = find('.rsa-form-label');
    assert.equal(label.textContent.trim(), 'foo');
  });

  test('it includes the proper classes when isDisabled is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isDisabled=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-disabled'));
  });

  test('it includes the proper classes when isReadOnly is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isReadOnly=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-read-only'));
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isError=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-error'));
  });

  test('it includes the proper classes when isSuccess is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isSuccess=true}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-success'));
  });

  test('it renders a placeholder', async function(assert) {
    await render(hbs `{{rsa-form-textarea placeholder='foo'}}`);
    const textarea = find('textarea');
    assert.equal(textarea.getAttribute('placeholder'), 'foo');
  });

  test('it renders the tooltip', async function(assert) {
    await render(hbs `{{rsa-form-textarea tooltip='foo'}}`);
    // findAll returns three elements - nested elements as separate
    // therefore [0] is not what we want, [1] is
    // querySelectorAll returns two elements - .item(0) contains nested div
    const tooltip = this.element.querySelectorAll('div').item(0);
    assert.ok(tooltip.classList.contains('tooltip-text'));
  });

  test('it can be disabled', async function(assert) {
    await render(hbs `{{rsa-form-textarea isDisabled=true}}`);
    const disabledCount = findAll('textarea[disabled]').length;
    assert.equal(disabledCount, 1);
  });

  test('it is disabled when isReadOnly', async function(assert) {
    await render(hbs `{{rsa-form-textarea isReadOnly=true}}`);
    const disabledCount = findAll('textarea[disabled]').length;
    assert.equal(disabledCount, 1);
  });

  test('it calls closure action focusIn', async function(assert) {
    assert.expect(1);
    this.actions.focus = () => {
      assert.ok(true);
    };
    await render(hbs `{{rsa-form-textarea focusIn=(action 'focus')}}`);
    await focus('textarea');
  });

  test('it calls closure action focusOut', async function(assert) {
    assert.expect(1);
    this.actions.blur = () => {
      assert.ok(true);
    };
    await render(hbs `{{rsa-form-textarea focusOut=(action 'blur')}}`);
    await focus('textarea');
    // await blur() does not trigger this.actions.blur
    this.element.querySelector('textarea').blur();
  });
});
