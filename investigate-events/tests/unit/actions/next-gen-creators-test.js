import { module, test } from 'qunit';
import nextGenCreators from 'investigate-events/actions/next-gen-creators';
import ACTION_TYPES from 'investigate-events/actions/types';

module('Unit | Actions | NextGen Creators', function() {

  test('addNextGenPill action creator returns proper type and payload', function(assert) {
    const action = nextGenCreators.addNextGenPill({
      pillData: 'foo',
      position: 1
    });

    assert.equal(action.type, ACTION_TYPES.ADD_NEXT_GEN_PILL);
    assert.equal(action.payload.pillData, 'foo');
    assert.equal(action.payload.position, 1);
  });
});