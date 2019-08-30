import { module, test } from 'qunit';

import {
  generateColumns,
  getSelectedAgentIds,
  isAlreadySelected,
  isolateMachineValidation
} from 'investigate-hosts/util/util';

module('Unit | Util');

test('Generate OS specific Columns', function(assert) {
  const customColumns = {
    mac: [{
      field: 'timeCreated',
      title: 'investigateHosts.process.creationTime',
      format: 'DATE',
      width: 100
    }],
    windows: [{
      field: 'timeCreated',
      title: 'investigateHosts.process.creationTime',
      format: 'DATE',
      width: 100
    }],
    linux: [] };

  const defaultColumns = [
    {
      field: 'fileName',
      title: 'investigateHosts.process.dll.dllName',
      width: 85
    }];

  const createExpectedResult = (customColumns, defaultColumns) => {
    for (const key in customColumns) {
      customColumns[key].unshift(defaultColumns[0]);
    }
    return customColumns;
  };
  /*
  Expected result format
  {
      mac: [{
        field: 'fileName',
        title: 'investigateHosts.process.dll.dllName',
        width: 85
      },
      {
        field: 'timeCreated',
        title: 'investigateHosts.process.creationTime',
        format: 'DATE',
        width: 100
      }],
      windows: [{
        field: 'fileName',
        title: 'investigateHosts.process.dll.dllName',
        width: 85
      },
      {
        field: 'timeCreated',
        title: 'investigateHosts.process.creationTime',
        format: 'DATE',
        width: 100
      }],
      linux: [{
        field: 'fileName',
        title: 'investigateHosts.process.dll.dllName',
        width: 85
      }]
  }
  */
  assert.equal(
    generateColumns(customColumns, defaultColumns),
    createExpectedResult(customColumns, defaultColumns),
    'Generate OS specific Columns'
  );
});

test('Extract the agent id', function(assert) {
  const list1 = [{ id: 1, version: '4.4', managed: true }, { id: 2, version: '4.4', managed: false }];
  assert.equal(getSelectedAgentIds(list1).length, 0, 'All are legacy ecat agents');
  const list2 = [{ id: 1, version: '4.4', managed: true }, { id: 2, version: '11.1', managed: false }];
  assert.equal(getSelectedAgentIds(list2).length, 0, 'has ecat and unmanaged agents');
  const list3 = [{ id: 1, version: '11.1', managed: false }, { id: 2, version: '11.1', managed: true }];
  assert.equal(getSelectedAgentIds(list3).length, 1, 'some agents are managed');
  const list4 = [{ id: 1, managed: false }, { id: 2, managed: true }];
  assert.equal(getSelectedAgentIds(list4).length, 0, 'list is empty because versions are undefined');
});

test('isAlreadySelected', function(assert) {
  const selectedItems = [{ id: 1 }, { id: 2 }];
  const result1 = isAlreadySelected(selectedItems, { id: 2 });
  assert.deepEqual(result1, { id: 2 });
  const result2 = isAlreadySelected(selectedItems, { id: 3 });
  assert.equal(result2, false);
});

test('IsolateMachineValidation', function(assert) {
  const value1 = '1.2.3.4, , 3ffe:1900:4545:3:200:f8ff:fe21:67cf';
  const result1 = isolateMachineValidation(value1);
  assert.deepEqual(result1, {
    isInvalidIPFormatPresent: false,
    listOfIPs: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
  });

  const value2 = '1.2.3.4, 00:0a:95:9d:68:16 ,3ffe:1900:4545:3:200:f8ff:fe21:67cf';
  const result2 = isolateMachineValidation(value2);
  assert.deepEqual(result2, {
    isInvalidIPFormatPresent: true,
    listOfIPs: ['1.2.3.4', '00:0a:95:9d:68:16', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
  });
});