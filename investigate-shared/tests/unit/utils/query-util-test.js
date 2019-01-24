import { module, test } from 'qunit';

import { addFilter, parseQueryString, isValidIPV6 } from 'investigate-shared/utils/query-util';

module('Unit | Utils | Query Util', function() {

  test('Parse the query string of IP related query params', function(assert) {
    // when ip.src is valid ipv4
    let queryString = 'ip.src%20%3D%201.1.1.1';
    let [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString.propertyName, 'machineIdentity.networkInterfaces.ipv4');
    assert.equal(parsedString.propertyValues[0].value, '1.1.1.1');
    assert.equal(parsedString.restrictionType, 'IN');

    // when alias.ip is valid ipv6
    queryString = 'alias.ip%20%3D%20fe80::49f4:9ee4:c2ae:ef0a';
    [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString.propertyName, 'machineIdentity.networkInterfaces.ipv6');
    assert.equal(parsedString.propertyValues[0].value, 'fe80::49f4:9ee4:c2ae:ef0a');
    assert.equal(parsedString.restrictionType, 'IN');
  });

  test('Parse the query string of alias.host query param', function(assert) {
    const queryString = 'alias.host%20%3D%20%27Test_Machine%27';
    const [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString.propertyName, 'machineIdentity.machineName');
    assert.equal(parsedString.propertyValues[0].value, 'Test_Machine');
    assert.equal(parsedString.restrictionType, 'IN');
  });

  test('Parse the query string of alias.mac query param', function(assert) {
    const queryString = 'alias.mac%20%3D%2000:50:56:01:1B:BC';
    const [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString.propertyName, 'machineIdentity.networkInterfaces.macAddress');
    assert.equal(parsedString.propertyValues[0].value, '00:50:56:01:1B:BC');
    assert.equal(parsedString.restrictionType, 'IN');
  });

  test('Parse the query string of checksum query param', function(assert) {
    const queryString = 'checksum%20%3D%20568c5cbf9877f6b9e39d1e7ca0ff0a36';
    const [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString.propertyName, 'fileHash');
    assert.equal(parsedString.propertyValues[0].value, '568c5cbf9877f6b9e39d1e7ca0ff0a36');
    assert.equal(parsedString.restrictionType, 'IN');
  });

  test('Parse the query string of filename query param', function(assert) {
    const queryString = 'filename%20%3D%20lsass.exe';
    const [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString.propertyName, 'firstFileName');
    assert.equal(parsedString.propertyValues[0].value, 'lsass.exe');
    assert.equal(parsedString.restrictionType, 'IN');
  });

  test('Parse the query string when query is empty', function(assert) {
    const queryString = '';
    const [parsedString] = parseQueryString(queryString);
    assert.equal(parsedString, undefined, 'should return undefined when query is empty.');
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

  test('adds IS_NULL expression when notDownloaded is selected', function(assert) {
    const originalExpressionList = [
      {
        propertyName: 'downloadInfo.status',
        propertyValues: [{ value: 'Error' }, { value: 'NotDownloaded' }],
        restrictionType: 'IN'
      }
    ];
    const result = addFilter({}, originalExpressionList);
    assert.equal(result.criteria.criteriaList.length, 1);
    const [{ expressionList }] = result.criteria.criteriaList;
    assert.equal(expressionList.length, 2);
    assert.equal(expressionList[0].restrictionType, 'IN', 'restriction type is IN');
    assert.equal(expressionList[0].propertyValues.length, 2, 'Contains Error and NotDownloaded');
    assert.equal(expressionList[1].restrictionType, 'IS_NULL', 'is null restriction type is added for NotDownloaded');
  });

  test('adds IS_NULL expression is not added when notDownloaded is not selected', function(assert) {
    const originalExpressionList = [
      {
        propertyName: 'downloadInfo.status',
        propertyValues: [{ value: 'error' }, { value: 'downloaded' }],
        restrictionType: 'IN'
      }
    ];
    const result = addFilter({}, originalExpressionList);
    assert.equal(result.criteria.criteriaList.length, 1);
    const [{ expressionList }] = result.criteria.criteriaList;
    assert.equal(expressionList.length, 1);
    assert.equal(expressionList[0].propertyValues.length, 2, 'Contains only error and downloaded');
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

  test('isValidIPV6 returns true for valid ipv6', function(assert) {
    const value = 'fe80::49f4:9ee4:c2ae:ef0a';
    const result = isValidIPV6(value);
    assert.ok(result);
  });

  test('isValidIPV6 returns false for empty/invalid ipv6', function(assert) {
    let value = null;
    let result = isValidIPV6(value);
    assert.notOk(result);

    value = '1.2.3.4';
    result = isValidIPV6(value);
    assert.notOk(result);
  });

  test('addFilter will add, machine.agentVersion to expresionList', function(assert) {

    const expressionList = [
      {
        propertyName: 'machineIdentity.agentVersion',
        propertyValues: [{ value: 11.1 }],
        restrictionType: 'IN'
      }
    ];
    const result = addFilter({}, expressionList);
    assert.equal(result.criteria.criteriaList.length, 1);
    assert.equal(result.criteria.criteriaList[0].expressionList.length, 2);
  });
});
