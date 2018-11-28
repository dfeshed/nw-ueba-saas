import { module, test } from 'qunit';
import { entityId, entityType, entityDetails, alertsForEntity } from 'entity-details/reducers/entity/selectors';
import details from '../../../data/presidio/user_details';

module('Unit | Selector | Entity Selector');

const state = {
  entity: {
    entityId: 'user-1',
    entityType: 'user',
    entityDetails: details.data[0]
  }
};

test('test entity state', function(assert) {
  assert.equal(entityId(state), 'user-1');
  assert.equal(entityType(state), 'user');
  assert.deepEqual(entityDetails(state), details.data[0]);
  assert.deepEqual(alertsForEntity(state), details.data[0].alerts);
});