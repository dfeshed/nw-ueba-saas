import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import {
  radioButtonValue,
  radioButtonOption
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';
import {
  ENABLED_CONFIG,
  SEND_TEST_LOG_CONFIG
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-settings';

module('Unit | Selectors | policy-wizard/windowsLogPolicy/windowsLog-selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('enabled', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLog')
      .policyWizWinLogEnabled(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'enabled');
    assert.deepEqual(result, expectedState, `should return enabled ${expectedState}`);
  });

  test('sendTestLog', function(assert) {
    const expectedState = false;
    const fullState = new ReduxDataHelper()
      .policyWiz('windowsLog')
      .policyWizWinLogSendTestLog(expectedState)
      .build();
    const result = radioButtonValue(fullState, 'sendTestLog');
    assert.deepEqual(result, expectedState, `should return sendTestLog ${expectedState}`);
  });

  test('radioButtonOption returns the right radio button options based on the id', function(assert) {
    const result1 = radioButtonOption('enabled');
    assert.deepEqual(result1, ENABLED_CONFIG, 'should return ENABLED_CONFIG options for enabled id');
    const result2 = radioButtonOption('sendTestLog');
    assert.deepEqual(result2, SEND_TEST_LOG_CONFIG, 'should return SEND_TEST_LOG_CONFIG options for sendTestLog id');
  });

});
