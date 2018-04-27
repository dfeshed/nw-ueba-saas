import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { click, render, find, findAll } from '@ember/test-helpers';
import files from '../../state/files';
import { patchReducer } from '../../../helpers/vnext-patch';

let setState;

module('content-filter', 'Integration | Component | content filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('content-filter renders', async function(assert) {
    const { files: { schema: { schema }, filter: { expressionList } } } = files;

    new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).build();

    await render(hbs`{{content-filter}}`);
    assert.equal(find('.filter-search-icon').textContent.trim(), 'Filter:', 'Filter:, text present');
    assert.equal(findAll('.text-filter').length, 1, 'Text filter added');

    assert.equal(findAll('.filter-trigger-button').length, 2, 'Add filter button present');
    assert.equal(find('.moreOptions .filter-trigger-button').textContent.trim(), 'Add Filter', 'Add filter button text present');

    assert.equal(findAll('.save-button').length, 1, 'Filter save button present');
    assert.equal(find('.save-button').textContent.trim(), 'Save', 'Filter save button present');

    assert.equal(find('.save-button + .rsa-form-button-wrapper button').textContent.trim(), 'Reset', 'Filter reset button present');

    await click('.moreOptions .filter-trigger-button');
    assert.equal(findAll('.ember-tether.ember-tether-enabled').length, 1, 'Column chooser dropdown enabled');
    assert.equal(findAll('.column-chooser-input').length, 1, 'Column chooser dropdown present');
    assert.equal(find('.column-chooser-input input').placeholder, 'Type to filter the list', 'Type to filter the list, Placeholder text present in column chooser');
    assert.equal(findAll('.filter-options input').length, 8, 'Number of filters present in the dropdown is 8');
    assert.equal(findAll('.filter-options input:checked').length, 1, 'Number of filters checked in the dropdown is 1');

    assert.equal(findAll('.files-content-filter > div').length, 6, 'Number of buttons present before a new filter has been added.');
    await click('.filter-options li:nth-of-type(1) input');
    assert.equal(findAll('.files-content-filter > div').length, 7, 'Count of buttons increased after New filter has been added.');
  });

  test('Save filter functionality', async function(assert) {
    const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
    new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

    await render(hbs`{{content-filter}}`);

    await click('.save-button button');

    assert.notEqual(document.querySelector('#modalDestination').children.length, 0, 'Modal popup present');
    assert.equal(document.querySelector('#modalDestination .modal-content').children.length, 3, 'modal-content present');
    assert.equal(find('.modal-content > label.rsa-form-label').textContent.trim(),
    'Provide a name to the search to be saved. This name will appear in the search box list.',
    'modal-content label provided');
    assert.equal(find('.name .rsa-form-label').textContent.trim(), 'Name *', 'modal-content label provided');
    assert.equal(find('.name input.ember-text-field').maxLength, 255, 'Length limit on input field');

    assert.equal(findAll('.modal-content .name + label').length, 0, 'Name field is empty. text not present');

    assert.equal(findAll('.modal-content .rsa-btn-group button').length, 2, 'Two buttons present');
    assert.equal(find('.modal-content .rsa-btn-group .save-filter button').textContent.trim(), 'Save', 'Save button present');
    assert.equal(find('.modal-content .rsa-btn-group .cancel-filter button').textContent.trim(), 'Cancel', 'Cancel button present');

    await click('.save-filter');
    await click('.save-filter');

    assert.equal(find('.modal-content .name + label').textContent.trim(), 'Name field is empty.', 'Name field is empty. Error text present');
  });

  test('Cancel filter functionality', async function(assert) {
    const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
    new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

    await render(hbs`{{content-filter}}`);

    await click('.save-button button');
    await click('.cancel-filter');
    assert.equal(document.querySelector('#modalDestination').children.length, 0, 'Save popup has been closed');
  });

  test('Save filter functionality', async function(assert) {
    const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
    new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

    this.set('saveFilterName', 'TestFilter');
    await render(hbs`{{content-filter saveFilterName=saveFilterName}}`);

    await click('.save-button button');

    await click('.save-filter button');
    assert.equal(document.querySelector('#modalDestination').children.length, 0, 'Save popup has been closed');
  });

  test('Reset filter functionality', async function(assert) {
    const { files: { schema: { schema }, filter: { expressionList, filter } } } = files;
    new ReduxDataHelper(setState).schema(schema).expressionList(expressionList).filesFilterFilter(filter).build();

    await render(hbs`{{content-filter}}`);

    assert.equal(findAll('.files-content-filter > div').length, 6, 'Number of buttons present before filter reset.');
    await click('.save-button + .rsa-form-button-wrapper button');
    assert.equal(findAll('.files-content-filter > div').length, 5, 'Number of buttons present after filter reset.');
  });
});