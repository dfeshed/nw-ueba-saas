import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import { PROTOCOL_PAYLOAD } from './protocol-payload-original';
import { PROTOCOL_PAYLOAD_EXTRACTED } from './protocol-data-extracted';
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
    const actualValues = (result.items).map((x) => x.protocol);
    const expectedValues = PROTOCOL_PAYLOAD_EXTRACTED.map((x) => x.protocol);

    for (let i = 0; i < actualValues.length; i++) {
      assert.equal(expectedValues[i], actualValues[i], 'correct protocol data was extracted');
    }
  });
});

