import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import * as params from './params';

module('Integration | Components | tree-view-operation-parameter', function(hooks) {
  setupRenderingTest(hooks);

  test('renders a text element for a text param', async function(assert) {
    this.set('param', params.optionalText);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.rsa-form-input > input[placeholder="Text"]'));
  });

  test('renders a text element for a number param', async function(assert) {
    this.set('param', params.optionalNumber);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.rsa-form-input > input[placeholder="Number"]'));
  });

  test('renders an enabled switch for a boolean param', async function(assert) {
    this.set('param', params.optionalBoolean);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.x-toggle-container'));
  });

  test('renders power-select for an optional enum-one', async function(assert) {
    this.set('param', params.optionalEnumOne);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.power-select'));
  });

  test('renders power-select-multiple for an optional enum-any', async function(assert) {
    this.set('param', params.optionalEnumAny);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.ember-power-select-multiple-trigger'));
  });

  test('renders a date-time picker for the date-time type', async function(assert) {
    this.set('param', params.optionalDateTime);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.flatpickr-input'));
  });

  test('renders a text element for an unknown type', async function(assert) {
    this.set('param', params.optionalUnknownType);
    await render(hbs`{{tree-view-operation-parameter param=param}}`);
    assert.ok(find('.rsa-form-input > input[placeholder="<size>"]'));
  });

});
