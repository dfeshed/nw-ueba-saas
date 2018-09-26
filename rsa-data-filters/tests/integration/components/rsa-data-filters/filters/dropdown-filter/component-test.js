import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';

module('Integration | Component | rsa-data-filters/filters/dropdown-filter', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the dropdown filter', async function(assert) {
    await render(hbs`{{rsa-data-filters/filters/dropdown-filter}}`);
    assert.equal(this.element.querySelectorAll('.dropdown-filter').length, 1, 'Expected to render the dropdown filter');
  });

  test('it should show options in dropdwon', async function(assert) {
    const options = [
      { name: 'one', label: 'Option 1' },
      { name: 'two', label: 'Option 2' },
      { name: 'three', label: 'Option 3' }
    ];
    this.set('options', { name: 'signature', listOptions: options });
    await render(hbs`{{rsa-data-filters/filters/dropdown-filter filterOptions=options}}`);
    await clickTrigger('.dropdown-filter');
    assert.equal(document.querySelectorAll('.ember-power-select-options li.ember-power-select-option').length, 3, 'There are 3 options available');
  });

  test('it should set the query on selecting drowdown value', async function(assert) {
    assert.expect(1);
    this.set('onChange', (query) => {
      assert.equal(query.value.length, 1);
    });
    const options = [
      { name: 'one', label: 'Option 1' },
      { name: 'two', label: 'Option 2' }
    ];
    this.set('options', { name: 'fileStatus', listOptions: options, filterValue: ['two'] });

    await render(hbs`{{rsa-data-filters/filters/dropdown-filter onChange=(action onChange) filterOptions=options}}`);
    await selectChoose('.dropdown-filter', '.ember-power-select-option', 0);
  });

  test('it should set the query on selecting drowdown value', async function(assert) {
    assert.expect(1);
    this.set('onChange', (query) => {
      assert.equal(query.value.length, 2);
    });
    const options = [
      { name: 'one', label: 'Option 1' },
      { name: 'two', label: 'Option 2' }
    ];
    this.set('options', { multiSelect: true, name: 'fileStatus', listOptions: options, filterValue: ['two'] });

    await render(hbs`{{rsa-data-filters/filters/dropdown-filter onChange=(action onChange) filterOptions=options}}`);
    await selectChoose('.dropdown-filter', '.ember-power-select-option', 0);
  });
});
