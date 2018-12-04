import { module, test } from 'qunit';
import { entityId, entityType, entityDetails, alertsForEntity, isFollowed } from 'entity-details/reducers/entity/selectors';
import details from '../../../data/presidio/user_details';
import Immutable from 'seamless-immutable';

module('Unit | Selector | Entity Selector');

const state = Immutable.from({
  entity: {
    entityId: 'user-1',
    entityType: 'user',
    entityDetails: details.data[0]
  }
});

test('test entity state for entityID and type', function(assert) {
  assert.equal(entityId(state), 'user-1');
  assert.equal(entityType(state), 'user');
});

test('test entity state for entity details', function(assert) {
  assert.deepEqual(entityDetails(state), details.data[0]);
});


test('test entity state for alerts for given entity', function(assert) {
  assert.deepEqual(alertsForEntity(state), details.data[0].alerts);
});

test('test entity is followed or not', function(assert) {
  assert.equal(isFollowed(state), false);
  const newState = state.setIn(['entity', 'entityDetails', 'followed'], true);
  assert.equal(isFollowed(newState), true);
});