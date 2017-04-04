import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import dSDetails from 'context/config/machines';


moduleForComponent('context-panel/dynamic-grid', 'Integration | Component | context panel/dynamic grid', {
  integration: true
});
// TODO: skipping this test for now, as need to change input data according to the dynamic grid
test('Testing grid rendered', function(assert) {
  const contextData = { data: [{
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
  } ] };


  this.set('contextData', contextData);
  this.set('dSDetails', dSDetails);
  this.render(hbs`{{context-panel/dynamic-grid contextData=contextData dSDetails=dSDetails }}`);

  assert.equal(this.$('.rsa-context-panel__grid__heading-text').length, 1, 'Testing count of grid header rendered');
  assert.equal(this.$('.rsa-context-panel__grid__host-details__field-value').length, 11, 'Testing count of fields rendered');
  assert.equal(this.$('.rsa-context-panel__grid a').length, 1, 'Testing count of link fields rendered');
  assert.equal(this.$('.rsa-context-risk-score').length, 1, 'Testing count of risk score fields rendered');
});