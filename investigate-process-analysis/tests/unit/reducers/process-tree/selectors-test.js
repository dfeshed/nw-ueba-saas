import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  treeData,
  hasError,
  isStreaming,
  errorMessage
} from 'investigate-process-analysis/reducers/process-tree/selectors';

module('Unit | Selectors | process-tree', function() {

  test('treeData returns the data for rendering tree', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          rootNode: {
            processName: 'test'
          }
        }
      }
    });

    const data = treeData(state);
    assert.equal(data.processName, 'test');
  });

  test('isStreaming', function(assert) {
    let state = Immutable.from({
      processAnalysis: {
        processTree: {
          streaming: false
        }
      }
    });

    let streaming = isStreaming(state);
    assert.equal(streaming, false);

    state = Immutable.from({
      processAnalysis: {
        processTree: {
          streaming: true
        }
      }
    });

    streaming = isStreaming(state);
    assert.equal(streaming, true);
  });

  test('hasError', function(assert) {
    let state = Immutable.from({
      processAnalysis: {
        processTree: {
          error: null
        }
      }
    });

    let error = hasError(state);
    assert.equal(error, false);

    state = Immutable.from({
      processAnalysis: {
        processTree: {
          error: 'xyz'
        }
      }
    });

    error = hasError(state);
    assert.equal(error, true);
  });

  test('errorMessage', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          error: 'failed'
        }
      }
    });

    const message = errorMessage(state);
    assert.equal(message, 'failed');
  });
});

