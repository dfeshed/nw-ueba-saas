import { module, test } from 'qunit';
import { hostDetails } from '../../state/state';
import Immutable from 'seamless-immutable';

import {
  hasScanTime,
  getColumnsConfig
} from 'investigate-hosts/reducers/details/selectors';

module('Unit | selectors | details');

test('areSomeScanning', function(assert) {
  const result = hasScanTime(Immutable.from({ endpoint: { detailsInput: { snapShots: [11231231, 12312311] } } }));
  assert.equal(result, true, 'should return true as some snapshots are available');
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
  const state = { endpoint: { overview: { hostDetails } } };

  assert.equal(getColumnsConfig(state, columnConfig), linuxConfig, 'Should return OS specific config (linux)');
});