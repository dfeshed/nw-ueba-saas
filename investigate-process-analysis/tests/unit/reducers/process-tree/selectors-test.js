import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  hasError,
  isStreaming,
  errorMessage,
  rootProcess,
  queryInput,
  children,
  selectedProcess,
  eventsData,
  eventsTableConfig,
  eventsSortField,
  eventsCount
} from 'investigate-process-analysis/reducers/process-tree/selectors';

module('Unit | Selectors | process-tree', function() {

  test('rootProcess returns root', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processTree: {
          queryInput: {
            pn: 'test'
          }
        }
      }
    });

    const data = rootProcess(state);
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
    assert.equal(result.length, 12);
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
          eventsData: new Array(10)
        }
      }
    });
    const result = eventsCount(state);
    assert.equal(result, 10);
  });
});

