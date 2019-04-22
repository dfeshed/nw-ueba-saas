import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { patchFetch } from '../../helpers/patch-fetch';
import { initializeAlert, alertIsNotARisk, resetAlerts, selectAlert, updateSort } from 'entity-details/actions/alert-details';
import dataIndex from '../../data/presidio';
import userAlerts from '../../data/presidio/user_alerts';
import indicatorEvents from '../../data/presidio/indicator-events';
import indicatorCount from '../../data/presidio/indicator-count';
import { patchFlash } from '../../helpers/patch-flash';

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

module('Unit | Actions | alert-details Actions', (hooks) => {
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

  test('it can intialize Alert', (assert) => {
    assert.expect(3);
    const types = ['ENTITY_DETAILS::INITIATE_ALERT', 'ENTITY_DETAILS::GET_ALERTS'];
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.ok(types.includes(type));
        if (payload && payload != '123') {
          assert.deepEqual(payload.length, userAlerts.data.length);
        }
      }
    };
    const getState = () => state;
    initializeAlert('123')(dispatch, getState);
  });

  test('it intialize Alert for error cases', (assert) => {
    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject({
          ok: true,
          error: 'some error'
        });
      });
    });
    assert.expect(2);
    const types = ['ENTITY_DETAILS::INITIATE_ALERT', 'ENTITY_DETAILS::ALERT_ERROR'];
    const dispatch = ({ type }) => {
      if (type) {
        assert.ok(types.includes(type));
      }
    };
    const getState = () => state;
    initializeAlert('123')(dispatch, getState);
  });

  test('it can not a risk', (assert) => {
    assert.expect(3);
    const dispatch = (obj) => {
      if (typeof obj === 'function') {
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
        obj(() => {
          // Due to async just checking that dispatch is called.
          assert.ok(true);
        });
      }
    };
    const getState = () => state;
    alertIsNotARisk({ entityId: '123' })(dispatch, getState);
  });


  test('it should show flash message if not a risk fails', (assert) => {
    const done = assert.async();
    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject({
          ok: true,
          error: 'some error'
        });
      });
    });
    const getState = () => state;
    alertIsNotARisk({ entityId: '123' })(null, getState);

    patchFlash((flash) => {
      assert.equal(flash.type, 'danger');
      done();
    });
  });

  test('it can updateSort for alert', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'ENTITY_DETAILS::UPDATE_SORT');
      assert.equal(payload, 'NAME');
    };
    dispatch(updateSort('NAME'));
  });

  test('it can reset alert', (assert) => {
    assert.expect(1);
    const dispatch = ({ type }) => {
      assert.equal(type, 'ENTITY_DETAILS::RESET_ALERT');
    };
    dispatch(resetAlerts());
  });

  test('it can set selected alert', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'ENTITY_DETAILS::SELECT_ALERT');
      assert.equal(payload, 'Alert-1');
    };
    dispatch(selectAlert('Alert-1'));
  });

});