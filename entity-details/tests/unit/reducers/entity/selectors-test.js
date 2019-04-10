import { module, test } from 'qunit';
import { entityId, entityType, entityDetails, entityDisplayName, entitySeverity, enityIcon, alertsForEntity, isFollowed, entityScore } from 'entity-details/reducers/entity/selectors';
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
  assert.equal(entityDisplayName(state), 'file_qa_1_101');
  assert.equal(entityScore(state), 220);
  assert.equal(entitySeverity(state), 'high');
  assert.equal(enityIcon(state), 'account-group-5');
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