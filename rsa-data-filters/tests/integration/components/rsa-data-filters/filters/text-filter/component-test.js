import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, fillIn, triggerKeyEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';

module('Integration | Component | rsa-data-filters/filters/text-filter', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the text filter', async function(assert) {
    this.set('options', { name: 'fileName', filterOnBlur: true });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options}}`);
    assert.equal(findAll('.text-filter').length, 1, 'expecting to render text filter container');
    assert.equal(findAll('.text-filter .operators').length, 1, 'expecting to render operator type dropdown');
    assert.equal(findAll('.text-filter .file-name-input').length, 1, 'expecting to render text field');
  });

  test('it should set the proper value to filter control', async function(assert) {
    this.set('options', { filterValue: { operator: 'IN', value: ['malware.exe'] }, filterOnBlur: true });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options}}`);
    assert.equal(document.querySelector('.ember-power-select-selected-item').textContent.trim(), 'Equals');
    assert.equal(document.querySelector('.file-name-input input').value, 'malware.exe');
  });

  test('on changing the operator type updates the query', async function(assert) {
    assert.expect(2);
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'LIKE');
    });
    this.set('options', { name: 'fileName', filterOnBlur: true });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options onChange=(action onQueryChange)}}`);
    await clickTrigger('.text-filter .operators');
    assert.equal(document.querySelectorAll('.ember-power-select-dropdown').length, 1, 'Dropdown is rendered');
    await selectChoose('.operators', 'Contains');
  });

  test('input text will set to query on focus out', async function(assert) {
    assert.expect(2);
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.value, 'malware.exe');
      assert.equal(filterValue.operator, 'IN');
    });
    this.set('options', { name: 'fileName', filterOnBlur: true });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options onChange=(action onQueryChange)}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
  });

  test('it should show the error message and error style', async function(assert) {
    assert.expect(2);
    this.set('onQueryChange', () => {});
    this.set('options', {
      name: 'fileName',
      filterOnBlur: true,
      'validations': {
        format: {
          validator: (value) => {
            return /[!@#$%^&*()+\-=[\]{};':"\\|,.<>/?~`]/.test(value);
          },
          message: 'Error'
        }
      }
    });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options onChange=(action onQueryChange)}}`);
    await fillIn('.file-name-input  input', '@@@123');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    assert.equal(document.querySelectorAll('.is-error').length, 1);
    assert.equal(document.querySelectorAll('.input-error').length, 1);
  });

  test('no validation if operator is part of exclude', async function(assert) {
    assert.expect(2);
    this.set('onQueryChange', () => {});
    this.set('options', {
      name: 'fileName',
      filterOnBlur: false,
      'validations': {
        format: {
          exclude: ['IN'],
          validator: (value) => {
            return !/^[A-Za-z0-9]*$/.test(value);
          },
          message: 'Error'
        }
      }
    });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options onChange=(action onQueryChange)}}`);
    await fillIn('.file-name-input  input', '@@@123');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    assert.equal(document.querySelectorAll('.is-error').length, 0);
    assert.equal(document.querySelectorAll('.input-error').length, 0);
  });

  test('no validation if value is empty', async function(assert) {
    assert.expect(2);
    this.set('onQueryChange', () => {});
    this.set('options', {
      name: 'fileName',
      filterOnBlur: false,
      'validations': {
        format: {
          validator: (value) => {
            return !/^(?:[0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$/.test(value);
          },
          message: 'Error'
        }
      }
    });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options onChange=(action onQueryChange)}}`);
    await fillIn('.file-name-input  input', '  ');
    await triggerKeyEvent('.file-name-input  input', 'keyup', 13);
    assert.equal(document.querySelectorAll('.is-error').length, 0);
    assert.equal(document.querySelectorAll('.input-error').length, 0);
  });

  test('if error on changing the operator not updates the query', async function(assert) {
    assert.expect(1);
    this.set('onQueryChange', () => {
      assert.ok(true);
    });
    this.set('options', { name: 'fileName', filterOnBlur: true });
    await render(hbs`{{rsa-data-filters/filters/text-filter isError=true filterOptions=options onChange=(action onQueryChange)}}`);
    await clickTrigger('.text-filter .operators');
    assert.equal(document.querySelectorAll('.ember-power-select-dropdown').length, 1, 'Dropdown is rendered');
    await selectChoose('.operators', 'Contains');
  });

});
