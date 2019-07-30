import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';
import { PROTOCOL_DATA_EXTRACTED, PROTOCOL_ROW_VALUES, PROTOCOL_ROW_VALUES_EXPECTED } from './protocol-data-stats-extracted';

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
});
