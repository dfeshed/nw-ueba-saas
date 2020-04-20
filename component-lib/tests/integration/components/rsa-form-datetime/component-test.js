import { find, findAll, render } from '@ember/test-helpers';
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
    assert.equal(findAll('input').length, 1);
  });

  test('it is a label', async function(assert) {
    await render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
    const labelCount = findAll('label').length;
    assert.equal(labelCount, 1);
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-form-datetime value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = find('label');
    assert.ok(label.classList.contains('rsa-form-input'));
  });

  test('sets the value', async function(assert) {
    await render(hbs `{{rsa-form-datetime value='foo' onChange=(action (mut dateValue))}}`);
    const input = find('input');
    assert.ok(input.value = 'foo');
  });

  test('sets the label', async function(assert) {
    await render(hbs `{{rsa-form-datetime label='foo' value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = find('.rsa-form-label').textContent.trim();
    assert.equal(label, 'foo');
  });

  test('sets the errorMessage', async function(assert) {
    await render(
      hbs `{{rsa-form-datetime label='foo' isError=true errorMessage='Bar' value=(readonly value) onChange=(action (mut dateValue))}}`
    );
    const error = find('.input-error').textContent.trim();
    assert.equal(error, 'Bar');
  });

  test('it includes the proper classes when isDisabled is true', async function(assert) {
    await render(hbs `{{rsa-form-datetime isDisabled=true value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-disabled'));
  });

  test('it includes the proper classes when isError is true', async function(assert) {
    await render(hbs `{{rsa-form-datetime isError=true value=(readonly value) onChange=(action (mut dateValue))}}`);
    const label = find('label');
    assert.ok(label.classList.contains('is-error'));
  });

  test('it renders a placeholder', async function(assert) {
    await render(
      hbs `{{rsa-form-datetime placeholder='foo' value=(readonly value) onChange=(action (mut dateValue))}}`
    );
    const input = find('input');
    assert.equal(input.getAttribute('placeholder'), 'foo');
  });

  test('it can be disabled', async function(assert) {
    await render(hbs `{{rsa-form-datetime isDisabled=true value=(readonly value) onChange=(action (mut dateValue))}}`);
    const disabledCount = findAll('input[disabled]').length;
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
