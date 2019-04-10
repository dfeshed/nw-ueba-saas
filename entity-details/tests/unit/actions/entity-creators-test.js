import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import entityDetails from '../../data/presidio/user_details';
import { patchFetch } from '../../helpers/patch-fetch';
import { initializeEntityDetails, followUser, unfollowUser } from 'entity-details/actions/entity-creators';
import dataIndex from '../../data/presidio';
import userAlerts from '../../data/presidio/user_alerts';
import indicatorEvents from '../../data/presidio/indicator-events';
import indicatorCount from '../../data/presidio/indicator-count';

const state = {
  entity: {
    entityId: 'user-1',
    entityType: 'user'
  },
  alerts: {
    alertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
    alerts: userAlerts.data
  },
  indicators: {
    indicatorId: '8614aa7f-c8ee-4824-9eaf-e0bb199cd006',
    events: indicatorEvents.data,
    historicalData: indicatorCount.data,
    eventFilter: {
      page: 1,
      size: 100,
      sort_direction: 'DESC'
    }
  }
};

module('Unit | Actions | Entity-creators Actions', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
  });

  test('it can intialize entity', (assert) => {
    assert.expect(3);
    const types = ['ENTITY_DETAILS::GET_ENTITY_DETAILS', 'ENTITY_DETAILS::INITIATE_ENTITY'];
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.ok(types.includes(type));
        if (payload && payload.displayName) {
          assert.deepEqual(payload, entityDetails.data[0]);
        }
      }
    };
    initializeEntityDetails({ entityId: '123' })(dispatch);
  });

  test('it can intialize entity if alert id is not passed', (assert) => {
    assert.expect(5);
    const types = ['ENTITY_DETAILS::GET_ENTITY_DETAILS', 'ENTITY_DETAILS::INITIATE_ENTITY'];
    const dispatch = (obj) => {
      if (obj.type) {
        assert.ok(types.includes(obj.type));
      }
      if (obj.payload && obj.payload.displayName) {
        assert.deepEqual(obj.payload, entityDetails.data[0]);
      }
      if (typeof obj === 'function') {
        obj(({ type, payload }) => {
          if (payload) {
            assert.equal('ENTITY_DETAILS::INITIATE_ALERT', type);
            assert.equal(payload, entityDetails.data[0].alerts[0].id);
          }
        });
      }
    };
    const getState = () => state;
    initializeEntityDetails({ entityId: '123' })(dispatch, getState);
  });

  test('it can intialize entity and alert if alertId is given', (assert) => {
    assert.expect(5);
    const types = ['ENTITY_DETAILS::GET_ENTITY_DETAILS', 'ENTITY_DETAILS::INITIATE_ENTITY'];
    const dispatch = (obj) => {
      if (obj.type) {
        assert.ok(types.includes(obj.type));
      }
      if (obj.payload && obj.payload.displayName) {
        assert.deepEqual(obj.payload, entityDetails.data[0]);
      }
      if (typeof obj === 'function') {
        obj(({ type, payload }) => {
          if (payload) {
            assert.equal('ENTITY_DETAILS::INITIATE_ALERT', type);
            assert.equal(payload, '234');
          }
        });
      }
    };
    initializeEntityDetails({ entityId: '123', alertId: '234' })(dispatch);
  });

  test('it can intialize entity, alert and indicator if indicator id is given', (assert) => {
    assert.expect(5);
    const types = ['ENTITY_DETAILS::GET_ENTITY_DETAILS', 'ENTITY_DETAILS::INITIATE_ENTITY'];
    const dispatch = (obj) => {
      if (obj.type) {
        assert.ok(types.includes(obj.type));
      }
      if (obj.payload && obj.payload.displayName) {
        assert.deepEqual(obj.payload, entityDetails.data[0]);
      }
      if (typeof obj === 'function') {
        obj(({ type, payload }) => {
          if (type === 'ENTITY_DETAILS::INITIATE_ALERT') {
            assert.equal(payload, '234');
          } else if (type === 'ENTITY_DETAILS::INITIATE_INDICATOR') {
            assert.equal(payload, 'inc-1');
          }
        });
      }
    };
    initializeEntityDetails({ entityId: '123', alertId: '234', indicatorId: 'inc-1' })(dispatch);
  });

  test('it can follow user', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.equal('ENTITY_DETAILS::UPDATE_FOLLOW', type);
        assert.equal(payload, true);
      }
    };
    const getState = () => state;
    followUser()(dispatch, getState);
  });

  test('it can unfollow user', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.equal('ENTITY_DETAILS::UPDATE_FOLLOW', type);
        assert.equal(payload, false);
      }
    };
    const getState = () => state;
    unfollowUser()(dispatch, getState);
  });
});