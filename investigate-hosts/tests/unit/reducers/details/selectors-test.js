import { module, test } from 'qunit';
import { hostDetails } from '../../state/state';
import Immutable from 'seamless-immutable';

import {
  hasScanTime,
  getColumnsConfig,
  hostDetailPropertyTabs,
  downloadLink,
  updateConfig,
  savedColumnsConfig
} from 'investigate-hosts/reducers/details/selectors';

module('Unit | selectors | details');

test('hasScanTime', function(assert) {
  let result = hasScanTime(Immutable.from({ endpoint: { detailsInput: { snapShots: [11231231, 12312311] } } }));
  assert.equal(result, true, 'should return true as some snapshots are available');
  result = hasScanTime(Immutable.from({ endpoint: { detailsInput: { snapShots: [] } } }));
  assert.equal(result, false, 'should return true as some snapshots are available');
});

test('Get OS specific column config', function(assert) {
  const columnConfig = Immutable.from({
    mac: [{
      field: 'fileName',
      title: 'investigateHosts.process.dll.dllName',
      width: 84
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
      width: 89
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
      width: 86
    }]
  });

  const linuxConfig = columnConfig.linux;
  const state = { preferences: { preferences: {} }, endpoint: { overview: { hostDetails } } };

  assert.equal(getColumnsConfig(state, columnConfig), linuxConfig, 'Should return OS specific config (linux)');
});

test('hostDetailPropertyTabs', function(assert) {
  const result = hostDetailPropertyTabs(Immutable.from({
    endpoint: {
      detailsInput: {
        activeHostDetailPropertyTab: 'RISK'
      }
    }
  }));

  assert.equal(result.findBy('name', 'RISK').selected, true, 'RISK Tab should be selected');
});
test('downloadLink for save local copy', function(assert) {
  const state = Immutable.from({
    endpoint: {
      detailsInput: {
        downloadLink: '/test/test.zip'
      }
    }
  });
  const result = downloadLink(state);
  assert.equal(result.includes('/test/test.zip'), true);
});
test('downloadLink for save local copy when null', function(assert) {
  const state = Immutable.from({
    endpoint: {
      detailsInput: {
        downloadLink: null
      }
    }
  });
  const result = downloadLink(state);
  assert.equal(result, null);
});


test('updateConfig updates the schema based on saved config', function(assert) {
  const schema = [{
    field: 'fileName',
    title: 'investigateHosts.process.dll.dllName',
    width: 89
  },
  {
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: 100
  }];
  const result = updateConfig(schema, [{ columns: [
    {
      field: 'timeCreated',
      width: '2vw'
    }
  ] }]);
  assert.equal(result[1].width, '2vw');
  assert.equal(result[0].width, 89);
});


test('updateConfig updates the schema based on saved config', function(assert) {
  const schema = [{
    field: 'fileName',
    title: 'investigateHosts.process.dll.dllName',
    width: 89
  },
  {
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: 100
  }];
  const result = updateConfig(schema, []);
  assert.equal(result[1].width, 100);
  assert.equal(result[0].width, 89);
});


test('savedColumnsConfig will extract the columns', function(assert) {
  const state = Immutable.from({
    preferences: {
      preferences: {
        machinePreference: {
          columnConfig: [
            {
              tableId: 'FILE',
              columns: [
                {
                  field: 'test'
                }
              ]
            },
            {
              tableId: 'DRIVER',
              columns: [
                {
                  field: 'test'
                }
              ]
            }
          ]
        }
      }
    }
  });
  const result = savedColumnsConfig(state, 'FILE');
  assert.equal(result[0].tableId, 'FILE');
});

test('savedColumnsConfig returns empty columns', function(assert) {
  const state = Immutable.from({
    preferences: {
      preferences: {
        filePreference: {
          columnConfig: [
            {
              tableId: 'FILE',
              columns: [
                {
                  field: 'test'
                }
              ]
            }
          ]
        }
      }
    }
  });
  const result = savedColumnsConfig(state, 'FILE');
  assert.equal(result.length, 0);
});
