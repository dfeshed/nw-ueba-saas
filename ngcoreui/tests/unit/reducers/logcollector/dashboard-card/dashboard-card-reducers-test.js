import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import makePackAction from 'ngcoreui/tests/helpers/make-pack-action';
import reducer from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-reducers';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';
import { PROTOCOL_LIST_PAYLOAD } from './protocol-payload-list-original';
import { PROTOCOL_PAYLOAD_LIST_EXTRACTED } from './protocol-list-extracted';
import { PROTOCOL_DATA_PAYLOAD } from './protocol-data-payload-original';
import { PROTOCOL_DATA_EXTRACTED } from './protocol-data-extracted';

module('Unit | Reducers | dashboardCard', (hooks) => {
  setupTest(hooks);

  test('LOG_COLLECTOR_FETCH_PROTOCOLS gets the available protocols', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOLS,
      payload: PROTOCOL_LIST_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValues = (result.itemKeys).map((x) => x.protocol);
    const expectedValues = PROTOCOL_PAYLOAD_LIST_EXTRACTED.map((x) => x.protocol);

    for (let i = 0; i < actualValues.length; i++) {
      assert.equal(expectedValues[i], actualValues[i], 'correct protocol data was extracted');
    }
  });

  test('LOG_COLLECTOR_FETCH_PROTOCOL_DATA gets the available protocols', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_FETCH_PROTOCOL_DATA,
      payload: PROTOCOL_DATA_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValues = result;
    const expectedValues = PROTOCOL_DATA_EXTRACTED;

    assert.deepEqual(expectedValues, actualValues.itemProtocolData.odbc,
      'correct protocol data was extracted');
  });

});

