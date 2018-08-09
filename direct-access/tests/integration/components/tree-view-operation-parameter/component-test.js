import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import * as params from './params';

module('Integration | Components | tree-view-operation-parameter', function(hooks) {
  setupRenderingTest(hooks);

  test('renders an enabled text element for a text param', async function(assert) {
    this.set('param', params.optionalText);
    await render(hbs`{{tree-view-operation-parameter param=param optionalEnabled=true}}`);
    assert.ok(find('.rsa-form-input'));
    assert.notOk(find('.rsa-form-input.is-disabled'));
  });

  test('renders a disabled input for a disabled text param', async function(assert) {
    this.set('param', params.optionalText);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.rsa-form-input.is-disabled'));
  });

  test('renders an enabled text element for a number param', async function(assert) {
    this.set('param', params.optionalNumber);
    await render(hbs`{{tree-view-operation-parameter param=param optionalEnabled=true}}`);
    assert.ok(find('.rsa-form-input'));
    assert.notOk(find('.rsa-form-input.is-disabled'));
  });

  test('renders a disabled input for a disabled number param', async function(assert) {
    this.set('param', params.optionalNumber);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.rsa-form-input.is-disabled'));
  });

  test('renders an enabled switch for a boolean param', async function(assert) {
    this.set('param', params.optionalBoolean);
    await render(hbs`{{tree-view-operation-parameter param=param optionalEnabled=true}}`);
    assert.ok(find('.x-toggle-container'));
    assert.notOk(find('.x-toggle-container.x-toggle-container-disabled'));
  });

  test('renders a disabled switch for a disabled boolean param', async function(assert) {
    this.set('param', params.optionalBoolean);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.x-toggle-container.x-toggle-container-disabled'));
  });

  test('does not render power-select for an optional enum-one which is not turned on', async function(assert) {
    this.set('param', params.optionalEnumOne);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.notOk(find('.power-select'));
  });

  test('renders power-select for an optional enum-one which is turned on', async function(assert) {
    this.set('param', params.optionalEnumOne);
    await render(hbs`{{tree-view-operation-parameter param=param optionalEnabled=true}}`);
    assert.ok(find('.power-select'));
  });

  test('does not render power-select-multiple for an optional enum-any which is not turned on', async function(assert) {
    this.set('param', params.optionalEnumAny);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.notOk(find('.ember-power-select-multiple-trigger'));
  });

  test('renders power-select-multiple for an optional enum-any which is turned on', async function(assert) {
    this.set('param', params.optionalEnumAny);
    await render(hbs`{{tree-view-operation-parameter param=param optionalEnabled=true}}`);
    assert.ok(find('.ember-power-select-multiple-trigger'));
  });

  test('renders an enabled date-time picker for the date-time type', async function(assert) {
    this.set('param', params.optionalDateTime);
    await render(hbs`{{tree-view-operation-parameter param=param optionalEnabled=true}}`);
    assert.ok(find('.flatpickr-input'));
    assert.notOk(find('.flatpickr-input[disabled]'));
  });

  test('renders an disabled date-time picker for a disabled date-time type', async function(assert) {
    this.set('param', params.optionalDateTime);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.flatpickr-input[disabled]'));
  });

  test('renders an enabled text element for an unknown type', async function(assert) {
    this.set('param', params.optionalUnknownType);
    await render(hbs`{{tree-view-operation-parameter param=param optionalEnabled=true}}`);
    const element = find('.rsa-form-input');
    assert.ok(element);
    assert.notOk(find('.rsa-form-input.is-disabled'));
  });

  test('renders a disabled input for a disabled unknown type', async function(assert) {
    this.set('param', params.optionalUnknownType);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.rsa-form-input.is-disabled'));
  });

});
