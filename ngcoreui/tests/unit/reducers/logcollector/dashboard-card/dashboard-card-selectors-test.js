import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';
import { PROTOCOL_DATA_ROWS } from './protocol-data-row-extracted';

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

  test('buildProtocolRow returning the correct set of items on similar data', (assert) => {
    const state = new ReduxDataHelper()
      .protocolSameRowData()
      .build();
    const expectedObj = [{ protocol: 'file', eventRate: '10', byteRate: '11', errorRate: '12',
      numOfEvents: '13', numOfBytes: '14', errorCount: '15' }];
    assert.deepEqual(dashboardCardSelectors.buildProtocolRow(state), expectedObj);
  });

  test('buildProtocolRow returning the correct set of items on dissimilar data', (assert) => {
    const state = new ReduxDataHelper()
      .protocolDifferentRowData()
      .build();
    const expectedObjs = PROTOCOL_DATA_ROWS;
    assert.deepEqual(dashboardCardSelectors.buildProtocolRow(state), expectedObjs);
  });
});
