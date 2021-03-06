import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, fillIn, triggerKeyEvent, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';

module('Integration | Component | rsa-data-filters/filters/number-filter', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('filterOptions', {
      filterOnBlur: true,
      isDecimalAllowed: true,
      name: 'size',
      units: [{ type: 'MB', label: 'Mega Bytes' }, { type: 'bytes', label: 'Bytes' }]
    });
    await render(hbs`{{rsa-data-filters/filters/number-filter filterOptions=filterOptions}}`);
    assert.equal(findAll('.number-filter').length, 1);
    assert.equal(findAll('.operators').length, 1, 'It renders operators');
    assert.equal(findAll('.number-input').length, 1, 'It renders text box');
    assert.equal(findAll('.units').length, 1, 'It renders the unit options');
  });

  test('it renders two text boxes if between operator selected', async function(assert) {
    this.set('filterOptions', { filterOnBlur: true, name: 'size', units: [{ type: 'MB', label: 'Mega Bytes' }, { type: 'bytes', label: 'Bytes' }] });
    await render(hbs`{{rsa-data-filters/filters/number-filter filterOptions=filterOptions}}`);
    assert.equal(findAll('.operators').length, 1, 'It renders operators');
    await clickTrigger('.number-filter .operators');
    assert.equal(document.querySelectorAll('.ember-power-select-dropdown').length, 1, 'Dropdown is rendered');
    await selectChoose('.operators', '.ember-power-select-option', 3);
    assert.equal(document.querySelectorAll('.number-input').length, 2, 'Two text boxes');
  });

  test('it sets the pre applied filter values', async function(assert) {
    this.set('filterOptions', {
      name: 'size',
      filterOnBlur: true,
      isDecimalAllowed: true,
      units: [{ type: 'MB', label: 'Mega Bytes' }, { type: 'bytes', label: 'Bytes' }],
      filterValue: { unit: 'MB', value: [10, 20], operator: 'BETWEEN' }
    });
    await render(hbs`{{rsa-data-filters/filters/number-filter filterOptions=filterOptions}}`);
    assert.equal(findAll('.operators').length, 1, 'It renders operators');
    assert.equal(document.querySelector('.ember-power-select-selected-item').textContent.trim(), 'Between');
    assert.equal(document.querySelectorAll('.number-input').length, 2, 'Two text boxes');
    assert.equal(document.querySelector('.number-input.start input').value, 10, 'Two text boxes');
    assert.equal(document.querySelector('.number-input.end input').value, 20, 'Two text boxes');
    assert.equal(document.querySelector('.number-input input').placeholder, 'Enter Value');
  });

  test('onChange is called on update', async function(assert) {
    assert.expect(2);
    this.set('onChange', (filter) => {
      assert.equal(filter.value[0], 20);
    });
    this.set('filterOptions', {
      name: 'size',
      filterOnBlur: true,
      isDecimalAllowed: true,
      units: [{ type: 'MB', label: 'Mega Bytes' }, { type: 'bytes', label: 'Bytes' }],
      filterValue: { unit: 'MB', value: [10, 20], operator: 'BETWEEN' }
    });
    await render(hbs`{{rsa-data-filters/filters/number-filter onChange=(action onChange) filterOptions=filterOptions}}`);
    assert.equal(findAll('.operators').length, 1, 'It renders operators');
    await fillIn('.number-input.start input', '20');
    await triggerKeyEvent('.number-input.start input', 'keyup', 13);
  });

  test('It test the key press for decimal value not allowed', async function(assert) {
    this.set('filterOptions', {
      name: 'hostCount',
      filterOnBlur: true,
      isDecimalAllowed: false,
      filterValue: { value: [10, 20], operator: 'BETWEEN' }
    });
    await render(hbs`{{rsa-data-filters/filters/number-filter filterOptions=filterOptions}}`);
    await triggerEvent('.number-input.start input', 'keypress', { charCode: 46 });
    assert.equal(document.querySelector('.number-input.start input').value, 10, 'value should be 10');
  });

});
