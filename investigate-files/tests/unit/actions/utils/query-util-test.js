import { module, test } from 'qunit';

import { addFilter } from 'investigate-files/actions/utils/query-util';

module('Unit | Utils | Query Util', function() {

  test('adFilter split the hashes in to three columns', function(assert) {

    const expressionList = [
      {
        propertyName: 'fileHash',
        propertyValues: [{ value: 1 }],
        restrictionType: 'IN'
      }
    ];
    const result = addFilter({}, expressionList);
    assert.equal(result.criteria.criteriaList.length, 1);
    assert.equal(result.criteria.criteriaList[0].expressionList.length, 3);
  });

  test('adFilter returns others if no file hash', function(assert) {

    const expressionList = [
      {
        propertyName: 'fileHash1',
        propertyValues: [{ value: 1 }],
        restrictionType: 'IN'
      }
    ];
    const result = addFilter({}, expressionList);
    assert.equal(result.criteria.criteriaList.length, 1);
    assert.equal(result.criteria.criteriaList[0].expressionList.length, 1);
  });


  test('adFilter returns criteria list', function(assert) {

    const expressionList = [
      {
        propertyName: 'fileName',
        propertyValues: [{ value: 1 }],
        restrictionType: 'IN'
      },
      {
        propertyName: 'fileHash',
        propertyValues: [{ value: 1 }],
        restrictionType: 'IN'
      }
    ];
    const result = addFilter({}, expressionList);
    assert.equal(result.criteria.criteriaList.length, 2);
    assert.equal(result.criteria.criteriaList[0].expressionList.length, 1);
    assert.equal(result.criteria.criteriaList[1].expressionList.length, 3);
  });
});
