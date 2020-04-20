import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import makePackAction from 'ngcoreui/tests/helpers/make-pack-action';
import reducer from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-reducers';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';
import { PROTOCOL_LIST_PAYLOAD } from './protocol-payload-list-original';
import { PROTOCOL_PAYLOAD_LIST_EXTRACTED } from './protocol-list-extracted';
import { PROTOCOL_DATA_PAYLOAD, PROTOCOL_STATS_PAYLOAD } from './protocol-data-stats-payload-original';
import { PROTOCOL_DATA_EXTRACTED, PROTOCOL_STATS_EXTRACTED, PROTOCOL_ODBC_STATS } from './protocol-data-stats-extracted';

module('Unit | Reducers | dashboardCard', (hooks) => {
  setupTest(hooks);

  test('LOG_COLLECTOR_FETCH_PROTOCOLS gets the available protocols', (assert) => {
    assert.expect(10);
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

  test('LOG_COLLECTOR_FETCH_PROTOCOL_DATA gets the data of available protocols', (assert) => {
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

  test('LOG_COLLECTOR_EVENT_SOURCES_STATS_DATA gets the correct stats from empty state', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_EVENT_SOURCES_STATS_DATA,
      payload: PROTOCOL_STATS_PAYLOAD
    });

    const state = new ReduxDataHelper()
      .connected()
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValues = result;
    const expectedValues = PROTOCOL_STATS_EXTRACTED;

    assert.deepEqual(expectedValues, actualValues.esStatsData.syslog,
      'correct protocol data was extracted');
  });

  test('LOG_COLLECTOR_EVENT_SOURCES_STATS_DATA gets the correct stats from non-empty state', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.LOG_COLLECTOR_EVENT_SOURCES_STATS_DATA,
      payload: PROTOCOL_STATS_PAYLOAD
    });

    const dict = {};
    dict.odbc = PROTOCOL_ODBC_STATS;
    const state = new ReduxDataHelper()
      .connected()
      .esStatsData(dict)
      .initialStateBeforeApiCall()
      .build().logcollector.dashboardCard;

    const result = reducer(state, action);
    const actualValues = result;
    const expectedValuesSyslog = PROTOCOL_STATS_EXTRACTED;

    assert.deepEqual(expectedValuesSyslog, actualValues.esStatsData.syslog,
      'correct syslog data was extracted');
    assert.deepEqual(PROTOCOL_ODBC_STATS, actualValues.esStatsData.odbc,
      'correct odbc data was extracted');
  });
});

