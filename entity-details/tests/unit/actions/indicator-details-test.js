import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { patchFetch } from '../../helpers/patch-fetch';
import { initializeIndicator, getEvents, getHistoricalData } from 'entity-details/actions/indicator-details';
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
    selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
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

module('Unit | Actions | indicator-details Actions', (hooks) => {
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

  test('it can getEvents', (assert) => {
    assert.expect(2);
    const types = ['ENTITY_DETAILS::INITIATE_INDICATOR', 'ENTITY_DETAILS::GET_INDICATOR_EVENTS', 'ENTITY_DETAILS::GET_INDICATOR_HISTORICAL_DATA'];
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.ok(types.includes(type));
        if (payload && payload != '123') {
          assert.deepEqual(payload.length, indicatorEvents.data.length);
        }
      }
    };
    const getState = () => state;
    getEvents('123')(dispatch, getState);
  });

  test('it can initializeIndicator', (assert) => {
    assert.expect(2);
    const types = ['ENTITY_DETAILS::INITIATE_INDICATOR', 'ENTITY_DETAILS::GET_INDICATOR_EVENTS', 'ENTITY_DETAILS::GET_INDICATOR_HISTORICAL_DATA'];
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.ok(types.includes(type));
        if (payload && payload.entityId) {
          assert.equal(payload.entityId, '123');
        }
      }
    };
    const getState = () => state;
    initializeIndicator({ entityId: '123' })(dispatch, getState);
  });

  test('it can getHistoricalData', (assert) => {
    assert.expect(2);
    const types = ['ENTITY_DETAILS::INITIATE_INDICATOR', 'ENTITY_DETAILS::GET_INDICATOR_EVENTS', 'ENTITY_DETAILS::GET_INDICATOR_HISTORICAL_DATA'];
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.ok(types.includes(type));
        if (payload) {
          assert.equal(payload.length, 78);
        }
      }
    };
    const getState = () => state;
    getHistoricalData({ entityId: '123' })(dispatch, getState);
  });

});