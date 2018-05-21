import { module, test } from 'qunit';
import {
  pillsData
} from 'investigate-events/reducers/investigate/next-gen/selectors';

module('Unit | Selectors | next-gen');

test('isEventResultsError is false when status is not error', function(assert) {
  const state = {
    investigate: {
      nextGen: {
        pillsData: 'foo'
      }
    }
  };

  const pD = pillsData(state);
  assert.equal(pD, 'foo', 'returns pillsData');
});