import { module, test } from 'qunit';

import { addFilter, parseQueryString } from 'investigate-shared/utils/query-util';

module('Unit | Utils | Query Util', function() {

  test('Parse the query string', function(assert) {
    let queryString = 'ip.src%20%3D%201.1.1.1';
    const [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString.propertyName, 'machine.networkInterfaces.ipv4');
    assert.equal(parsedString.propertyValues[0].value, '1.1.1.1');

    queryString = 'alias.host%20%3D%20%27Test_Machine%27';
    const [newParsedString] = parseQueryString(queryString);
    assert.equal(newParsedString.propertyName, 'machine.machineName');
    assert.equal(newParsedString.propertyValues[0].value, 'Test_Machine');
  });

  test('addFilter split the hashes in to three columns', function(assert) {

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

  test('addFilter add one more expression to check null risk score when risk score filter is selected', function(assert) {

    const expressionList = [
      {
        propertyName: 'score',
        propertyValues: [{ value: 0 }, { value: 70 }],
        restrictionType: 'BETWEEN'
      }
    ];
    const result = addFilter({}, expressionList);
    assert.equal(result.criteria.criteriaList.length, 1);
    assert.equal(result.criteria.criteriaList[0].expressionList.length, 2);
  });

  test('addFilter returns others if no file hash', function(assert) {

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


  test('addFilter returns criteria list', function(assert) {

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
