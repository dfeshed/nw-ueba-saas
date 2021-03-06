import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import { constructFilterQueryString, selectedFilterItemsArray, isWindowsAgent } from 'investigate-process-analysis/reducers/process-filter/selectors';

module('Unit | Selectors | process-filter', function() {

  test('Returns a constructed query to filter the events table', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processFilter: {
          filter: {
            action: ['createProcess', 'openProcess'],
            category: ['network', 'file']
          }
        }
      }
    });
    const expectedResult = [
      { value: "(action='createProcess'||action='openProcess')" },
      { value: "(category='network'||category='file')" }
    ];
    const result = constructFilterQueryString(state);
    assert.deepEqual(result, expectedResult);
  });

  test('Creates an array with all the selected filter items', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processFilter: {
          filter: {
            action: ['createProcess', 'openProcess'],
            category: ['network', 'file']
          }
        }
      }
    });

    const expectedResult = ['createProcess', 'openProcess', 'network', 'file'];
    const result = selectedFilterItemsArray(state);
    assert.deepEqual(result, expectedResult);
  });

  test('if the agent is window or not', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processProperties: {
          hostDetails: [{
            machineOsType: 'windows'
          }
          ]
        }
      }
    });
    const result = isWindowsAgent(state);
    assert.deepEqual(result, true);
  });

  test('isWindowsAgent when host details is absent', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processProperties: {
          hostDetails: []
        }
      }
    });
    const result = isWindowsAgent(state);
    assert.deepEqual(result, false);

    const state2 = Immutable.from({
      processAnalysis: {
        processProperties: {}
      }
    });
    const result2 = isWindowsAgent(state2);
    assert.deepEqual(result2, false);
  });
});