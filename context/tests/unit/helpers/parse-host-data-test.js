
import { parseHostData } from 'dummy/helpers/parse-host-data';
import { module, test } from 'qunit';

module('Unit | Helper | parse host data');

// Replace this with your real tests.

test('it works', function(assert) {

  const additionalData = { iocScore_gte: '500', total_modules_count: '3642' };
  const machinesData = {
    'OperatingSystem': 'Microsoft Windows Server 2012 R2 Standard',
    'Platform': '64-bit (x64)',
    'DNS': '10.100.174.10',
    'LastSeen': '5/15/2015 4:06:28 AM',
    'LocalIPAddress': '10.101.47.53',
    'LastScan': '5/14/2015 9:00:51 AM',
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
    'BootTime': '5/12/2015 3:57:36 AM',
    'IIOCLevel0': '1',
    'IIOCLevel1': '5',
    'MAC': '84:8F:69:E7:D1:3A',
    'VersionInfo': '4.1.0.0',
    'TimeZone': 'Eastern Standard Time',
    'Serial': '5BQZPW1',
    'MachineName': 'BED-ECAT-APP-02',
    'ServicePackOS': '0',
    'OrganizationUnit': '',
    'Country': 'USA',
    'Online': 'True'
  };

  const result = parseHostData([additionalData, machinesData]);
  assert.equal(result.total_modules_count, 3642);
});

