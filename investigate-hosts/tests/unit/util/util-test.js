import { module, test } from 'qunit';

import { generateColumns, getSelectedAgentIds } from 'investigate-hosts/util/util';

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

test('Extract the agent id', function(assert) {
  const list1 = [{ id: 1, version: '4.4' }, { id: 2, version: '4.4' }];
  assert.equal(getSelectedAgentIds(list1).length, 0, 'All are legacy ecat agent');
  const list2 = [{ id: 1, version: '4.4' }, { id: 2, version: '11.1' }];
  assert.equal(getSelectedAgentIds(list2).length, 1, 'Some are legacy ecat agent');
  const list3 = [{ id: 1, version: '11.1' }, { id: 2, version: '11.1' }];
  assert.equal(getSelectedAgentIds(list3).length, 2, 'No legacy ecat agent');

});
