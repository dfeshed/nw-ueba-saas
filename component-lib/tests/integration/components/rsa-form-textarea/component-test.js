import { focus, render } from '@ember/test-helpers';
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
    assert.equal(this.$().find('textarea').length, 1);
  });

  test('it is a label', async function(assert) {
    await render(hbs `{{rsa-form-textarea}}`);
    const labelCount = this.$().find('label').length === 1;
    assert.equal(labelCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-form-textarea}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('rsa-form-textarea'));
  });

  test('sets the value', async function(assert) {
    await render(hbs `{{rsa-form-textarea value='foo'}}`);
    const input = this.$().find('textarea').first();
    assert.ok(input.val('foo'));
  });

  test('sets the label', async function(assert) {
    await render(hbs `{{rsa-form-textarea label='foo'}}`);
    const label = this.$().find('.rsa-form-label').first();
    assert.equal(label.text().trim(), 'foo');
  });

  test('it includes the proper classes when isDisabled is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isDisabled=true}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('is-disabled'));
  });

  test('it includes the proper classes when isReadOnly is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isReadOnly=true}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('is-read-only'));
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isError=true}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('is-error'));
  });

  test('it includes the proper classes when isSuccess is true', async function(assert) {
    await render(hbs `{{rsa-form-textarea isSuccess=true}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('is-success'));
  });

  test('it renders a placeholder', async function(assert) {
    await render(hbs `{{rsa-form-textarea placeholder='foo'}}`);
    const textarea = this.$().find('textarea').first();
    assert.equal(textarea.attr('placeholder'), 'foo');
  });

  test('it renders the tooltip', async function(assert) {
    await render(hbs `{{rsa-form-textarea tooltip='foo'}}`);
    const tooltip = this.$().find('div').first();
    assert.ok(tooltip.hasClass('tooltip-text'));
  });

  test('it can be disabled', async function(assert) {
    await render(hbs `{{rsa-form-textarea isDisabled=true}}`);
    const disabledCount = this.$().find('textarea[disabled]').length === 1;
    assert.equal(disabledCount, 1);
  });

  test('it is disabled when isReadOnly', async function(assert) {
    await render(hbs `{{rsa-form-textarea isReadOnly=true}}`);
    const disabledCount = this.$().find('textarea[disabled]').length === 1;
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
    this.$('textarea').blur();
  });
});
