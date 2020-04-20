import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';
import { PROTOCOL_DATA_EXTRACTED, PROTOCOL_ROW_VALUES, PROTOCOL_ROW_VALUES_2, PROTOCOL_ROW_VALUES_EXPECTED } from './protocol-data-stats-extracted';

module('Unit | Selectors | dashboardCard', (hooks) => {
  setupTest(hooks);

  test('areProtocolsLoading picking up wait status', (assert) => {
    const state = new ReduxDataHelper()
      .protocolLoadingStatus('wait')
      .build();
    assert.deepEqual(dashboardCardSelectors.areProtocolsLoading(state), true);
  });

  test('areProtocolsLoading picking up complete status', (assert) => {
    const state = new ReduxDataHelper()
      .protocolLoadingStatus('complete')
      .build();
    assert.deepEqual(dashboardCardSelectors.areProtocolsLoading(state), false);
  });

  test('areProtocolsLoading picking up error status', (assert) => {
    const state = new ReduxDataHelper()
      .protocolLoadingStatus('err')
      .build();
    assert.deepEqual(dashboardCardSelectors.areProtocolsLoading(state), false);
  });

  test('protocolArray returning the correct list of items', (assert) => {
    const arr = [{ protocol: 'file' }, { protocol: 'checkpoint' }, { protocol: 'syslog' }];
    const state = new ReduxDataHelper()
      .protocolListItems(arr)
      .build();
    assert.deepEqual(dashboardCardSelectors.protocolArray(state), arr);
  });

  test('isProtocolDataLoadingSuccess picking up complete status', (assert) => {
    const state = new ReduxDataHelper()
      .protocolDataLoadingStatus('complete')
      .build();
    assert.deepEqual(dashboardCardSelectors.isProtocolDataLoadingSuccess(state), true);
  });

  test('getProtocolData picking up correct items', (assert) => {
    const dict = {};
    dict.odbc = PROTOCOL_DATA_EXTRACTED;
    const state = new ReduxDataHelper()
      .protocolData(dict)
      .build();
    assert.deepEqual(dashboardCardSelectors.getProtocolData(state), [dict.odbc]);
  });

  test('addHeaderRow returning an extra header row', (assert) => {
    const protocolRowValues = PROTOCOL_ROW_VALUES;
    const expectedValues = PROTOCOL_ROW_VALUES_EXPECTED;
    assert.deepEqual(dashboardCardSelectors.addHeaderRow(protocolRowValues), expectedValues,
      'Correct calculation is done while calculating headers');
  });

  test('getLogCollectorData returns the correct protocol rows', (assert) => {
    const state = new ReduxDataHelper()
      .protocolDataLoadingStatus('complete')
      .protocolData(PROTOCOL_ROW_VALUES_2)
      .build();
    assert.deepEqual(dashboardCardSelectors.getLogCollectorData(state), PROTOCOL_ROW_VALUES_EXPECTED,
      'Correct protocol rows with header should be returned');
  });

  test('getLogCollectorData returns empty rows on empty data', (assert) => {
    const expectedValues = [];
    const state = new ReduxDataHelper()
      .protocolDataLoadingStatus('complete')
      .build();
    assert.deepEqual(dashboardCardSelectors.getLogCollectorData(state), expectedValues,
      'Correct protocol rows without header should be returned');
  });

  test('getLogCollectorTotalEventRate returns the correct sum', (assert) => {
    const state = new ReduxDataHelper()
      .protocolData(PROTOCOL_ROW_VALUES_2)
      .build();
    assert.deepEqual(dashboardCardSelectors.getLogCollectorTotalEventRate(state), '100',
      'Correct sum should be generated');
  });

  test('getPointsArray returning an 2-d points given a list of numbers', (assert) => {
    const numList = [ '1', '3', '2,000', '210'];
    const points = [ { x: 0, y: 1 }, { x: 1, y: 3 }, { x: 2, y: 2000 }, { x: 3, y: 210 } ];
    assert.deepEqual(dashboardCardSelectors.getPointsArray(numList), points,
      'Correct points array should be generated');
  });

  test('getTCPRate getting the correct value', (assert) => {
    const value = '23';
    const state = new ReduxDataHelper()
      .setTcpRateValue(value)
      .build();
    assert.deepEqual(dashboardCardSelectors.getTCPRate(state), value);
  });

  test('getTCPTid getting the correct value', (assert) => {
    const tid = '11';
    const state = new ReduxDataHelper()
      .setTcpRateTid(tid)
      .build();
    assert.deepEqual(dashboardCardSelectors.getTCPTid(state), tid);
  });

});
