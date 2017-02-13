
import { parseADData } from 'dummy/helpers/parse-addata';
import { module, test } from 'qunit';

module('Unit | Helper | parse addata');

// Replace this with your real tests.
test('it works', function(assert) {
  const usersData = [{
    'uSNChanged': '465878',
    'manager': 'CN=sumithra m,CN=Users,DC=saserver,DC=local',
    'sAMAccountName': 'Administrator',
    'displayName': 'AdministratorDisplayName',
    'postalCode': '560056',
    'givenName': 'AdministratorFirstName',
    'objectClass': [
      'top',
      'person',
      'organizationalPerson',
      'user'
    ],
    'description': 'Built-in account for administering the computer/domain',
    'cn': 'Administrator',
    'title': 'AdministratorJobTitle',
    'countryCode': '0',
    'primaryGroupID': '513',
    'sAMAccountType': '805306368',
    'name': 'Administrator',
    'objectGUID': 'LwkCa55hzkqL2LCQlnVxgA==',
    'objectSid': 'AQUAAAAAAAUVAAAAe5QUeKjaINGBAqPp9AEAAA==',
    'company': 'AdministratorCompany',
    'memberOf': [
      'CN=svgroup,CN=Users,DC=saserver,DC=local',
      'CN=25NOVGroup,CN=Users,DC=saserver,DC=local',
      'CN=Group Policy Creator Owners,CN=Users,DC=saserver,DC=local',
      'CN=Domain Admins,CN=Users,DC=saserver,DC=local',
      'CN=Enterprise Admins,CN=Users,DC=saserver,DC=local',
      'CN=Schema Admins,CN=Users,DC=saserver,DC=local',
      'CN=Administrators,CN=Builtin,DC=saserver,DC=local'
    ],
    'sn': 'AdministratorLastName',
    'department': 'AdministratorDepartment',
    'pwdLastSet': '130802340489843750'
  }];
  const result = parseADData([usersData]);
  assert.equal(result[0].managerName, 'sumithra m ');
});

