import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { listOfHostNames } from 'investigate-process-analysis/reducers/host-context/selectors';

module('Unit | selectors | host-context', function() {

  test('it returns the list of host names', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        hostContext: {
          hostList: [
            {
              value: 'windows'
            },
            {
              value: 'mac'
            },
            {
              value: 'linux'
            }
          ],
          isLoading: false
        }
      }
    });
    const result = listOfHostNames(state);
    assert.deepEqual(result.length, 3, 'Expected to return 3 host names');
  });
});
