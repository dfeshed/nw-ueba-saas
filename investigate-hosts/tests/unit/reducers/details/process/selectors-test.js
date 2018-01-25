import { module, test } from 'qunit';
import { processDetailsData, processTreeData, processListData } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | process');

import {
  getProcessData,
  processTree,
  isNavigatedFromExplore,
  noProcessData
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

test('isNavigatedFromExplore', function(assert) {
  const result = isNavigatedFromExplore(Immutable.from({
    endpoint: {
      process: {
        processList: processListData,
        processTree: processTreeData
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.equal(result, true);
});

test('noProcessData returns true when processTree is empty', function(assert) {
  const result = noProcessData(Immutable.from({
    endpoint: {
      process: {
        processTree: [],
        processList: []
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('noProcessData returns false when processTree is not empty', function(assert) {
  const result = noProcessData(Immutable.from({
    endpoint: {
      process: {
        processTree: processTreeData,
        processList: processListData
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.deepEqual(result, false);
});