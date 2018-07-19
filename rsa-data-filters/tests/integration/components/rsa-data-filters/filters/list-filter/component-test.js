import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-data-filters/filters/list-filter', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the list filter', async function(assert) {
    const listOptions = [
      { name: 'one', label: 'Option 1' },
      { name: 'two', label: 'Option 2' }
    ];
    this.set('options', { name: 'fileStatus', listOptions });
    await render(hbs`{{rsa-data-filters/filters/list-filter filterOptions=options}}`);
    assert.equal(findAll('.list-filter').length, 1, 'expecting to render list filter container');
  });

  test('it should render the checkboxes for list filter', async function(assert) {
    const options = [
      { name: 'one', label: 'Option 1' },
      { name: 'two', label: 'Option 2' }
    ];
    this.set('options', { name: 'fileStatus', listOptions: options });
    await render(hbs`{{rsa-data-filters/filters/list-filter filterOptions=options}}`);
    assert.equal(findAll('.list-filter .list-filter-option').length, 2, 'expecting to render list filter options');
  });

  test('it should set the proper value to filter control', async function(assert) {
    const options = [
      { name: 'one', label: 'Option 1' },
      { name: 'two', label: 'Option 2' }
    ];
    this.set('options', { name: 'fileStatus', listOptions: options, filterValue: ['one', 'two'] });
    await render(hbs`{{rsa-data-filters/filters/list-filter filterOptions=options}}`);
    assert.equal(findAll('.list-filter .list-filter-option.checked').length, 2, 'expecting to select all checkbox');
  });

  test('it should set the query on clicking the checkbox', async function(assert) {
    this.set('onChange', (query) => {
      assert.equal(query.value.length, 2);
    });
    const options = [
      { name: 'one', label: 'Option 1' },
      { name: 'two', label: 'Option 2' }
    ];
    this.set('options', { name: 'fileStatus', listOptions: options, filterValue: ['two'] });

    await render(hbs`{{rsa-data-filters/filters/list-filter onChange=(action onChange) filterOptions=options}}`);
    assert.equal(findAll('.list-filter .list-filter-option.checked').length, 1, 'expecting to select one checkbox');
    await click('.list-filter .list-filter-option');
    assert.equal(findAll('.list-filter .list-filter-option.checked').length, 2, 'expecting to select all checkbox');
  });

});
