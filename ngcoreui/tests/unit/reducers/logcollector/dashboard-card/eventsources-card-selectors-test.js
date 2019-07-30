import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as esCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/eventsources-card-selectors';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';
import { PROTOCOL_DATA_EXTRACTED, PROTOCOL_STATS_ROW_VALUES, PROTOCOL_STATS_VALUES_EXPECTED } from './protocol-data-stats-extracted';

module('Unit | Selectors | eventsources-card', (hooks) => {
  setupTest(hooks);

  test('isEsStatsDataLoadingSuccess picking up complete status', (assert) => {
    const state = new ReduxDataHelper()
      .esStatsDataLoadingStatus('complete')
      .build();
    assert.deepEqual(esCardSelectors.isEsStatsDataLoadingSuccess(state), true);
  });

  test('isEsStatsDataLoadingSuccess picking up wait status', (assert) => {
    const state = new ReduxDataHelper()
      .esStatsDataLoadingStatus('wait')
      .build();
    assert.deepEqual(esCardSelectors.isEsStatsDataLoadingSuccess(state), false);
  });

  test('isEsStatsDataLoadingSuccess picking up error status', (assert) => {
    const state = new ReduxDataHelper()
      .esStatsDataLoadingStatus('error')
      .build();
    assert.deepEqual(esCardSelectors.isEsStatsDataLoadingSuccess(state), false);
  });

  test('getProtocolData picking up correct items', (assert) => {
    const dict = {};
    dict.odbc = PROTOCOL_DATA_EXTRACTED;
    const state = new ReduxDataHelper()
      .esStatsData(dict)
      .build();
    assert.deepEqual(esCardSelectors.getEsStatsData(state), [dict.odbc]);
  });

  test('addHeaderRow returning an extra header row', (assert) => {
    const protocolRowValues = PROTOCOL_STATS_ROW_VALUES;
    const expectedValues = PROTOCOL_STATS_VALUES_EXPECTED;
    assert.deepEqual(esCardSelectors.addHeaderRow(protocolRowValues), expectedValues,
      'Correct calculation is done while calculating headers');
  });
});
