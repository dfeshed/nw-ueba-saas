import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { listOfHostNames } from 'investigate-process-analysis/reducers/host-context/selectors';

module('Unit | selectors | host-context', function() {

  test('it returns the list of host names', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        hostContext: {
          hostList: {
            data: [
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC', 'hostname': 'windows', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAB', 'hostname': 'mac', 'score': 0 },
              { 'agentId': '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAD', 'hostname': 'linux', 'score': 0 }
            ]
          },
          isLoading: false
        }
      }
    });
    const result = listOfHostNames(state);
    assert.deepEqual(result.length, 3, 'Expected to return 3 host names');
  });
});
