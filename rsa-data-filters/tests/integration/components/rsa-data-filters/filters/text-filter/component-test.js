import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, fillIn, blur } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from '../../../../../helpers/ember-power-select';

module('Integration | Component | rsa-data-filters/filters/text-filter', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the text filter', async function(assert) {
    this.set('options', { name: 'fileName' });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options}}`);
    assert.equal(findAll('.text-filter').length, 1, 'expecting to render text filter container');
    assert.equal(findAll('.text-filter .operators').length, 1, 'expecting to render operator type dropdown');
    assert.equal(findAll('.text-filter .file-name-input').length, 1, 'expecting to render text field');
  });

  test('it should set the proper value to filter control', async function(assert) {
    this.set('options', { filterValue: { operator: 'IN', value: 'malware.exe' } });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options}}`);
    assert.equal(document.querySelector('.ember-power-select-selected-item').textContent.trim(), 'Equals');
    assert.equal(document.querySelector('.file-name-input input').value, 'malware.exe');
  });

  test('on changing the operator type updates the query', async function(assert) {
    assert.expect(2);
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.operator, 'LIKE');
    });
    this.set('options', { name: 'fileName' });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options onChange=(action onQueryChange)}}`);
    clickTrigger('.text-filter .operators');
    assert.equal(document.querySelectorAll('.ember-power-select-dropdown').length, 1, 'Dropdown is rendered');
    selectChoose('.operators', 'Contains');
  });

  test('input text will set to query on focus out', async function(assert) {
    assert.expect(2);
    this.set('onQueryChange', (filterValue) => {
      assert.equal(filterValue.value, 'malware.exe');
      assert.equal(filterValue.operator, 'IN');
    });
    this.set('options', { name: 'fileName' });
    await render(hbs`{{rsa-data-filters/filters/text-filter filterOptions=options onChange=(action onQueryChange)}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await blur('.file-name-input  input');
  });

});
