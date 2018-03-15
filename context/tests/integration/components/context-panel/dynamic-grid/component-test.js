import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import dataSourceDetails from 'context/config/machines';
import * as ACTION_TYPES from 'context/actions/types';


moduleForComponent('context-panel/dynamic-grid', 'Integration | Component | context panel/dynamic grid', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
  }
});
// TODO: skipping this test for now, as need to change input data according to the dynamic grid
test('Testing grid rendered', function(assert) {
  const contextData = {
    dataSourceGroup: 'Machines',
    resultList: [{
      'OperatingSystem': 'Microsoft Windows Server 2012 R2 Standard',
      'Platform': '64-bit (x64)',
      'DNS': '10.100.174.10',
      'LastSeen': '5/15/2015 4: 06: 28 AM',
      'LocalIPAddress': '10.101.47.53',
      'LastScan': '5/14/2015 9: 00: 51 AM',
      'FirewallDisabled': 'False',
      'Gateway': '10.101.46.1',
      'NetworkSegment': '10.101.47.0',
      'MachineID': '00000000-0000-0000-0000-000000000000',
      'Comment': '',
      'AdminStatus': '',
      'UserName': '',
      'MachineStatus': 'Online',
      'IIOCScore': '1024',
      'IIOCLevel2': '7',
      'IIOCLevel3': '21',
      'BootTime': '5/12/2015 3: 57: 36 AM',
      'IIOCLevel0': '1',
      'IIOCLevel1': '5',
      'MAC': '84: 8F: 69: E7: D1: 3A',
      'VersionInfo': '4.1.0.0',
      'TimeZone': 'Eastern Standard Time',
      'Serial': '5BQZPW1',
      'MachineName': 'BED-ECAT-APP-02',
      'ServicePackOS': '0',
      'OrganizationUnit': '',
      'Country': 'USA',
      'Online': 'True'
    }]
  };
  const dataSources = {
    'id': '58be7d121969557cbbdbfc57',
    'name': '58be7d121969557cbbdbfc55_Incidents_datasource',
    'isConfigured': true,
    'description': null,
    'type': 'Endpoint',
    'dataSourceType': 'Endpoint',
    details: {
      Machines: {
        'isConfigured': true,
        'description': null,
        'type': 'Machines',
        'dataSourceGroup': 'Machines',
        'maxSize': -1,
        'maxAgeOfEntriesInSeconds': -1,
        'maxStorageSize': -1,
        'maxCacheMemorySize': -1,
        'contentVersion': 13,
        'contentLastModifiedTime': 1488906967222
      }
    }
  };

  this.get('redux').dispatch({
    type: ACTION_TYPES.INITIALIZE_CONTEXT_PANEL,
    payload: {
      lookupKey: '1.1.1.1',
      meta: 'IP'
    }
  });
  this.get('redux').dispatch({
    type: ACTION_TYPES.GET_ALL_DATA_SOURCES,
    payload: [dataSources]
  });
  this.get('redux').dispatch({
    type: ACTION_TYPES.GET_LOOKUP_DATA,
    payload: [contextData]
  });
  this.get('redux').dispatch({
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: 'Endpoint'
  });

  this.set('contextData', contextData);
  this.set('dataSourceDetails', dataSourceDetails);
  this.render(hbs `{{context-panel/dynamic-grid contextData=contextData dataSourceDetails=dataSourceDetails }}`);

  assert.equal(this.$('.value').length, 10, 'Testing count of fields rendered');
  assert.equal(this.$('.rsa-context-panel__grid a').length, 1, 'Testing count of link fields rendered');
  assert.equal(this.$('.rsa-context-panel__grid__risk-badge__default').length, 1, 'Testing count of risk score fields rendered');
});
