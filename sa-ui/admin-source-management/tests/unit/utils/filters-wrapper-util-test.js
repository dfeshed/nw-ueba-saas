import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { isFilterHasValues, parseFilters } from 'admin-source-management/components/usm-shared/filters-wrapper/filters-wrapper-util';

module('Unit | Utils | components/usm-shared/filters-wrapper/filters-wrapper-util', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('isFilterHasValues() util function', function(assert) {
    assert.expect(4);

    let noValuesFilter = null;
    let hasValuesResult = isFilterHasValues(noValuesFilter);
    assert.equal(hasValuesResult, false, 'null filter returns false as expected');

    noValuesFilter = { name: 'publishStatus', operator: 'IN' };
    hasValuesResult = isFilterHasValues(noValuesFilter);
    assert.equal(hasValuesResult, false, 'filter with no value prop returns false as expected');

    noValuesFilter = { name: 'publishStatus', operator: 'IN', value: [] };
    hasValuesResult = isFilterHasValues(noValuesFilter);
    assert.equal(hasValuesResult, false, 'filter with empty value prop returns false as expected');

    const hasValuesFilter = { name: 'publishStatus', operator: 'IN', value: ['published', 'unpublished', 'unpublished_edits'] };
    hasValuesResult = isFilterHasValues(hasValuesFilter);
    assert.equal(hasValuesResult, true, 'filter with values returns true as expected');
  });

  // example filters going in
  // [{'name':'publishStatus','operator':'IN','value':['published','unpublished','unpublished_edits']}]

  // example expressionList coming out
  // [{'restrictionType':'IN','propertyValues':[{'value':'published'},{'value':'unpublished'},{'value':'unpublished_edits'}],'propertyName':'publishStatus'}]

  test('parseFilters() util function', function(assert) {
    assert.expect(3);

    let noValuesFilters = [];
    let parsedResult = parseFilters(noValuesFilters);
    assert.equal(parsedResult.length, 0, 'empty filters returns empty array as expected');

    noValuesFilters = [
      null,
      { name: 'publishStatus', operator: 'IN' },
      { name: 'publishStatus', operator: 'IN', value: [] }
    ];
    parsedResult = parseFilters(noValuesFilters);
    assert.equal(parsedResult.length, 0, 'filters with no values returns empty array as expected');

    const expectedResult = [ { restrictionType: 'IN', propertyValues: [{ type: 'STRING', value: 'published' }, { type: 'STRING', value: 'unpublished' }, { type: 'STRING', value: 'unpublished_edits' }], propertyName: 'publishStatus' }];
    const hasValuesFilters = [{ name: 'publishStatus', operator: 'IN', value: ['published', 'unpublished', 'unpublished_edits'] }];
    parsedResult = parseFilters(hasValuesFilters);
    assert.deepEqual(parsedResult, expectedResult, 'filters with values returns array of expressions as expected');
  });

});
