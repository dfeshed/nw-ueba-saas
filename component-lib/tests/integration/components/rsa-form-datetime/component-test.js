import { findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-form-datetime', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('value', '1991-01-01T06:00:00.000Z');
  });

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
    assert.equal(this.$().find('input').length, 1);
  });

  test('it is a label', async function(assert) {
    await render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
    const labelCount = this.$().find('label').length === 1;
    assert.equal(labelCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('rsa-form-input'));
  });

  test('sets the value', async function(assert) {
    await render(hbs `{{rsa-form-datetime value='foo' onChange=(action (mut dateValue))}}`);
    const input = this.$().find('input').first();
    assert.ok(input.val('foo'));
  });

  test('sets the label', async function(assert) {
    await render(hbs `{{rsa-form-datetime label='foo' value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = this.$().find('.rsa-form-label').first().text().trim();
    assert.equal(label, 'foo');
  });

  test('sets the errorMessage', async function(assert) {
    await render(
      hbs `{{rsa-form-datetime label='foo' isError=true errorMessage='Bar' value=(readonly value) onChange=(action (mut dateValue))}}`
    );
    const error = this.$().find('.input-error').first().text().trim();
    assert.equal(error, 'Bar');
  });

  test('it includes the proper classes when isDisabled is true', async function(assert) {
    await render(hbs `{{rsa-form-datetime isDisabled=true value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('is-disabled'));
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    await render(hbs `{{rsa-form-datetime isError=true value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = this.$().find('label').first();
    assert.ok(label.hasClass('is-error'));
  });

  test('it renders a placeholder', async function(assert) {
    await render(
      hbs `{{rsa-form-datetime placeholder='foo' value=(readonly value) onChange=(action (mut dateValue))}}`
    );
    const input = this.$().find('input').first();
    assert.equal(input.attr('placeholder'), 'foo');
  });

  test('it can be disabled', async function(assert) {
    await render(hbs `{{rsa-form-datetime isDisabled=true value=(readonly value) onChange=(action (mut dateValue))}}`);
    const disabledCount = this.$().find('input[disabled]').length === 1;
    assert.equal(disabledCount, 1);
  });

  test('it does not show the error message if there is no error', async function(assert) {
    await render(
      hbs `{{rsa-form-datetime isError=false errorMessage='There was an error' value=(readonly value) onChange=(action (mut dateValue))}}`
    );
    assert.equal(findAll('.input-error').length, 0, 'The error message is not present');
  });

  test('it does show the error message if isError is true', async function(assert) {
    await render(
      hbs `{{rsa-form-datetime isError=true errorMessage='There was an error' value=(readonly value) onChange=(action (mut dateValue))}}`
    );
    assert.equal(findAll('.input-error').length, 1, 'The error message is present');
  });
});
