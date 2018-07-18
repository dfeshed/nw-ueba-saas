import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, fillIn, blur } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-data-filters', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders configured filters', async function(assert) {
    this.set('config', [{ type: 'text', name: 'filterName' }]);
    await render(hbs`{{rsa-data-filters config=config}}`);
    assert.equal(findAll('.filter-controls').length, 1, 'Expecting to render one filter control');
    assert.equal(findAll('.text-filter').length, 1, 'Expected render text filter');
  });

  test('it should call the onFilterChange after changing the filter', async function(assert) {
    assert.expect(3);
    this.set('config', [{ type: 'text', name: 'filterName' }]);
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
    this.set('config', [{ type: 'text', name: 'filterName' }, { type: 'text', name: 'size', filterValue: { operator: 'LIKE', value: 'test' } }]);
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
});
