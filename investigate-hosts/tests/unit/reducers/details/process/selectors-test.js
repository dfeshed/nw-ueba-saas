import { module, test } from 'qunit';
import { processDetailsData, processTreeData, processListData } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | process');

import {
  getProcessData,
  processTree
} from 'investigate-hosts/reducers/details/process/selectors';

test('getProcessData', function(assert) {
  const result = getProcessData(Immutable.from({
    endpoint: {
      process: {
        processDetails: processDetailsData
      }
    }
  }));
  assert.equal(result.process.signature, 'valid,signed,Apple');
});

test('processTree', function(assert) {
  const result = processTree(Immutable.from({
    endpoint: {
      process: {
        processList: processListData,
        processTree: processTreeData
      },
      explore: {
        selectedTab: 'process'
      }
    }
  }));
  assert.equal(result.length, 3);
});
