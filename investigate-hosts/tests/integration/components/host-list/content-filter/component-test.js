import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, fillIn, blur, click } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

import engineResolver from '../../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import endpoint from '../../state/schema';

let initState;
const testExpressionList = {
  expressionList: [
    {
      propertyName: 'id',
      propertyValues: null
    },
    {
      propertyName: 'machine.agentVersion',
      propertyValues: null
    },
    {
      propertyName: 'machine.scanStartTime',
      propertyValues: null
    }
  ]
};

initState = Immutable.from({
  endpoint: {
    filter: {
      expressionList: [],
      lastFilterAdded: null,
      schemas: endpoint.schema,
      appliedFilters: null
    }
  }
});

module('Integration | Component | host-list/content-filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('host-list content-filter renders default filters', async function(assert) {
    assert.expect(1);
    // set height to get all lazy rendered items on the page
    await render(hbs`{{host-list/content-filter}}`);
    assert.equal(findAll('.content-filter .text-filter').length, 0, 'As there are no default filters');
  });

  test('host-list content-filter renders filters', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    assert.equal(findAll('.content-filter .text-filter').length, 2, 'Two text filters rendered');
  });

  test('host-list content-filter renders text filter with empty validation', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    await click('.text-filter:nth-of-type(2) .filter-trigger-button');
    await fillIn('.text-filter__content .ember-text-field', '');
    await blur('.text-filter__content .ember-text-field');
    await click('.text-filter__content .rsa-form-button');
    const textIndex = find('.input-error').textContent.indexOf('Invalid');
    assert.notEqual(textIndex, -1, 'Update text filter with empty value validated');
  });

  test('host-list content-filter renders text filter with 257 char validation', async function(assert) {
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    const char257 = 'The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown' +
    'fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.' +
    'The quick brown fox jumps over ';
    await click('.text-filter:nth-of-type(2) .filter-trigger-button');
    await fillIn('.ember-text-field', char257);
    await blur('.text-filter__content .ember-text-field');
    await click('.text-filter__content .rsa-form-button');
    const textIndex = find('.input-error').textContent.indexOf('Please enter a valid Agent ID');
    assert.notEqual(textIndex, -1, 'Update text filter with 257 text value validated');
  });

  test('host-list content-filter renders date time filters', async function(assert) {
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    assert.equal(findAll('.content-filter .datetime-filter').length, 1, 'Datetime filter rendered');
  });

  test('host-list content-filter selecting a filter', async function(assert) {
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    await click('.filter-add-more .filter-trigger-button');
    await click('.filter-options li:nth-child(3) .ember-checkbox');
    assert.equal(findAll('.filter-trigger-button').length, 5, 'Added a filter');
  });

  test('host-list content-filter search a filter', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    await click('.filter-add-more .filter-trigger-button');
    await fillIn('.column-chooser-input .ember-text-field', 'Agent');
    await blur('.column-chooser-input .ember-text-field');
    assert.equal(findAll('.filter-options li').length, 2, 'Filter search validated');
  });

  test('host-list text filter update test', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    await click('.text-filter:nth-of-type(2) .filter-trigger-button');
    await fillIn('.text-filter__content .ember-text-field', 'C1C6F9');
    await blur('.text-filter__content .ember-text-field');
    await click('.text-filter__content .rsa-form-button');
    const textIndex = find('.text-filter:nth-of-type(2) .filter-trigger-button').innerText.indexOf('C1C6F9');
    assert.notEqual(textIndex, -1, 'Update text filter with value tested');
  });

  // -------------- List filter tests  -----------------------

  test('host-list List filter render test', async function(assert) {
    assert.expect(2);
    // test data setting
    testExpressionList.expressionList.push({
      propertyName: 'machine.machineOsType',
      propertyValues: null
    });
    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    assert.equal(findAll('.list-filter-lists').length, 0);
    await click('.list-filter .filter-trigger-button');
    assert.equal(findAll('.list-filter-lists').length, 1, 'List filter rendered successful');
  });


  test('host-list List filter selecting list items test', async function(assert) {
    assert.expect(1);
    // test data setting
    testExpressionList.expressionList.push({
      propertyName: 'machine.machineOsType',
      propertyValues: null
    });

    new ReduxDataHelper(initState)
    .updateFilterExpressionList(testExpressionList.expressionList)
    .updateFilterSchems(endpoint.schema)
    .lastFilterAdded()
    .build();
    await render(hbs`{{host-list/content-filter}}`);
    await click('.list-filter .filter-trigger-button');
    await click('.list-filter-lists li:first-child .ember-checkbox');
    await click('.footer .rsa-form-button');
    const textIndex = find('.list-filter .filter-trigger-button').innerText.indexOf('windows');
    assert.notEqual(textIndex, -1, 'List filter list item selected');
  });
});