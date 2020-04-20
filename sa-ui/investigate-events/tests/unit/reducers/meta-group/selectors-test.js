import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { metaGroups } from 'investigate-events/reducers/investigate/meta-group/selectors';
import DEFAULT_META_GROUPS from '../../../data/subscriptions/meta-group/findAll/data';

module('Unit | Selectors | meta-group');

const state1 = {
  investigate: {
    metaGroup: {
      metaGroups: DEFAULT_META_GROUPS
    }
  }
};

test('metaGroups selects meta groups', function(assert) {
  assert.deepEqual(
    metaGroups(
      Immutable.from(state1)
    ), DEFAULT_META_GROUPS, 'metaGroups selects meta groups');
});
