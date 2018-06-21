import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  hasError,
  isStreaming,
  errorMessage,
  queryInput,
  children,
  selectedProcess,
  eventsData,
  eventsTableConfig,
  eventsSortField,
  eventsCount,
  eventsFilteredCount
} from 'investigate-process-analysis/reducers/process-tree/selectors';

module('Unit | Selectors | process-tree', function() {

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

  test('queryInput', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          queryInput: {
            sid: '1',
            pn: 'test'
          }
        }
      }
    });
    const result = queryInput(state);
    assert.equal(result.sid, '1');
  });
  test('children', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          queryInput: {
            sid: '1',
            pn: 'test'
          },
          rawData: new Array(10)
        }
      }
    });
    const result = children(state);
    assert.equal(result.length, 10);
  });

  test('selectedProcess', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          selectedProcess: {
            processName: 'test'
          },
          queryInput: {
            sid: '1',
            vid: '123'
          },
          rawData: new Array(10)
        }
      }
    });
    const result = selectedProcess(state);
    assert.equal(result.processName, 'test');
  });

  test('eventsData', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          eventsData: new Array(10)
        }
      }
    });
    const result = eventsData(state);
    assert.equal(result.length, 10);
  });
  test('eventsTableConfig', function(assert) {
    const result = eventsTableConfig();
    assert.equal(result.length, 11);
  });
  test('eventsSortField', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          eventsSortField: { field: 'event.time' }
        }
      }
    });
    const result = eventsSortField(state);
    assert.equal(result.field, 'event.time');
  });

  test('eventCount', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          eventsCount: 10
        }
      }
    });
    const result = eventsCount(state);
    assert.equal(result, 10);
  });

  test('eventsFilteredCount', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          eventsFilteredCount: 10
        }
      }
    });
    const result = eventsFilteredCount(state);
    assert.equal(result, 10);
  });
});

