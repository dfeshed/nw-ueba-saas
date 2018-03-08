import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import endpoint from '../../state/schema';
import $ from 'jquery';

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

moduleForComponent('host-list/content-filter', 'Integration | Component | endpoint host-list/content-filter', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('host-list content-filter renders default filters', function(assert) {

  // set height to get all lazy rendered items on the page
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    const textFilters = this.$('.content-filter').find('.text-filter');
    assert.equal(textFilters.length, 0, 'As there are no default filters');
  });
});

test('host-list content-filter renders filters', function(assert) {
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    const textFilters = this.$('.content-filter').find('.text-filter');
    assert.equal(textFilters.length, 2, 'Two text filters rendered');
  });
});

test('host-list content-filter renders text filter with empty validation', function(assert) {
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    $('.filter-trigger-button')[1].click();
    return wait().then(() => {
      $('.ember-text-field').val('').change();
      $('.rsa-form-button')[6].click();
      return wait().then(() => {
        const textIndex = $('.input-error').text().indexOf('Invalid');
        assert.notEqual(textIndex, -1, 'Update text filter with empty value validated');
      });
    });
  });
});

test('host-list content-filter renders text filter with 257 char validation', function(assert) {
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    const char257 = 'The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown' +
    'fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.' +
    'The quick brown fox jumps over ';
    $('.filter-trigger-button')[1].click();
    return wait().then(() => {
      $('.ember-text-field').val(char257).change();
      $('.rsa-form-button')[6].click();
      return wait().then(() => {
        const textIndex = $('.input-error').text().indexOf('Please enter a valid Agent ID');
        assert.notEqual(textIndex, -1, 'Update text filter with 257 text value validated');
      });
    });
  });
});

test('host-list content-filter renders date time filters', function(assert) {
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    const textFilters = this.$('.content-filter').find('.datetime-filter');
    assert.equal(textFilters.length, 1, 'Datetime filter rendered');
  });
});

test('host-list content-filter selecting a filter', function(assert) {
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    $('.filter-trigger-button')[3].click();
    return wait().then(() => {
      $($('.filter-options li')[2]).find('.ember-checkbox').prop('checked', true).change();
      return wait().then(() => {
        assert.equal($('.filter-trigger-button').length, 5, 'Added a filter');
      });
    });
  });
});

test('host-list content-filter search a filter', function(assert) {
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    $('.filter-trigger-button')[3].click();
    return wait().then(() => {
      $('.ember-text-field').val('Agent').change();
      return wait().then(() => {
        assert.equal($('.filter-options li').length, 2, 'Filter search validated');
      });
    });
  });
});

test('host-list text filter update test', function(assert) {
  new ReduxDataHelper(initState)
  .updateFilterExpressionList(testExpressionList.expressionList)
  .updateFilterSchems(endpoint.schema)
  .lastFilterAdded()
  .build();
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    $('.filter-trigger-button')[1].click();
    return wait().then(() => {
      $('.ember-text-field').val('C1C6F9').change();
      $('.rsa-form-button')[6].click();
      const textIndex = $('.filter-trigger-button')[1].innerText.indexOf('C1C6F9');
      assert.notEqual(textIndex, -1, 'Update text filter with value tested');
    });
  });
});

// -------------- List filter tests  -----------------------

test('host-list List filter render test', function(assert) {

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
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    $('.filter-trigger-button')[2].click();
    return wait().then(() => {
      assert.equal($('#list-filter-content').is(':visible'), true, 'List filter render successful');
    });
  });
});


test('host-list List filter selecting list items test', function(assert) {

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
  this.render(hbs`{{host-list/content-filter}}`);
  return wait().then(() => {
    $('.filter-trigger-button')[2].click();
    return wait().then(() => {
      $($('#list-filter-content li')[0]).find('.ember-checkbox').prop('checked', true).change();
      $('.rsa-form-button')[7].click();
      const textIndex = $('.filter-trigger-button')[2].innerText.indexOf('windows');
      assert.notEqual(textIndex, -1, 'List filter list item selected');
    });
  });
});