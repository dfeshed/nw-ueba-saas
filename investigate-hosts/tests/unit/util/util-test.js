import { module, test } from 'qunit';

import { generateColumns } from 'investigate-hosts/util/util';

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
  assert.equal(generateColumns(customColumns, defaultColumns),
               createExpectedResult(customColumns, defaultColumns),
               'Generate OS specific Columns');
});