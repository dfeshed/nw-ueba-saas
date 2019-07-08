import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as dashboardCardSelectors from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';
import ReduxDataHelper from 'ngcoreui/tests/helpers/redux-data-helper';

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
});
