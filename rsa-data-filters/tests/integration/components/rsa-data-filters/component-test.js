import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, fillIn, blur, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-data-filters', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders configured filters', async function(assert) {
    this.set('config', [{ type: 'text', name: 'filterName' }, { type: 'list', listOptions: [{ name: 'one', label: 'ONE' }] }]);
    await render(hbs`{{rsa-data-filters config=config}}`);
    assert.equal(findAll('.filter-controls').length, 2, 'Expecting to render one filter control');
    assert.equal(findAll('.text-filter').length, 1, 'Expected render text filter');
    assert.equal(findAll('.list-filter').length, 1, 'Expected render list filter');
  });

  test('it should call the onFilterChange after changing the filter', async function(assert) {
    assert.expect(3);
    this.set('config', [{ type: 'text', name: 'filterName', filterOnBlur: true }]);
    this.set('onFilterChange', (filters) => {
      assert.equal(filters.length, 1);
    });

    await render(hbs`{{rsa-data-filters onFilterChange=(action onFilterChange) config=config}}`);
    assert.equal(findAll('.filter-controls').length, 1, 'Expecting to render one filter control');
    assert.equal(findAll('.text-filter').length, 1, 'Expected render text filter');
    await fillIn('.file-name-input  input', 'malware.exe');
    await blur('.file-name-input  input');
  });

  test('it should call the onFilterChange with newly added and pre loaded filter', async function(assert) {
    assert.expect(4);
    this.set('config', [
      { type: 'text', name: 'status', filterOnBlur: true },
      { type: 'text', name: 'size', filterOnBlur: true, filterValue: { operator: 'LIKE', value: ['test'] } }
    ]);
    this.set('onFilterChange', (filters) => {
      assert.equal(filters.length, 2);
      assert.equal(filters[0].operator, 'LIKE');
    });

    await render(hbs`{{rsa-data-filters onFilterChange=(action onFilterChange) config=config}}`);
    assert.equal(findAll('.filter-controls').length, 2, 'Expecting to render one filter control');
    assert.equal(findAll('.text-filter').length, 2, 'Expected render text filter');
    await fillIn('.file-name-input  input', 'malware.exe');
    await blur('.file-name-input  input');
  });

  test('it should call the onFilterChange with newly added and pre loaded filter', async function(assert) {
    assert.expect(6);
    this.set('config', [
      { type: 'list', name: 'status', listOptions: [{ name: 'one', label: 'ONE' }] },
      { type: 'text', name: 'size', filterOnBlur: true, filterValue: { operator: 'LIKE', value: ['test'] } }
    ]);
    this.set('onFilterChange', (filters) => {
      assert.equal(filters.length, 2);
      assert.equal(filters[0].operator, 'LIKE');
    });

    await render(hbs`{{rsa-data-filters onFilterChange=(action onFilterChange) config=config}}`);
    assert.equal(findAll('.filter-controls').length, 2, 'Expecting to render one filter control');
    assert.equal(findAll('.text-filter').length, 1, 'Expected render text filter');
    assert.equal(findAll('.list-filter').length, 1, 'Expected render list filter');
    assert.equal(find('.text-filter .file-name-input input').value.trim(), 'test', 'Expected to set the correct value to text field');
    await click('.list-filter .list-filter-option');
  });

  test('it should call the onFilterChange with newly added and pre loaded filter', async function(assert) {
    assert.expect(7);
    this.set('config', [{ type: 'list', name: 'status', filterValue: ['one', 'two'], listOptions: [{ name: 'one', label: 'ONE' }, { name: 'two', label: 'Two' }] }, { type: 'text', filterOnBlur: true, name: 'size', filterValue: { operator: 'LIKE', value: ['test'] } }]);
    this.set('onFilterChange', (filters) => {
      assert.equal(filters.length, 2);
      assert.equal(filters[1].name, 'status');
      assert.equal(filters[1].value.length, 1);
    });

    await render(hbs`{{rsa-data-filters onFilterChange=(action onFilterChange) config=config}}`);
    assert.equal(findAll('.filter-controls').length, 2, 'Expecting to render one filter control');
    assert.equal(findAll('.text-filter').length, 1, 'Expected render text filter');
    assert.equal(findAll('.list-filter').length, 1, 'Expected render list filter');
    assert.equal(find('.text-filter .file-name-input input').value.trim(), 'test', 'Expected to set the correct value to text field');
    await click('.list-filter .list-filter-option');
  });

  test('it should show Save filter button and clicking on save will call the callback', async function(assert) {
    assert.expect(2);
    this.set('config', [{ type: 'list', name: 'status', filterValue: ['one', 'two'], listOptions: [{ name: 'one', label: 'ONE' }, { name: 'two', label: 'Two' }] }, { type: 'text', filterOnBlur: true, name: 'size', filterValue: { operator: 'LIKE', value: ['test'] } }]);
    this.set('onSave', (filters) => {
      assert.equal(filters.length, 2);
    });

    this.set('showSaveFilterButton', true);
    await render(hbs`{{rsa-data-filters showSaveFilterButton=showSaveFilterButton onSave=(action onSave) config=config}}`);
    assert.equal(findAll('.save-filter-button').length, 1, 'Expected render save button');
    click('.save-filter-button button');
  });

  test('it reset action is executed', async function(assert) {
    assert.expect(3);
    this.set('config', [{ type: 'list', name: 'status', filterValue: ['one', 'two'], listOptions: [{ name: 'one', label: 'ONE' }, { name: 'two', label: 'Two' }] }, { type: 'text', filterOnBlur: true, name: 'size', filterValue: { operator: 'LIKE', value: ['test'] } }]);
    this.set('onFilterChange', (filters) => {
      assert.equal(filters.length, 2);
    });

    this.set('showSaveFilterButton', false);
    await render(hbs`{{rsa-data-filters onFilterChange=(action onFilterChange) showSaveFilterButton=showSaveFilterButton config=config}}`);
    assert.equal(findAll('.save-filter-button').length, 0);
    assert.equal(findAll('.reset-filter-button').length, 1, 'Expected render reset button');
    click('.reset-filter-button button');
  });


});
