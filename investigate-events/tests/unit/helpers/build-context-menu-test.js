import { module, test } from 'qunit';
import {
  _buildInvestigateUrl,
  _buildHostsUrl,
  buildContextMenu
} from 'investigate-events/helpers/build-context-menu';

module('Unit | Utils | Build Context Menu');

test('it builds the correct investigation drill URL', function(assert) {
  const selection = {
    metaName: 'ip.src',
    metaValue: '1.1.1.1'
  };
  const contextDetails = {
    endpointId: 'service-1',
    startTime: 1510768478,
    endTime: 1510768488,
    queryConditions: [
      {
        meta: 'ip.dst',
        value: '2.2.2.2',
        operator: '='
      },
      {
        meta: 'device.type',
        value: 'cisco',
        operator: '='
      }
    ],
    language: [
      { metaName: 'ip.dst', format: 'IP4Addr' },
      { metaName: 'ip.src', format: 'IP4Addr' },
      { metaName: 'device.type', format: 'Text' } ]
  };
  const investigateUrl = _buildInvestigateUrl(selection, '=', contextDetails);
  const expected = '/investigation/endpointid/service-1/navigate/query/(ip.dst%2520%253D%25202.2.2.2%2520%2526%2526%2520device.type%2520%253D%2520\'cisco\')%2520%2526%2526%2520ip.src%2520%253D%25201.1.1.1/date/2017-11-15T17:54:38Z/2017-11-15T17:54:48Z';
  assert.equal(investigateUrl, expected, 'Investigate URL should be compiled properly');
});

test('it builds the correct investigation refocus URL', function(assert) {
  const selection = {
    metaName: 'ip.src',
    metaValue: '1.1.1.1'
  };
  const contextDetails = {
    endpointId: 'service-1',
    startTime: 1510768478,
    endTime: 1510768488,
    queryConditions: [
      {
        meta: 'ip.dst',
        value: '2.2.2.2',
        operator: '='
      },
      {
        meta: 'device.type',
        value: 'cisco',
        operator: '='
      }]
  };
  const investigateUrl = _buildInvestigateUrl(selection, '=', contextDetails, true);
  const expectedUrl = '/investigation/endpointid/service-1/navigate/query/ip.src%2520%253D%2520\'1.1.1.1\'/date/2017-11-15T17:54:38Z/2017-11-15T17:54:48Z';
  assert.equal(investigateUrl, expectedUrl, 'Investigate URL should be compiled properly');
});

test('it builds the correct Apply Drill in New Tab URL', function(assert) {
  const selection = {
    metaName: 'ip.src',
    metaValue: '1.1.1.1'
  };
  const contextDetails = {
    endpointId: 'service-1',
    startTime: 1510768478,
    endTime: 1510768488,
    queryConditions: [
      {
        meta: 'user',
        value: 'nt service\\mssqlserver',
        operator: '='
      }
    ]
  };
  const contextDetails2 = {
    endpointId: 'service-1',
    startTime: 1510768478,
    endTime: 1510768488,
    queryConditions: [
      {
        meta: 'user',
        value: 'nt service\\mssqlserver',
        operator: '='
      },
      {
        meta: 'user.all',
        value: 'NT Service\\MSSQLSERVER',
        operator: '='
      }
    ]
  };
  const contextDetails3 = {
    endpointId: 'service-1',
    startTime: 1510768478,
    endTime: 1510768488,
    queryConditions: [
      {
        meta: 'user.dst',
        value: '\'',
        operator: 'contains'
      }
    ]
  };
  let investigateUrl = _buildInvestigateUrl(selection, '=', contextDetails);
  let expected = '/investigation/endpointid/service-1/navigate/query/(user%2520%253D%2520\'nt%2520service%255Cmssqlserver\')%2520%2526%2526%2520ip.src%2520%253D%2520\'1.1.1.1\'/date/2017-11-15T17:54:38Z/2017-11-15T17:54:48Z';
  assert.equal(investigateUrl, expected, 'Correct Apply Drill in New Tab URL1');
  investigateUrl = _buildInvestigateUrl(selection, '=', contextDetails2);
  expected = '/investigation/endpointid/service-1/navigate/query/(user%2520%253D%2520\'nt%2520service%255Cmssqlserver\'%2520%2526%2526%2520user.all%2520%253D%2520\'NT%2520Service%255CMSSQLSERVER\')%2520%2526%2526%2520ip.src%2520%253D%2520\'1.1.1.1\'/date/2017-11-15T17:54:38Z/2017-11-15T17:54:48Z';
  assert.equal(investigateUrl, expected, 'Correct Apply Drill in New Tab URL2');
  investigateUrl = _buildInvestigateUrl(selection, '=', contextDetails3);
  expected = '/investigation/endpointid/service-1/navigate/query/(user.dst%2520contains%2520\'\'\')%2520%2526%2526%2520ip.src%2520%253D%2520\'1.1.1.1\'/date/2017-11-15T17:54:38Z/2017-11-15T17:54:48Z';
  assert.equal(investigateUrl, expected, 'Correct Apply Drill in New Tab URL3');
});

test('it builds hosts URL with the correct query', function(assert) {
  const selection = {
    metaName: 'ip.src',
    metaValue: '1.1.1.1'
  };
  const language = [{ metaName: 'ip.src', format: 'IP4Addr' }];
  const investigateUrl = _buildHostsUrl(selection, { language });
  const expectedUrl = '/investigate/hosts?query=ip.src%20%3D%201.1.1.1';
  assert.equal(investigateUrl, expectedUrl, 'Hosts URL should be correct');
});

test('it builds URL even when languages is null', function(assert) {
  const selection = {
    metaName: 'ip.src',
    metaValue: '1.1.1.1'
  };
  const investigateUrl = _buildHostsUrl(selection, { language: null });
  const expectedUrl = '/investigate/hosts?query=ip.src%20%3D%20\'1.1.1.1\'';
  assert.equal(investigateUrl, expectedUrl, 'URL should be correctly compiled');
});

test('it checks if all the menu items are present', function(assert) {
  const menuItems = buildContextMenu();
  assert.equal(menuItems.length, 8, 'No. of internal actions expected is 8');
  assert.equal(menuItems[7].subActions.length, 7, 'No. of external actions expected is 7');
});