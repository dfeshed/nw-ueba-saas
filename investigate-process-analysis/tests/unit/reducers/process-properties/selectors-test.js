import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  processProperties,
  osType,
  rootHash
} from 'investigate-process-analysis/reducers/process-properties/selectors';

module('Unit | Selectors | process-properties', function() {

  test('processDetails returns the data for rendering properties', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processProperties: {
          hostDetails: {
            operatingSystem: 'windows'
          }
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

});