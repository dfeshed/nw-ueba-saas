import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  processProperties,
  osType,
  rootHash,
  hasProperties,
  processDetails,
  processExecutionConfig,
  processHostCount
} from 'investigate-process-analysis/reducers/process-properties/selectors';

module('Unit | Selectors | process-properties', function() {

  test('processDetails returns the data for rendering properties', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processProperties: {
          hostDetails: [{
            operatingSystem: 'windows'
          }]
        }
      }
    });

    const data = processProperties(state);
    assert.equal(data.operatingSystem, 'windows');
  });
  test('osType returns the osType for rendering properties', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          queryInput: {
            osType: 'linux'
          }
        }
      }
    });

    const data = osType(state);
    assert.equal(data, 'linux');
  });

  test('rootHash returns the hash of the root node', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          queryInput: {
            checksum: 'f6066162e9e4f279d2d5f0c207c209ae8c2b13fc7555186f32c7e8ce49eb52a2'
          }
        }
      }
    });

    const data = rootHash(state);
    assert.equal(data, 'f6066162e9e4f279d2d5f0c207c209ae8c2b13fc7555186f32c7e8ce49eb52a2');
  });

  test('hasProperties returns the hash of the root node', function(assert) {
    const state1 = Immutable.from({
      processAnalysis: {
        processProperties: {
          hostDetails: [
            { checksumSha256: 'xyz' }
          ]
        }
      }
    });

    const state2 = Immutable.from({
      processAnalysis: {
        processProperties: {
          hostDetails: []
        }
      }
    });

    const test1 = hasProperties(state1);
    const test2 = hasProperties(state2);

    assert.equal(test1, true);
    assert.equal(test2, false);
  });

  test('processDetails returns the modified process details', function(assert) {
    // Matching src
    let selectedProcess = {
      processId: 1,
      processVidSrc: 1,
      paramSrc: 'SRC',
      paramDst: 'DST',
      directoryDst: 'd:/',
      directorySrc: 'c:/'
    };
    let result = processDetails(selectedProcess);
    assert.equal(result.paramDst, 'SRC');
    assert.equal(result.directoryDst, 'c:/');

    // Matching dst
    selectedProcess = {
      processId: 1,
      processVidSrc: 2,
      paramSrc: 'SRC',
      paramDst: 'DST',
      directoryDst: 'd:/',
      directorySrc: 'c:/'
    };
    result = processDetails(selectedProcess);
    assert.equal(result.paramDst, 'DST');
    assert.equal(result.directoryDst, 'd:/');
  });


  test('processExecutionConfig', function(assert) {
    const expectedResult =
      [
        {
          'field': 'eventTime',
          'format': 'DATE'
        },
        {
          'field': 'processName'
        },
        {
          'field': 'directoryDst'
        },
        {
          'field': 'checksum'
        },
        {
          'field': 'sessionId'
        },
        {
          'field': 'userAll'
        },
        {
          'field': 'paramDst'
        }
      ];
    const data = processExecutionConfig();
    assert.deepEqual(data, expectedResult);
  });

  test('processHostCount returns the hostCount', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processProperties: {
          hostDetails: [
            { hostCount: 2 }
          ]
        }
      }
    });

    const test = processHostCount(state);
    assert.equal(test, 2);
  });

});
