import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'entity-details/reducers/entity/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'entity-details/actions/types';
import entityDetails from '../../../data/presidio/user_details';

module('Unit | Reducers | Entity Reducer', (hooks) => {
  setupTest(hooks);

  test('test init entity', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ENTITY,
      payload: { entityId: 123, entityType: 'user' }
    });

    assert.equal(result.entityId, 123);
    assert.equal(result.entityType, 'user');
  });

  test('test reset entity', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ENTITY,
      payload: { entityId: 123, entityType: 'user' }
    });

    assert.equal(result.entityId, 123);
    assert.equal(result.entityType, 'user');

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESET_ENTITY
    });


    assert.equal(result.entityId, null);
  });

  test('test init entity with details', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_ENTITY_DETAILS,
      payload: entityDetails.data[0]
    });

    assert.deepEqual(result.entityDetails, entityDetails.data[0]);
  });

  test('test enity error ', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ENTITY,
      payload: { entityId: 123, entityType: 'user' }
    });

    assert.equal(result.entityId, 123);
    assert.equal(result.entityType, 'user');
    assert.equal(result.entityFetchError, false);

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.ENTITY_ERROR
    });

    assert.equal(result.entityFetchError, true);
  });

  test('test update follow', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_ENTITY_DETAILS,
      payload: entityDetails.data[0]
    });

    assert.deepEqual(result.entityDetails, entityDetails.data[0]);

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.UPDATE_FOLLOW,
      payload: false
    });

    assert.deepEqual(result.entityDetails.followed, false);

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.UPDATE_FOLLOW,
      payload: true
    });

    assert.deepEqual(result.entityDetails.followed, true);
  });
});