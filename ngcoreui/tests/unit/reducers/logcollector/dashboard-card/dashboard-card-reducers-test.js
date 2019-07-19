import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import { PROTOCOL_PAYLOAD } from './protocol-payload-original';
import { PROTOCOL_PAYLOAD_EXTRACTED } from './protocol-data-extracted';
import { PROTOCOL_VALUE_PAYLOAD } from './protocol-value-payload-original';
import makePackAction from 'ngcoreui/tests/helpers/make-pack-action';
import reducer from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-reducers';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';

module('Unit | Reducers | dashboardCard', (hooks) => {
  setupTest(hooks);

  test('LOG_COLLECTOR_FETCH_PROTOCOLS gets the available protocols', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS,
      payload: PROTOCOL_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValues = (result.itemKeys).map((x) => x.protocol);
    const expectedValues = PROTOCOL_PAYLOAD_EXTRACTED.map((x) => x.protocol);

    for (let i = 0; i < actualValues.length; i++) {
      assert.equal(expectedValues[i], actualValues[i], 'correct protocol data was extracted');
    }
  });

  test('LOG_COLLECTOR_FETCH_EVENT_RATE gets the event rate of the desired protocol', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_EVENT_RATE,
      payload: PROTOCOL_VALUE_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValue = result.itemValueEventRate;
    const protocol = 'file';
    assert.equal(actualValue[protocol], '0', 'correct protocol and value was extracted');
  });

  test('LOG_COLLECTOR_FETCH_BYTE_RATE gets the byte rate of the desired protocol', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_BYTE_RATE,
      payload: PROTOCOL_VALUE_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValue = result.itemValueByteRate;
    const protocol = 'file';
    assert.equal(actualValue[protocol], '0', 'correct protocol and value was extracted');
  });

  test('LOG_COLLECTOR_FETCH_ERROR_RATE gets the error rate of the desired protocol', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_ERROR_RATE,
      payload: PROTOCOL_VALUE_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValue = result.itemValueErrorRate;
    const protocol = 'file';
    assert.equal(actualValue[protocol], '0', 'correct protocol and value was extracted');
  });

  test('LOG_COLLECTOR_FETCH_TOTAL_EVENTS gets the number of events of the desired protocol', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_EVENTS,
      payload: PROTOCOL_VALUE_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValue = result.itemValueNumEvents;
    const protocol = 'file';
    assert.equal(actualValue[protocol], '0', 'correct protocol and value was extracted');
  });

  test('LOG_COLLECTOR_FETCH_TOTAL_BYTES gets the number of bytes of the desired protocol', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_BYTES,
      payload: PROTOCOL_VALUE_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValue = result.itemValueNumBytes;
    const protocol = 'file';
    assert.equal(actualValue[protocol], '0', 'correct protocol and value was extracted');
  });

  test('LOG_COLLECTOR_FETCH_TOTAL_ERRORS gets the number of errors of the desired protocol', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_TOTAL_ERRORS,
      payload: PROTOCOL_VALUE_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValue = result.itemValueNumErrors;
    const protocol = 'file';
    assert.equal(actualValue[protocol], '0', 'correct protocol and value was extracted');
  });
});

