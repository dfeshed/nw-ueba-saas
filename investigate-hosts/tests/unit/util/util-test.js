import { module, test } from 'qunit';

import {
  generateColumns,
  getSelectedAgentIds,
  isAlreadySelected,
  isolateMachineValidation,
  convertBytesIntoKbOrMb,
  filePathValidation,
  numberValidation
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

test('convertBytesIntoKbOrMb', function(assert) {
  const value1 = convertBytesIntoKbOrMb();
  assert.deepEqual(value1, { unit: 'KB', value: '1.00' }, '1 KB returned when no value is passed.');

  const value2 = convertBytesIntoKbOrMb(1524000);
  assert.deepEqual(value2, { unit: 'MB', value: '1.45' }, '1.45 MB returned when no value is passed.');

  const value3 = convertBytesIntoKbOrMb(1500);
  assert.deepEqual(value3, { unit: 'KB', value: '1.46' }, '1.46 KB returned when no value is passed.');

});

test('filePathValidation', function(assert) {
  const value1 = filePathValidation();
  assert.equal(value1, false, '');

  const value2 = filePathValidation('D:\\Folder\\Test.xMl', 'windows');
  assert.equal(value2, true, 'D:\\Folder\\Test.xMl, Valid Windows path');

  const value5 = filePathValidation('/folder/test.xml', 'windows', '\\');
  assert.equal(value5, false, '/folder/test.xml, invalid windows path');

  const value6 = filePathValidation('\\folder\\test.xml', 'windows');
  assert.equal(value6, false, '\\folder\\test.xml, invalid windows path');

  const value3 = filePathValidation('D:/folder/test.xml', 'linux', '/');
  assert.equal(value3, false, 'D:/folder/test.xml, invalid Linux path');

  const value4 = filePathValidation('/Folder/tEst.xMl', 'linux', '/');
  assert.equal(value4, true, '/Folder/tEst.xMl, Valid Linux path');

  const value7 = filePathValidation('/folder**/test.xml', 'linux', '/');
  assert.equal(value7, false, '/folder**/test.xml, invalid Linux path, more than 1 wildcard in the directory path');

  const value8 = filePathValidation('/folder/*test.*', 'linux', '/');
  assert.equal(value8, false, '/folder/*test.*, invalid Linux path, 2 wildcards in the file name');

  const value9 = filePathValidation('/folder/*test.*', 'linux', '/');
  assert.equal(value9, false, '/folder/, invalid Linux path, no file name present');

  const value10 = filePathValidation('D*\\**\\test.xml', 'windows', '\\');
  assert.equal(value10, false, 'D*\\**\\test.xml, invalid windows path, more than 1 wildcard in the directory path');

  const value11 = filePathValidation('D:\\folder\\*.*', 'windows', '\\');
  assert.equal(value11, false, 'D:\\folder\\*.*, invalid windows path, 2 wildcards in the file name');

  const value12 = filePathValidation('D:\\folder\\', 'windows');
  assert.equal(value12, false, 'D:\\folder\\, invalid windows path, no file name present');
});

test('numberValidation', function(assert) {
  const value1 = numberValidation('randomValue');
  assert.deepEqual(value1, { isInvalid: true, value: 'randomValue' }, 'Not a number');

  const value2 = numberValidation(1);
  assert.deepEqual(value2, { isInvalid: false, value: 1 }, 'Is a number');

  const value3 = numberValidation(0);
  assert.deepEqual(value3, { isInvalid: false, value: 0 }, 'Is a number');

  const value4 = numberValidation(0, { lowerLimit: 1, upperLimit: 10 });
  assert.deepEqual(value4, { isInvalid: true, value: 0 }, 'Not within range');

  const value5 = numberValidation(0, { lowerLimit: 1 });
  assert.deepEqual(value5, { isInvalid: true, value: 0 }, 'Not within range');

  const value6 = numberValidation(5, { lowerLimit: 1 });
  assert.deepEqual(value6, { isInvalid: false, value: 5 }, 'Within range');

  const value7 = numberValidation(5, { upperLimit: 10 });
  assert.deepEqual(value7, { isInvalid: false, value: 5 }, 'Within range');

  const value8 = numberValidation(5, { upperLimit: 1 });
  assert.deepEqual(value8, { isInvalid: true, value: 5 }, 'Not within range');
});