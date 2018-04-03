
import { contextDataParser } from 'dummy/helpers/context-data-parser';
import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import ipData from '../../data/subscriptions/context/stream/data/ip';
import userData from '../../data/subscriptions/context/stream/data/user';
import _ from 'lodash';

let lookupdDataObj = null;
let lookupData = null;
let timeNow = new Date().getTime();

module('Unit | Helper | context data parser', function(hooks) {

  hooks.beforeEach(function() {
    lookupdDataObj = Immutable.from([{}]);
    lookupData = contextDataParser([ipData, lookupdDataObj]);
  });

  test('Test lookup data present for all Data Sources', function(assert) {
    assert.ok(lookupData.Alerts);
    assert.equal(lookupData.Alerts.resultList.length, 15, 'Should have 15 alerts');
    assert.ok(lookupData.Incidents);
    assert.equal(lookupData.Incidents.resultList.length, 14, 'Should have 15 Incedents');
    assert.ok(lookupData.Archer);
    assert.equal(lookupData.Archer.resultList.length, 1, 'Should have device details');
    assert.ok(lookupData.IOC);
    assert.equal(lookupData.IOC.resultList.length, 7, 'Should have 15 alerts');
    assert.ok(lookupData.Machines);
    assert.equal(lookupData.Machines.resultList.length, 1, 'Should have Machines details');
    assert.ok(lookupData.Modules);
    assert.equal(lookupData.Modules.resultList.length, 5, 'Should have 15 Modules');
    assert.ok(lookupData.LIST);
    assert.equal(lookupData.LIST.resultList.length, 5, 'Should have 5 LIST');
  });

  test('Enrich Machines module count', function(assert) {
    assert.equal(lookupData.Machines.resultList[0].total_modules_count, 3642, 'Should show proper modules count');
  });

  test('Enrich Modules IIOC header', function(assert) {
    assert.equal(lookupData.Modules.header, ' (IIOC Score > 500)', 'Should show proper IIOC header based on server value.');
  });

  test('Enrich user data', function(assert) {
    lookupData = contextDataParser([userData, lookupdDataObj]);
    assert.equal(lookupData.Users.resultList.length, 5, 'Should show 5 users details');
    const [firstUserDetails] = lookupData.Users.resultList;
    assert.equal(firstUserDetails.location, ' Bangalore KA IN ', 'Should show proper location');
    assert.equal(firstUserDetails.lastLogonTimestamp, '1491568464977.4648', 'Should show proper lastLogonTimestamp in milisecond');
    assert.equal(firstUserDetails.lastLogon, '1491568464977.4648', 'Should show proper lastLogon in milisecond');
  });

  test('Test lookup data for new List Entry should show latest updated time for list modification', function(assert) {
    assert.equal(lookupData.LIST.resultMeta.timeQuerySubmitted, 1522649579564, 'Should show latest modified time');
    const data = _.clone(ipData);
    data.push({
      'dataSourceName': 'LogFile1',
      'dataSourceDescription': 'LogFile-Desc1',
      'dataSourceType': 'LIST',
      'dataSourceGroup': 'LIST',
      'dataSourceLastModifiedOn': timeNow,
      'contentLastModifiedOn': 1522649579764,
      'resultList': [
        {
          'createdBy': 'admin',
          'createdOn': 1491288661876,
          'lastModifiedOn': 1491288661876,
          'id': 'ad48ecc7-a034-4be2-a01f-63b64150aad2',
          'data': {
            'LIST': '10.101.47.107'
          }
        }
      ],
      'resultMeta': {
        'timeQuerySubmitted': 1491289231023,
        'dataSourceCreatedBy': 'Administrator'
      }
    });
    lookupData = contextDataParser([data, lookupdDataObj]);
    assert.equal(lookupData.LIST.resultMeta.timeQuerySubmitted, timeNow, 'Should show latest modified time');

  });

  test('Test lookup data for new List Entry should show latest updated time if content is modified', function(assert) {
    timeNow = new Date().getTime();
    const data = _.clone(ipData);
    data.push({
      'dataSourceName': 'LogFile1',
      'dataSourceDescription': 'LogFile-Desc1',
      'dataSourceType': 'LIST',
      'dataSourceGroup': 'LIST',
      'dataSourceLastModifiedOn': 1522649575764,
      'contentLastModifiedOn': timeNow,
      'resultList': [
        {
          'createdBy': 'admin',
          'createdOn': 1491288661876,
          'lastModifiedOn': 1491288661876,
          'id': 'ad48ecc7-a034-4be2-a01f-63b64150aad2',
          'data': {
            'LIST': '10.101.47.107'
          }
        }
      ],
      'resultMeta': {
        'timeQuerySubmitted': 1491289231023,
        'dataSourceCreatedBy': 'Administrator'
      }
    });
    lookupData = contextDataParser([data, lookupdDataObj]);
    assert.equal(lookupData.LIST.resultMeta.timeQuerySubmitted, timeNow, 'Should show latest modified time');

  });

});
